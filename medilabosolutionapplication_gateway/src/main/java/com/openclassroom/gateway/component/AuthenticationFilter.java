package com.openclassroom.gateway.component;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Base64;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import com.openclassroom.common.model.KeycloakTokenResponseDTO;
import com.openclassroom.gateway.proxies.SecurityServiceClient;

import reactor.core.publisher.Mono;

@Component
public class AuthenticationFilter implements GlobalFilter, Ordered {


	private static final Logger logger = LogManager.getLogger("AuthenticationFilter");

    private static final List<String> PUBLIC_PATHS = List.of("/login", "/auth/login", "/auth/refresh");

    @Autowired
    private SecurityServiceClient securityClientService;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();
        logger.info("filter : " + path);

        if (PUBLIC_PATHS.stream().anyMatch(path::startsWith)) {
            logger.info("public path : ALLOWED");
            return chain.filter(exchange);
        }

        // Priorité 1 : header Authorization (appels Feign machine-à-machine)
        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authHeader != null && !authHeader.isEmpty()) {
            logger.info("private path : token depuis header Authorization");
            //if (!isTokenValid(authHeader)) {
            //	logger.error("TOKEN INVALIDE header !");
            //}
            // Injecter X-Authenticated-User pour InternalAuthFilter
            String username = extractUsernameFromJwt(authHeader.replace("Bearer ", ""));
            logger.info("Token " + authHeader);
            ServerHttpRequest mutated = exchange.getRequest().mutate()
            		.header(HttpHeaders.AUTHORIZATION, authHeader)
                    .header("X-Authenticated-User", username != null ? username : "unknown")
                    .build();
            return chain.filter(exchange.mutate().request(mutated).build());
        }

        // Priorité 2 : cookie jwt_token (navigation navigateur)
        List<HttpCookie> cookies = exchange.getRequest().getCookies().get("jwt_token");
        if (cookies != null && !cookies.isEmpty()) {
            String token = cookies.get(0).getValue();
            logger.info("private path : token depuis cookie jwt_token");
            //if (!isTokenValid(token)) {
            //	logger.error("TOKEN INVALIDE (cookies) !");
            //}
            String username = extractUsernameFromJwt(token);
            // Injecter Authorization + X-Authenticated-User pour les services en aval
            ServerHttpRequest mutated = exchange.getRequest().mutate()
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                    .header("X-Authenticated-User", username != null ? username : "unknown")
                    .build();
            return chain.filter(exchange.mutate().request(mutated).build());
        }

        logger.info("Aucun token trouvé → redirect login");
        return redirectToLogin(exchange);
    }

    // Tente de renouveler le token via le refresh_token stocké dans le cookie.
    // Si le refresh réussit, met à jour les cookies et continue la requête.
    // Sinon, redirige vers login.
    private Mono<Void> tryRefreshToken(ServerWebExchange exchange, GatewayFilterChain chain) {
        List<HttpCookie> refreshCookies = exchange.getRequest().getCookies().get("refresh_token");
        if (refreshCookies == null || refreshCookies.isEmpty()) {
            logger.info("Pas de refresh_token erreurpourrie → redirect login");
            return redirectToLogin(exchange);
        }

        String refreshToken = refreshCookies.get(0).getValue();
        logger.info("Refresh token trouvé → appel /auth/refresh");

        return securityClientService.refreshToken(refreshToken)
            .flatMap(newTokens -> {
                logger.info("Token rafraîchi avec succès");

                // Mettre à jour les cookies sur la réponse
                exchange.getResponse().addCookie(
                    ResponseCookie.from("jwt_token", newTokens.getAccessToken())
                        .httpOnly(true)
                        .path("/")
                        .maxAge(Duration.ofHours(1))
                        .build()
                );
                exchange.getResponse().addCookie(
                    ResponseCookie.from("refresh_token", newTokens.getRefreshToken())
                        .httpOnly(true)
                        .path("/")
                        .maxAge(Duration.ofDays(30))
                        .build()
                );

                // Continuer la requête avec le nouveau token,
                // en passant les nouveaux tokens via headers internes
                // pour que le front puisse mettre à jour sa session
                return continueWithNewTokens(exchange, chain, newTokens);
            })
            .switchIfEmpty(Mono.defer(() -> {
                logger.info("Refresh échoué → redirect login");
                return redirectToLogin(exchange);
            }));
    }

    // Continue la requête après un refresh réussi.
    // Passe les nouveaux tokens en headers internes (X-New-*) pour
    // que le front service puisse mettre à jour sa session HTTP.
    private Mono<Void> continueWithNewTokens(ServerWebExchange exchange, GatewayFilterChain chain,
                                              KeycloakTokenResponseDTO newTokens) {
        String username = extractUsernameFromJwt(newTokens.getAccessToken());
        ServerHttpRequest.Builder requestBuilder = exchange.getRequest().mutate()
            .header("X-New-Access-Token", newTokens.getAccessToken())
            .header("X-New-Refresh-Token", newTokens.getRefreshToken());

        if (username != null) {
            requestBuilder.header("X-Authenticated-User", username);
        }
        return chain.filter(exchange.mutate().request(requestBuilder.build()).build());
    }

    // Continue la requête avec le token fourni (déjà validé).
    private Mono<Void> continueWithToken(ServerWebExchange exchange, GatewayFilterChain chain, String token) {
        String username = extractUsernameFromJwt(token);
        if (username != null) {
            ServerHttpRequest mutatedRequest = exchange.getRequest().mutate()
                .header("X-Authenticated-User", username)
                .build();

            logger.info("Return with username");
            return chain.filter(exchange.mutate().request(mutatedRequest).build());
        }
        logger.info("Return without username");
        return chain.filter(exchange);
    }

    // Décode le payload du JWT (base64url) et extrait preferred_username ou sub.
    // La signature a déjà été vérifiée par le service security.
    // Le paramètre est le token brut (sans préfixe "Bearer ").
    private String extractUsernameFromJwt(String token) {
        logger.info("extractUsernameFromJwt token = " + token);
        try {
            String[] parts = token.split("\\.");
            if (parts.length < 2) return null;

            // Padding base64url si nécessaire
            String payload = parts[1];
            int mod = payload.length() % 4;
            if (mod != 0) payload = payload + "=".repeat(4 - mod);

            String json = new String(Base64.getUrlDecoder().decode(payload), StandardCharsets.UTF_8);
            String username = extractStringClaim(json, "preferred_username");
            if (username == null) username = extractStringClaim(json, "sub");
            logger.info("username = " + username);
            return username;
        } catch (Exception e) {
            return null;
        }
    }

     // Extraction simple d'un claim string depuis le JSON du payload JWT.
     // Evite une dépendance Jackson supplémentaire.
    private String extractStringClaim(String json, String claimName) {
        String key = "\"" + claimName + "\":\"";
        int start = json.indexOf(key);
        if (start == -1) return null;
        start += key.length();
        int end = json.indexOf('"', start);
        if (end == -1) return null;
        return json.substring(start, end);
    }

    private Mono<Void> redirectToLogin(ServerWebExchange exchange) {
        logger.info("redirectToLogin");
        if (!exchange.getResponse().isCommitted()) {
            exchange.getResponse().setStatusCode(HttpStatus.FOUND);
            exchange.getResponse().getHeaders().setLocation(URI.create("/login"));
        }
        return exchange.getResponse().setComplete();
    }

    @Override
    public int getOrder() {
        return -1; // Priorité haute
    }
}

