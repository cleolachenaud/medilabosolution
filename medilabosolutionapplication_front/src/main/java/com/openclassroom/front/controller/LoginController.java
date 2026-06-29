package com.openclassroom.front.controller;

/**
 * Classe de controller pour la sécurité (mise en place du login)
 */
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.openclassroom.common.model.KeycloakTokenResponseDTO;
import com.openclassroom.common.model.LoginRequestDTO;
import com.openclassroom.front.proxies.IMicroserviceSecurityProxy;

import feign.RequestTemplate;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Cookie;

@Controller
@RequestMapping("/login")
public class LoginController {

	private static final Logger logger = LogManager.getLogger("LoginController");

	@Autowired
	IMicroserviceSecurityProxy securityClient;

	@GetMapping({"", "/"})
	public String loginPage(Model model) {
		logger.info("GET loginPage");
        model.addAttribute("loginRequest", new LoginRequestDTO());
		return "login";
	}
	
	@PostMapping({"", "/"})
	public String login(@ModelAttribute LoginRequestDTO loginRequest,
			HttpSession session,
			RedirectAttributes redirectAttributes,
			HttpServletResponse httpResponse) {
		logger.info("POST loginPage : " + loginRequest.getUsername());
		ResponseEntity<KeycloakTokenResponseDTO> response = securityClient.login(loginRequest);
        if (response.getBody() == null) {
    		logger.info("Aucun token");
            redirectAttributes.addFlashAttribute("erreur", "Identifiants invalides");
            return "login";
        }
		logger.info("Token obtenu : " + response.getBody().getAccessToken());
		session.setAttribute("jwt_token", response.getBody().getAccessToken());
		
		String token = response.getBody().getAccessToken();

		Cookie jwtCookie = new Cookie("jwt_token", token);
		jwtCookie.setHttpOnly(true);        // Empêche l'accès JS côté client
		//jwtCookie.setSecure(true);          // En prod : cookie transmis uniquement via HTTPS
		jwtCookie.setPath("/");             // Accessible pour toutes les routes
		jwtCookie.setMaxAge(60 * 60);      // Durée de vie en secondes (ex 1h)

		httpResponse.addCookie(jwtCookie);
		
		//httpResponse.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + response.getBody().getAccessToken());
        
		logger.info("GoTo accueilAppli");
		//return "redirect:/patients";
		return "redirect:http://localhost:8080/patients";
	}
}
