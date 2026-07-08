package com.openclassroom.medilabosolutionapplication_risk.component;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import jakarta.servlet.http.HttpServletRequest;

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
        logger.info("FeignTokenInterceptor : " + template.toString());

        // Priorité 1 : header Authorization direct (appels machine-à-machine)
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            logger.info("FeignTokenInterceptor : token depuis header Authorization");
            template.header(HttpHeaders.AUTHORIZATION, authHeader);
            return;
        }

        // Priorité 2 : cookie jwt_token (posé par LoginController après login)
        if (request.getCookies() != null) {
            for (jakarta.servlet.http.Cookie cookie : request.getCookies()) {
                if ("jwt_token".equals(cookie.getName())) {
                    logger.info("FeignTokenInterceptor : token depuis cookie jwt_token");
                    template.header(HttpHeaders.AUTHORIZATION, "Bearer " + cookie.getValue());
                    return;
                }
            }
        }

        logger.warn("FeignTokenInterceptor : aucun token trouvé (ni header, ni cookie)");
    }
}
