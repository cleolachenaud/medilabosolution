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

        // Si la gateway a rafraîchi le token, les nouveaux tokens sont passés en headers internes
        String newAccessToken = request.getHeader("X-New-Access-Token");
        if (newAccessToken != null) {
            logger.info("Nouveau token détecté via X-New-Access-Token → mise à jour de la session");
            if (session != null) {
                session.setAttribute("jwt_token", newAccessToken);
                String newRefreshToken = request.getHeader("X-New-Refresh-Token");
                if (newRefreshToken != null) {
                    session.setAttribute("refresh_token", newRefreshToken);
                }
            }
            template.header(HttpHeaders.AUTHORIZATION, "Bearer " + newAccessToken);
            return;
        }

        if (session != null) {
            String token = (String) session.getAttribute("jwt_token"); // le jwt est stocké dans la session
            if (token != null) {
                template.header(HttpHeaders.AUTHORIZATION, "Bearer " + token);
            }
        }
    }
}
