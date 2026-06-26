package com.openclassroom.gateway.component;

import java.net.URI;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import com.openclassroom.gateway.proxies.SecurityServiceClient;

import reactor.core.publisher.Mono;

@Component
public class AuthenticationFilter implements GlobalFilter, Ordered {
	private static final List<String> PUBLIC_PATHS = List.of("/login", "/auth/token");

    @Autowired
    private SecurityServiceClient securityClientService;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
    	String path = exchange.getRequest().getURI().getPath();
    	if (PUBLIC_PATHS.stream().anyMatch(path::startsWith)) {
    		return chain.filter(exchange);
    	}
    	
    	String header = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
    	if (header == null || !header.startsWith("Bearer ")) {
    		return redirectToLogin(exchange);
    	}
    	
        
        return securityClientService.isTokenValid(header)
            .flatMap(isValid -> isValid ? chain.filter(exchange) : redirectToLogin(exchange));
    }

    private Mono<Void> redirectToLogin(ServerWebExchange exchange) {
		exchange.getResponse().setStatusCode(HttpStatus.FOUND);
		exchange.getResponse().getHeaders().setLocation(URI.create("/login"));
		return exchange.getResponse().setComplete();
	}

	@Override
    public int getOrder() {
        return -1; // Priorité haute
    }

}
