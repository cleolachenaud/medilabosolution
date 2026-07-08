package com.openclassroom.medilabosolutionapplication_risk.filter;

import java.io.IOException;

import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.openclassroom.medilabosolutionapplication_risk.service.SecurityServiceClientSync;


/**
 * Filtre de sécurité interne.
 * Les requêtes doivent provenir de la gateway (réseau Docker interne) et contenir le header X-Authenticated-User injecté par la gateway après validation du JWT Keycloak.
 */
@Component
public class InternalAuthFilter extends OncePerRequestFilter {
    private static final Logger logger = LogManager.getLogger(InternalAuthFilter.class);

    @Autowired
    SecurityServiceClientSync securityServiceClientSync;
/**
 * vérifie si le header authorisation est présent et si le token est valide avant d'accéder au service
 */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {
        logger.info("doFilterInternal");
        String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authorization == null || authorization.isBlank()) {
            logger.info("authorization is null or blank");
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Accès interdit : header Authorization manquant");
            return;
        }
        if (!securityServiceClientSync.isTokenValid(authorization)) {
            logger.info("Token non valide");
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Accès interdit : header Authorization manquant");
            return;
        }
        logger.info("authorization is present");
        filterChain.doFilter(request, response);
    }
}


