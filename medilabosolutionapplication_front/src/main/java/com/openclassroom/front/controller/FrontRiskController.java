package com.openclassroom.front.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.openclassroom.common.model.PatientDTO;
import com.openclassroom.common.model.RiskDTO;
import com.openclassroom.front.proxies.IMicroservicePatientsProxy;
import com.openclassroom.front.proxies.IMicroserviceRiskProxy;

import feign.FeignException;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/risk")
public class FrontRiskController {

    @Autowired
    private final IMicroserviceRiskProxy riskProxy;
    @Autowired
    private final IMicroservicePatientsProxy patientProxy ;

    public FrontRiskController(IMicroserviceRiskProxy riskProxy,IMicroservicePatientsProxy patientProxy){
        this.patientProxy = patientProxy;
        this.riskProxy = riskProxy;
    }

    @GetMapping("/patient/{patientId}")
    public String afficherRiskPatient(@PathVariable("patientId") Integer patientId, Model model, HttpSession session) {
        recupererEtAfficherRisk(patientId, model);
        //ResponseEntity<PatientDTO> response = patientProxy.getPatientById(patientId);
        PatientDTO patient = patientProxy.getPatientById(patientId).getBody();
        //PatientDTO patient = (PatientDTO) session.getAttribute("patient");
        System.out.println(patient);
        model.addAttribute("patient", patient);
    	model.addAttribute("showCreateForm", false);
    	model.addAttribute("showEditForm", false);
        System.out.println("/risk/patient/" + patientId + " : " + patient.toString());
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
