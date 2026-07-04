package com.openclassroom.medilabosolutionapplication.security.configuration;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.client.RestTemplate;

import com.openclassroom.medilabosolutionapplication.security.component.JwtConverter;

@Configuration
@EnableWebSecurity(debug = true)
@EnableMethodSecurity
public class SpringSecurityConfig {

	private static final Logger logger = LogManager.getLogger("SpringSecurityConfig");
	
    @Autowired
    private JwtConverter jwtConverter;


    @Value("${spring.websecurity.debug:false}")
    boolean webSecurityDebug;

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.debug(webSecurityDebug);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    	logger.info("logsecurityFilterChain : " + http.toString());
        http
	        .csrf(AbstractHttpConfigurer::disable)
	        .authorizeHttpRequests(
                    (authorize)-> authorize
                    .requestMatchers("/auth/login").permitAll() 
                    .requestMatchers("/auth/refresh").permitAll()
                    .requestMatchers("/login").permitAll()
                    .anyRequest().authenticated()
            )
	        .oauth2ResourceServer(
                (oauth2)-> oauth2.jwt(jwt-> jwt.jwtAuthenticationConverter(jwtConverter)) 
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
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate;
    }
}



