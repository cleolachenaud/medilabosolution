package com.openclassroom.medilabosolutionapplication.security.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.openclassroom.common.model.KeycloakTokenResponseDTO;
import com.openclassroom.common.model.LoginRequestDTO;
import com.openclassroom.medilabosolutionapplication.security.service.KeycloakAuthService;

@RestController
@RequestMapping("/auth")
public class SecurityController {

	@Autowired
    private KeycloakAuthService keycloakAuthService;
    
    @GetMapping("/validate")
    public ResponseEntity<Void> validateToken(@RequestHeader(value="Authorization", required=false) String header) {	
    	if (header == null || !header.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    	}
        return ResponseEntity.ok().build();
    }
    
    @PostMapping("/login)")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO loginRequest) {
        try {
            KeycloakTokenResponseDTO token = keycloakAuthService.login(
                loginRequest.getUsername(),
                loginRequest.getPassword()
            );
            return ResponseEntity.ok(token);

        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body("Identifiants invalides");
        }
    }
	/*
    @GetMapping
    public ResponseEntity<String> hello() {
        return ResponseEntity.ok("Hello!");
    }

    @GetMapping("/user")
    @PreAuthorize("hasRole('role_user')")
    public ResponseEntity<String> helloUser() {
        return ResponseEntity.ok("Hello From User!");
    }
    */
}
