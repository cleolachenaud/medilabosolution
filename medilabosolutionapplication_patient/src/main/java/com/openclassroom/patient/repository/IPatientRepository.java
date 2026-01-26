package com.openclassroom.patient.repository;

import com.openclassroom.patient.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface IPatientRepository extends JpaRepository<Patient, Integer>{

	/**
	 * pour vérifier si un patient existe deja en bdd
	 * @param nom
	 * @param prenom
	 * @param dateNaissance
	 * @return
	 */
	boolean existsByNomAndPrenomAndDateNaissance(String nom, String prenom, LocalDate dateNaissance);
	
	/**
	 * pour trouver un patient par son nom et prenom
	 * @param nom
	 * @return
	 */
	Optional<Patient>findByNomAndPrenom(String nom, String prenom);
	
}
