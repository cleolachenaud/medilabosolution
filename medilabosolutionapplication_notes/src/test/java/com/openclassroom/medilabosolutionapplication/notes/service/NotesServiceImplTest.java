package com.openclassroom.medilabosolutionapplication.notes.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.openclassroom.medilabosolutionapplication.notes.model.Notes;
import com.openclassroom.medilabosolutionapplication.notes.repository.INotesRepository;
import com.openclassroom.medilabosolutionapplication.notes.utils.MessageErreur;

	public class NotesServiceImplTest {

	    @Mock
	    private INotesRepository notesRepository;

	    @InjectMocks
	    private NotesServiceImpl notesService;

	    private Notes note1;
	    private Notes note2;

	    @BeforeEach
	    public void setUp() {
	        MockitoAnnotations.openMocks(this);
	        note1 = new Notes();
	        note1.setId("note1");
	        note1.setPatientId(1);
	        note1.setNotes("Note 1");
	        note1.setDate(Instant.now());

	        note2 = new Notes();
	        note2.setId("note2");
	        note2.setPatientId(1);
	        note2.setNotes("Note 2");
	        note2.setDate(Instant.now());
	    }

	    @Test
	    public void testFindByPatientIdOK() {
	        when(notesRepository.findByPatientIdOrderByDateAsc(1)).thenReturn(Arrays.asList(note1, note2));

	        List<Notes> notes = notesService.findByPatientId(1);

	        assertNotNull(notes);
	        assertEquals(2, notes.size());
	        verify(notesRepository).findByPatientIdOrderByDateAsc(1);
	    }

	    @Test
	    public void testFindNoteByIdOK() {
	        when(notesRepository.findById("note1")).thenReturn(Optional.of(note1));

	        Optional<Notes> result = notesService.findNoteById("note1");

	        assertTrue(result.isPresent());
	        assertEquals("Note 1", result.get().getNotes());
	        verify(notesRepository).findById("note1");
	    }

	    @Test
	    public void testFindNoteByIdNonTROUVE() {
	        when(notesRepository.findById("unknown")).thenReturn(Optional.empty());

	        Optional<Notes> result = notesService.findNoteById("unknown");

	        assertFalse(result.isPresent());
	        verify(notesRepository).findById("unknown");
	    }

	    @Test
	    public void testCreateNotePourPatientOK() {
	        Notes newNote = new Notes();
	        newNote.setPatientId(1);
	        newNote.setNotes("Nouveau contenu");

	        when(notesRepository.insert(any(Notes.class))).thenAnswer(invocation -> {
	            Notes argNote = invocation.getArgument(0);
	            argNote.setId("newId");
	            return argNote;
	        });

	        Notes createdNote = notesService.createNoteForPatient(1, "Nouveau contenu");

	        assertNotNull(createdNote);
	        assertEquals(1, createdNote.getPatientId());
	        assertEquals("Nouveau contenu", createdNote.getNotes());
	        assertNotNull(createdNote.getDate());
	        assertEquals("newId", createdNote.getId());
	        verify(notesRepository).insert(any(Notes.class));
	    }

	    @Test
	    public void testCreateNotePourPatientRepositoryThrowsException() {
	        when(notesRepository.insert(any(Notes.class))).thenThrow(new RuntimeException("DB error"));

	        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
	            notesService.createNoteForPatient(1, "Contenu");
	        });

	        assertTrue(exception.getMessage().contains(MessageErreur.NOTE_EXCEPTION));
	        verify(notesRepository).insert(any(Notes.class));
	    }
}
