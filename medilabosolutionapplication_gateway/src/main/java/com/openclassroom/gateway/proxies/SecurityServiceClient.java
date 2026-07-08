package com.openclassroom.gateway.proxies;

import java.time.Duration;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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

@Service
public class SecurityServiceClient {

	private static final Logger logger = LogManager.getLogger("SecurityServiceClient");
	
	private final WebClient webClient;
	
	public SecurityServiceClient(WebClient.Builder builder, @Value("${security.service.url}") String securityUrl) {
		logger.info("SecurityServiceClient constructor");
		HttpClient httpClient = HttpClient.create()
				.responseTimeout(Duration.ofSeconds(3));
		this.webClient = builder
				.baseUrl(securityUrl)
				.clientConnector(new ReactorClientHttpConnector(httpClient))
				.build();
	}

	public Mono<Boolean> isTokenValid(String header){
		logger.info("isTokenValid called with header: " + header);
		return
			webClient.get()
			.uri("/auth/validate")
			.header(HttpHeaders.AUTHORIZATION, header)
			.retrieve()
			//.toBodilessEntity()
			.bodyToMono(String.class)
			.map(response -> true)
			.onErrorResume(WebClientResponseException.Unauthorized.class, e -> {
				logger.error("isTokenValid Error 1 : " + e.getMessage());
				return Mono.just(false);
			})
			.onErrorResume(e -> {
				logger.error("isTokenValid Error 2 : " + e.getMessage());
				return Mono.just(false);
			})
			;
	}

	public Mono<KeycloakTokenResponseDTO> refreshToken(String refreshToken) {
		logger.info("refreshToken called with refreshToken: " + refreshToken);
		return webClient.post()
			.uri("/auth/refresh")
			.contentType(MediaType.APPLICATION_JSON)
			.bodyValue(new RefreshTokenRequestDTO(refreshToken))
			.retrieve()
			.bodyToMono(KeycloakTokenResponseDTO.class)
			.onErrorResume(e -> {
			    logger.error("refreshToken failed : " + e.getMessage());
			    return Mono.empty();
			});
	}
}

