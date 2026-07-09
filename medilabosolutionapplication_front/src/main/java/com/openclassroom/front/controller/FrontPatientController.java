package com.openclassroom.front.controller;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.openclassroom.common.model.PatientDTO;
import com.openclassroom.front.proxies.IMicroservicePatientsProxy;

import feign.FeignException;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("")
/**
 * Classe de controllerPatient pour le front
 */
public class FrontPatientController {

    private static final Logger logger = LogManager.getLogger("FrontPatientController");

    @Autowired
    // pour FeignClient
    private final IMicroservicePatientsProxy patientProxy ;
    

    public FrontPatientController(IMicroservicePatientsProxy patientProxy){
        this.patientProxy = patientProxy;
    }

    @GetMapping({"", "/", "/patients"})
    public String accueilFormulaire() {
        logger.info("GET /patients → accueilAppli");
        return "accueilAppli";
    }

    @GetMapping("/recherche")
    public String rechercherPatient(@RequestParam("nom") String nom,
                                    @RequestParam("prenom") String prenom,
                                    Model model, HttpSession session) {
        logger.info("GET rechercherPatient");
        PatientDTO patient = null;
        try {
            ResponseEntity<PatientDTO> response = patientProxy.getPatient(nom, prenom);
            patient = response.getBody();
            session.setAttribute("patient", patient);
        } catch (FeignException.NotFound e) {
            patient = new PatientDTO();
            patient.setNom(nom);
            patient.setPrenom(prenom);

            model.addAttribute("message", "Patient non trouvé");
            model.addAttribute("showCreateForm", true);
            model.addAttribute("patient", patient);
            return "accueilAppli";
        } catch (Exception e) {
        	StringWriter sw = new StringWriter();
        	e.printStackTrace(new PrintWriter(sw));
        	String exceptionAsString = sw.toString();
            model.addAttribute("message", "Erreur lors de la recherche : " + e.getMessage() + "\n" + exceptionAsString);
            logger.error("message Erreur lors de la recherche : " + e.getMessage() + "\n" + exceptionAsString);
            return "accueilAppli";
        }

        model.addAttribute("patient", patient);
        model.addAttribute("showCreateForm", false);
        model.addAttribute("showEditForm", false); // pas en mode édition par défaut
        return "accueilAppli";
    }

    @PostMapping("/patients")
    public String creerPatient(@ModelAttribute PatientDTO patientDTO, Model model) {
        ResponseEntity<PatientDTO> response = patientProxy.createPatient(patientDTO);
        PatientDTO createdPatient = response.getBody();
        model.addAttribute("patient", createdPatient);
        model.addAttribute("message", "Patient créé avec succès");
        model.addAttribute("showCreateForm", false);
        model.addAttribute("showEditForm", false);
        return "accueilAppli";
    }

    @GetMapping("/patients/modifier/form")
    public String afficherFormulaireModification(@RequestParam("id") Integer id, Model model) {
        ResponseEntity<PatientDTO> response = patientProxy.getPatientById(id);
        if (response.getStatusCode().is2xxSuccessful()) {
            model.addAttribute("patient", response.getBody());
            logger.info("Patient.dateNaissance : " + response.getBody().getDateNaissance());
            model.addAttribute("showEditForm", true);
            model.addAttribute("showCreateForm", false);
        } else {
            model.addAttribute("message", "Patient introuvable");
            model.addAttribute("showEditForm", false);
            model.addAttribute("showCreateForm", false);
        }
        return "accueilAppli";
    }
    @GetMapping("/patients/{id}")
    // pour revenir sur le patient en cours après avoir été a la page des notes
    public String afficherPatientParId(@PathVariable("id") Integer id, Model model) {
        try {
            ResponseEntity<PatientDTO> response = patientProxy.getPatientById(id);
            model.addAttribute("patient", response.getBody());
            model.addAttribute("showCreateForm", false);
            model.addAttribute("showEditForm", false);
            return "accueilAppli";
        } catch (Exception e) {
            model.addAttribute("message", "Patient introuvable");
            return "accueilAppli";
        }
    }
    @PostMapping("/patients/{id}")
    public String modifierPatient(@PathVariable("id") Integer id, @ModelAttribute PatientDTO patientDTO, Model model) {
        ResponseEntity<PatientDTO> response = patientProxy.updatePatient(id, patientDTO);
        if (response.getStatusCode().is2xxSuccessful()) {
            model.addAttribute("patient", response.getBody());
            model.addAttribute("message", "Patient modifié avec succès");
            model.addAttribute("showEditForm", false);
            model.addAttribute("showCreateForm", false);
        } else {
            model.addAttribute("message", "Erreur lors de la modification");
            model.addAttribute("patient", patientDTO);
            model.addAttribute("showEditForm", true);
            model.addAttribute("showCreateForm", false);
        }
        return "accueilAppli";
    }
}