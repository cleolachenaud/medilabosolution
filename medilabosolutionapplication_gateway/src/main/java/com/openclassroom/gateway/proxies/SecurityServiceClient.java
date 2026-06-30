package com.openclassroom.gateway.proxies;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.openclassroom.common.model.KeycloakTokenResponseDTO;
import com.openclassroom.common.model.RefreshTokenRequestDTO;

import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;

@Service
public class SecurityServiceClient {
	private final WebClient webClient;
	
	public SecurityServiceClient(WebClient.Builder builder, @Value("${security.service.url}") String securityUrl) {
		HttpClient httpClient = HttpClient.create()
				.responseTimeout(Duration.ofSeconds(3));
		this.webClient = builder
				.baseUrl(securityUrl)
				.clientConnector(new ReactorClientHttpConnector(httpClient))
				.build();
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

	public Mono<KeycloakTokenResponseDTO> refreshToken(String refreshToken) {
		return webClient.post()
			.uri("/auth/refresh")
			.contentType(MediaType.APPLICATION_JSON)
			.bodyValue(new RefreshTokenRequestDTO(refreshToken))
			.retrieve()
			.bodyToMono(KeycloakTokenResponseDTO.class)
			.onErrorResume(e -> Mono.empty());
	}
}

