package com.openclassroom.medilabosolutionapplication.notes.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import com.openclassroom.medilabosolutionapplication.notes.model.Notes;

@Repository
public interface INotesRepository extends MongoRepository<Notes, String> {
	/**
	 * méthode pour trouver une note par par son ID unique
	 */
    Optional<Notes> findById(String id);         
    /**
     * méthode pour trouver toutes les notes d'un patient
     */
    List<Notes> findByPatientIdOrderByDateAsc(Integer patientId); 
}
