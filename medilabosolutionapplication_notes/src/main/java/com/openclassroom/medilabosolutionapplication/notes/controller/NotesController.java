package com.openclassroom.medilabosolutionapplication.notes.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.openclassroom.common.model.NotesDTO;
import com.openclassroom.medilabosolutionapplication.notes.model.Notes;
import com.openclassroom.medilabosolutionapplication.notes.service.INotesService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/notes")
public class NotesController {

    private final INotesService notesService;

    public NotesController(INotesService notesService) {
        this.notesService = notesService;
    }

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<NotesDTO>> getNotesByPatient(@PathVariable("patientId") Integer patientId) {
        System.out.println("getNotesByPatient : " + patientId);
        List<Notes> notes = notesService.findByPatientId(patientId);
        if (notes.isEmpty()) {
            System.out.println("getNotesByPatient : Empty");
            return ResponseEntity.noContent().build();
        }
        System.out.println("getNotesByPatient : " + notes.toString());
        return ResponseEntity.ok(notesService.notesToNotesDTO(notes));
    }
    
    @PostMapping("/patient/{patientId}")
    public ResponseEntity<NotesDTO> createNoteForPatient(
            @PathVariable("patientId") Integer patientId,
            @Valid @RequestBody NotesDTO noteDTO) {
        try {
            Notes createdNote = notesService.createNoteForPatient(patientId, noteDTO.getContenu());
            return ResponseEntity.status(HttpStatus.CREATED).body(notesService.notesToNotesDTO(createdNote));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
}