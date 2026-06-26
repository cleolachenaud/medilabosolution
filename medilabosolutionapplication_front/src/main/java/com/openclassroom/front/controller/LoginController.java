package com.openclassroom.front.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.openclassroom.common.model.KeycloakTokenResponseDTO;
import com.openclassroom.common.model.LoginRequestDTO;
import com.openclassroom.front.proxies.IMicroserviceSecurityProxy;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/login")
public class LoginController {
	
	@Autowired
	IMicroserviceSecurityProxy securityClient;

	@GetMapping({"", "/"})
	public String loginPage() {
		return "login";
	}
	
	@PostMapping({"", "/"})
	public String login(@RequestParam String username,
			@RequestParam String password,
			HttpSession session,
			RedirectAttributes redirectAttributes) {
		ResponseEntity<KeycloakTokenResponseDTO> response = securityClient.login(new LoginRequestDTO(username, password));
		System.out.println("Token obtenu : " + response.getBody());
		return "login";
	}
}
