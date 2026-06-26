package com.openclassroom.gateway.proxies;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import reactor.core.publisher.Mono;

@Service
public class SecurityServiceClient {
	private final WebClient webClient;
	
	public SecurityServiceClient(WebClient.Builder builder, @Value("${security.service.url}") String securityUrl) {
		this.webClient = builder.baseUrl(securityUrl).build();
	}

	public Mono<Boolean> isTokenValid(String header){
		return
			webClient.get()
			.uri("/auth/validate")
			.header(HttpHeaders.AUTHORIZATION, header)
			.retrieve()
			.toBodilessEntity()
			.map(response -> true)
			.onErrorResume(WebClientResponseException.Unauthorized.class, e -> Mono.just(false))
			.onErrorResume(e -> Mono.just(false));
	}
}
