package com.openclassroom.front.controller;

/**
 * Classe de controller pour la sécurité (mise en place du login)
 */
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
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

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

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
			RedirectAttributes redirectAttributes,
			HttpServletResponse httpResponse) {
		logger.info("POST loginPage : " + loginRequest.getUsername());
		ResponseEntity<KeycloakTokenResponseDTO> response = securityClient.login(loginRequest);
		if (response.getBody() == null) {
			redirectAttributes.addFlashAttribute("erreur", "Identifiants invalides");
			return "login";
		}
		String token = response.getBody().getAccessToken();
		String refreshToken = response.getBody().getRefreshToken();

		// Cookie HTTP-only : navigateur le retransmet automatiquement, inaccessible au JS
		Cookie jwtCookie = new Cookie("jwt_token", token);
		jwtCookie.setHttpOnly(true);
		jwtCookie.setPath("/");
		jwtCookie.setMaxAge(60 * 60); // 1h
		httpResponse.addCookie(jwtCookie);

		Cookie refreshCookie = new Cookie("refresh_token", refreshToken);
		refreshCookie.setHttpOnly(true);
		refreshCookie.setPath("/");
		refreshCookie.setMaxAge(60 * 60 * 24 * 30); // 30 jours
		httpResponse.addCookie(refreshCookie);

		logger.info("GoTo accueilAppli");
		return "redirect:/patients";
	}
}
