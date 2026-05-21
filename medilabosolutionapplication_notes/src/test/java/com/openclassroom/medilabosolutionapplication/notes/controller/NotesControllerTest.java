package com.openclassroom.medilabosolutionapplication.notes.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.openclassroom.common.model.NotesDTO;
import com.openclassroom.medilabosolutionapplication.notes.model.Notes;
import com.openclassroom.medilabosolutionapplication.notes.service.INotesService;

public class NotesControllerTest {

    private MockMvc mockMvc;

    @Mock
    private INotesService notesService;

    @InjectMocks
    private NotesController notesController;

    private ObjectMapper objectMapper = new ObjectMapper();

    private Notes sampleNote;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(notesController).build();

        sampleNote = new Notes();
        sampleNote.setId("note1");
        sampleNote.setPatientId(1);
        sampleNote.setNotes("Test note");
        sampleNote.setDate(Instant.now());
    }

    @Test
    public void testGetNotesByPatientOK() throws Exception {
        when(notesService.findByPatientId(1)).thenReturn(Arrays.asList(sampleNote));

        mockMvc.perform(get("/notes/patient/1"))
                .andExpect(status().isOk())
                //.andExpect(jsonPath("$[0].id").value("note1"))
                //.andExpect(jsonPath("$[0].patientId").value(1))
                //.andExpect(jsonPath("$[0].contenu").value("Test note"))
                ;

        verify(notesService).findByPatientId(1);
    }

    @Test
    public void testGetNotesByPatient_returnsNoContent() throws Exception {
        when(notesService.findByPatientId(2)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/notes/patient/2"))
                .andExpect(status().isNoContent());

        verify(notesService).findByPatientId(2);
    }

    @Test
    public void testCreateNoteForPatientOK() throws Exception {
        NotesDTO noteDTO = new NotesDTO();
        noteDTO.setContenu("Nouvelle note");

        Notes createdNote = new Notes();
        createdNote.setId("newId");
        createdNote.setPatientId(1);
        createdNote.setNotes("Nouvelle note");
        createdNote.setDate(Instant.now());

        when(notesService.createNoteForPatient(eq(1), eq("Nouvelle note"))).thenReturn(createdNote);

        mockMvc.perform(post("/notes/patient/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(noteDTO)))
                .andExpect(status().isCreated())
                //.andExpect(jsonPath("$.id").value("newId"))
                //.andExpect(jsonPath("$.patientId").value(1))
                //.andExpect(jsonPath("$.contenu").value("Nouvelle note"))
                ;

        verify(notesService).createNoteForPatient(eq(1), eq("Nouvelle note"));
    }

    @Test
    public void testCreateNoteForPatientInternalServeurError() throws Exception {
        NotesDTO noteDTO = new NotesDTO();
        noteDTO.setContenu("Erreur note");

        when(notesService.createNoteForPatient(eq(1), eq("Erreur note"))).thenThrow(new RuntimeException("Erreur"));

        mockMvc.perform(post("/notes/patient/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(noteDTO)))
                .andExpect(status().isInternalServerError());

        verify(notesService).createNoteForPatient(eq(1), eq("Erreur note"));
    }
}