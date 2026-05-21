package com.openclassroom.front.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.openclassroom.common.model.NotesDTO;
import com.openclassroom.front.proxies.IMicroserviceNotesProxy;

import feign.FeignException;
import jakarta.servlet.http.HttpSession;


@Controller
@RequestMapping("/notes")
public class FrontNotesController {

    @Autowired
    private final IMicroserviceNotesProxy notesProxy;

    public FrontNotesController(IMicroserviceNotesProxy notesProxy) {
        this.notesProxy = notesProxy;
    }

    @GetMapping("/patient/{patientId}")
    public String afficherNotesPatient(@PathVariable("patientId") Integer patientId, Model model, HttpSession session) {
        session.setAttribute("patientId", patientId);  // Stockage en session
        afficherListeNotes(patientId, model);
        model.addAttribute("showCreateForm", false);
        return "notesPatient";
    }

    @GetMapping("/patient/ajouter")
    public String afficherFormulaireAjoutNote(Model model, HttpSession session) {
        Integer patientId = (Integer) session.getAttribute("patientId"); // permet de stocker l'ID du patient dans la session et d'éviter la concurence
        NotesDTO nouvelleNote = new NotesDTO();
        model.addAttribute("note", nouvelleNote);
        model.addAttribute("showCreateForm", true);
        model.addAttribute("patientId", patientId);
        return "notesPatient";
    }

    @PostMapping("/patient/{patientId}")
    public String ajouterNotePourPatient(@ModelAttribute("note") NotesDTO noteDTO,
                                         Model model, HttpSession session) {
        Integer patientId = (Integer) session.getAttribute("patientId");
        try {
            notesProxy.createNoteForPatient(patientId, noteDTO);
            model.addAttribute("message", "Note ajoutée avec succès");
            model.addAttribute("patientId", patientId);
            afficherListeNotes(patientId, model);
        } catch (Exception e) {
            model.addAttribute("message", "Erreur lors de l’ajout de la note : " + e.getMessage());
        }
        return "notesPatient";
    }

    private void afficherListeNotes(Integer patientId, Model model) {
        try {
            ResponseEntity<List<NotesDTO>> response = notesProxy.getNotesByPatient(patientId);
            List<NotesDTO> notes = response.getBody();
            model.addAttribute("notes", notes);
            model.addAttribute("message", null);
            model.addAttribute("showCreateForm", false);
        } catch (FeignException.NotFound e) {
            model.addAttribute("notes", null);
            model.addAttribute("message", "Notes du patient non trouvées");
        } catch (Exception e) {
            model.addAttribute("notes", null);
            model.addAttribute("message", "Erreur lors de la récupération des notes : " + e.getMessage());
        }
    }
}