package com.openclassroom.medilabosolutionapplication.notes.service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.openclassroom.common.model.NotesDTO;
import com.openclassroom.medilabosolutionapplication.notes.model.Notes;
import com.openclassroom.medilabosolutionapplication.notes.repository.INotesRepository;
import com.openclassroom.medilabosolutionapplication.notes.utils.MessageErreur;

@Service
public class NotesServiceImpl implements INotesService{

    private final INotesRepository notesRepository;

    public NotesServiceImpl (INotesRepository notesRepository) {
        this.notesRepository = notesRepository;
    }
	/**
	 * méthode pour trouver toutes les notes d'un patient
	 */
	@Override
	public List<Notes> findByPatientId(Integer patientId) {
		return notesRepository.findByPatientIdOrderByDateAsc(patientId);
	}
	/**
	 * méthode pour trouver une note par par son ID unique
	 */
	@Override
	public Optional<Notes> findNoteById(String id) {
	    return notesRepository.findById(id);
	}
	
	/**
	 * méthode pour créer une note 
	 */
	@Override
	public Notes createNoteForPatient(Integer patientId, String contenu) {
	    try {
	        Notes note = new Notes();
	        note.setPatientId(patientId);
	        note.setDate(Instant.now());
	        note.setNotes(contenu);
	        return notesRepository.insert(note);
	    } catch (Exception e) {
	        throw new RuntimeException(MessageErreur.NOTE_EXCEPTION + patientId, e);
	    }
	    /*utilisation de insert plutôt que save garanti de ne pas pouvoir modifier 
	     une note déjà existante */
	}
	
	/**
	 * permet de convertir un noteDTO (classe common) en un note (model) 
	 */
	@Override
	public Notes notesDTOToNotes(NotesDTO noteDTO) {
		Notes note = new Notes();
		note.setNotes(noteDTO.getContenu());
			if(noteDTO.getDate()!=null) {
				Instant instantDate = Instant.from(noteDTO.getDate());
				note.setDate(instantDate);
			}
		
		return note;
	}
	@Override
	public List<Notes> notesDTOToNotes(List<NotesDTO> notesDTO) {
		List<Notes> notes = new ArrayList<>();
		for (NotesDTO noteDTO : notesDTO) {
			notes.add(this.notesDTOToNotes(noteDTO));
		}
		return notes;
	}
	
	/**
	 * permet de convertir un note(model) en un note DTO (common) pour pouvoir le retourner facilement aux autres microservices
	 */
	@Override
	public NotesDTO notesToNotesDTO(Notes note) {
		NotesDTO noteDTO = new NotesDTO();
		noteDTO.setContenu(note.getNotes());
		if (note.getDate() != null) { // je transforme la date Instant (pratique pour bien enregistrer précisément dans la bdd en localdate, qui est plus lisible pour l'utilisateur
	        LocalDate localDate = note.getDate().atZone(ZoneId.systemDefault()).toLocalDate();
	        noteDTO.setDate(localDate);
	    }
		return noteDTO;
	}
	@Override
	public List<NotesDTO> notesToNotesDTO(List<Notes> notes) {
		List<NotesDTO> notesDTO = new ArrayList<>();
		for (Notes note : notes) {
			notesDTO.add(this.notesToNotesDTO(note));
		}
		return notesDTO;
	}
}