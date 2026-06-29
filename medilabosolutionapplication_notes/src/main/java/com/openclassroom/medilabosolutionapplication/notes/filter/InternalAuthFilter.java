package com.openclassroom.medilabosolutionapplication.notes.filter;

import java.io.IOException;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Filtre de sécurité interne.
 * Les requêtes doivent provenir de la gateway (réseau Docker interne) et contenir le header X-Authenticated-User injecté par la gateway après validation du JWT Keycloak.
 */
@Component
public class InternalAuthFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String user = request.getHeader("X-Authenticated-User");
        if (user == null || user.isBlank()) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN,
                    "Accès interdit : header X-Authenticated-User manquant");
            return;
        }
        filterChain.doFilter(request, response);
    }
}

