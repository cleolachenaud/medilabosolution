package com.openclassroom.front.proxies;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;

import com.openclassroom.common.model.KeycloakTokenResponseDTO;
import com.openclassroom.common.model.LoginRequestDTO;


@FeignClient(name = "medilabosolutionapplication-security", url = "${spring.cloud.openfeign.client.security.url}")
public interface IMicroserviceSecurityProxy {

    @PostMapping(value = "/auth/login")
    ResponseEntity<KeycloakTokenResponseDTO> login(LoginRequestDTO loginRequest);
    
    @PostMapping(value = "/auth/validate")
    ResponseEntity<KeycloakTokenResponseDTO> validate(String token);
}
