package com.openclassroom.medilabosolutionapplication.security.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.client.RestTemplate;

import com.openclassroom.medilabosolutionapplication.security.component.JwtConverter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SpringSecurityConfig {

    @Autowired
    private JwtConverter jwtConverter;
    

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
	        .csrf(AbstractHttpConfigurer::disable)
	        .authorizeHttpRequests((authorize)-> authorize.anyRequest().authenticated())
	        .oauth2ResourceServer(
                (oauth2)-> oauth2.jwt(
                        jwt-> jwt.jwtAuthenticationConverter(jwtConverter)
                )
        	)
	        .sessionManagement(
                session-> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
	        );

        return http.build();
    }
    /**
     * nécessaire à l'injection du service dans keycloakAuthService
     * @return
     */
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}



