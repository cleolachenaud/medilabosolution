package com.openclassroom.front.component;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

@Component
public class FeignTokenInterceptor implements RequestInterceptor {
	private static final Logger logger = LogManager.getLogger("FeignTokenInterceptor");
	
	// "RequestInterceptor" indique à Feign d' executer cette classe avant chaque requête HTTP envoyée par Feign
    private final HttpServletRequest request;

    public FeignTokenInterceptor(HttpServletRequest request) {
        this.request = request;
    }

    @Override
    public void apply(RequestTemplate template) {
        HttpSession session = request.getSession(false);
    	logger.info("FeignTokenInterceptor");
        if (session != null) {
            String token = (String) session.getAttribute("jwt_token"); // le jwt est stocké dans la session
            if (token != null) {
                template.header(HttpHeaders.AUTHORIZATION, "Bearer " + token);
            }
        }
    }
}
