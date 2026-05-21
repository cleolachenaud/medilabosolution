package com.openclassroom.front.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.openclassroom.common.model.RiskDTO;
import com.openclassroom.front.proxies.IMicroserviceRiskProxy;

import feign.FeignException;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/risk")
public class FrontRiskController {

    private final IMicroserviceRiskProxy riskProxy;

    @Autowired
    public FrontRiskController(IMicroserviceRiskProxy riskProxy) {
        this.riskProxy = riskProxy;
    }

    @GetMapping("/patient/{patientId}")
    public String afficherRiskPatient(@PathVariable("patientId") Integer patientId, Model model, HttpSession session) {
        session.setAttribute("patientId", patientId);  // stocker patientId en session si besoin
        recupererEtAfficherRisk(patientId, model);
        return "accueilAppli";  
    }

    private void recupererEtAfficherRisk(Integer patientId, Model model) {
        try {
            ResponseEntity<RiskDTO> response = riskProxy.getRiskByPatient(patientId);
            RiskDTO risk = response.getBody();
            model.addAttribute("risk", risk);
            model.addAttribute("message", null);
        } catch (FeignException.NotFound e) {
            model.addAttribute("risk", null);
            model.addAttribute("message", "Risque du patient non trouvé");
        } catch (Exception e) {
            model.addAttribute("risk", null);
            model.addAttribute("message", "Erreur lors de la récupération du risque : " + e.getMessage());
        }
    }
}
