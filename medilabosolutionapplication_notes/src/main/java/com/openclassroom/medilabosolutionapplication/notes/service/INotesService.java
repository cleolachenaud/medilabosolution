package com.openclassroom.medilabosolutionapplication.notes.service;

import java.util.List;
import java.util.Optional;

import com.openclassroom.common.model.NotesDTO;
import com.openclassroom.medilabosolutionapplication.notes.model.Notes;

public interface INotesService {
	public Optional<Notes> findNoteById(String id);
	public List<Notes>findByPatientId(Integer patientId);
	public Notes createNoteForPatient(Integer patientId, String contenu);
	NotesDTO notesToNotesDTO(Notes note);
	List<NotesDTO> notesToNotesDTO(List<Notes> note);
	Notes notesDTOToNotes(NotesDTO noteDTO);
	List<Notes> notesDTOToNotes(List<NotesDTO> noteDTO);

}
