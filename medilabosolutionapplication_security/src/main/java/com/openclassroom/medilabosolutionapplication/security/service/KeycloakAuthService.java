package com.openclassroom.medilabosolutionapplication.security.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.openclassroom.common.model.KeycloakTokenResponseDTO;

@Service
public class KeycloakAuthService {

    private final RestTemplate restTemplate;

    @Value("${keycloak.url.token}")
    private String keycloakUrlToken;
    
    public KeycloakAuthService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public KeycloakTokenResponseDTO login(String username, String password) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "password");
        body.add("client_id", "medilabosolution_client");
        body.add("username", username);
        body.add("password", password);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<KeycloakTokenResponseDTO> response = restTemplate.postForEntity(
            	keycloakUrlToken,
                request,
                KeycloakTokenResponseDTO.class
            );
            return response.getBody();

        } catch (HttpClientErrorException.Unauthorized e) {
            throw new BadCredentialsException("Identifiants invalides");
        } catch (HttpClientErrorException e) {
            throw new RuntimeException("Erreur Keycloak : " + e.getStatusCode());
        }
    }
}