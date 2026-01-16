package com.openclassroom.medilabosolutionapplication.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.openclassroom.medilabosolutionapplication.model.Patient;

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
