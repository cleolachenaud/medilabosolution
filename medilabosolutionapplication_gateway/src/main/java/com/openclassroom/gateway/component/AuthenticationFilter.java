package com.openclassroom.gateway.component;

import java.net.URI;
import org.springframework.http.HttpCookie;
import org.springframework.http.server.reactive.ServerHttpRequest;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import com.openclassroom.gateway.proxies.SecurityServiceClient;

import reactor.core.publisher.Mono;

@Component
public class AuthenticationFilter implements GlobalFilter, Ordered {

	private static final Logger logger = LogManager.getLogger("AuthenticationFilter");

    private static final List<String> PUBLIC_PATHS = List.of("/login", "/auth/login");

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
        logger.info("Headers: " + exchange.getRequest().getHeaders().toString());

    	logger.info("Cookies : " + exchange.getRequest().getCookies());
        List<HttpCookie> cookies = exchange.getRequest().getCookies().get("jwt_token");
        if (cookies == null || cookies.isEmpty()) {
            return redirectToLogin(exchange);
        }
        final String token = cookies.get(0).getValue();
        logger.info("token getValue"+token);
        logger.info("cookie getName"+cookies.get(0).getName());
       /* String header = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        if (header == null || !header.startsWith("Bearer ")) {
            return redirectToLogin(exchange);
        }
        */
        
         return securityClientService.isTokenValid(token)
         // return securityClientService.isTokenValid(header)
            .flatMap(isValid -> {
                if (!isValid) return redirectToLogin(exchange);
                // Décoder le JWT et injecter le nom d'utilisateur dans un header interne
                
                
                // String username = extractUsernameFromJwt(header);
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
            });
    }

    /**
     * Décode le payload du JWT (base64url) et extrait preferred_username ou sub.
     * La signature a déjà été vérifiée par le service security.
     */
    private String extractUsernameFromJwt(String bearerHeader) {
        logger.info("bearerHeader = " + bearerHeader);
        try {
            String token = bearerHeader.substring(7); // Retire "Bearer "
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

    /**
     * Extraction simple d'un claim string depuis le JSON du payload JWT.
     * Evite une dépendance Jackson supplémentaire.
     */
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
        exchange.getResponse().setStatusCode(HttpStatus.FOUND);
        exchange.getResponse().getHeaders().setLocation(URI.create("/login"));
        logger.info("Exchange = " + exchange.toString());
        return exchange.getResponse().setComplete();
    }

    @Override
    public int getOrder() {
        return -1; // Priorité haute
    }

}
