package com.openclassroom.medilabosolutionapplication_risk.service;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class SecurityServiceClientSync {
	
    private static final Logger logger = LogManager.getLogger(SecurityServiceClientSync.class);

    private final WebClient webClient;

    public SecurityServiceClientSync(WebClient.Builder builder,
                                     @Value("${security.service.url}") String securityUrl) {
        this.webClient = builder.baseUrl(securityUrl).build();
    }
    /**
     * juste la pour transmettre le token à la sécurité et retourner la réponse au microservice (true ou false) 
     * @param authorizationHeader
     * @return
     */
    public boolean isTokenValid(String authorizationHeader) {
    	logger.info("isTokenValid ? ");
        try {
            return webClient.get()
                    .uri("/auth/validate")
                    .header(HttpHeaders.AUTHORIZATION, authorizationHeader)
                    .retrieve()
                    .bodyToMono(String.class)
                    .map(response -> true)
                    .onErrorReturn(false)
                    .block();
        } catch (Exception e) {
        	logger.info("Exception : " + e);
            return false;
        }
    }
}
