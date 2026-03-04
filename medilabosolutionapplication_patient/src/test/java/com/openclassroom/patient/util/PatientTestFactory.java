package com.openclassroom.patient.util;

import java.time.LocalDate;

import com.openclassroom.common.model.PatientDTO;
import com.openclassroom.common.util.Genre;
import com.openclassroom.patient.model.Patient;


public class PatientTestFactory {
	
	/**
	 * méthode pour créer un patient de test
	 * @return
	 */
	public static Patient creationPatient(String nom, String prenom, Genre genre) {
		Patient patient = new Patient();
		patient.setId(1);
	    patient.setNom(nom);
	    patient.setPrenom(prenom);
	    patient.setGenre(genre);
	    patient.setDateNaissance(LocalDate.of(1980, 1, 1));
		return patient;
	}

	/**
	 * méthode pour créer un patient de test
	 * @return
	 */
	public static PatientDTO creationPatientDTO(String nom, String prenom, Genre genre) {
		PatientDTO patientDTO = new PatientDTO();
		patientDTO.setId(1);
	    patientDTO.setNom(nom);
	    patientDTO.setPrenom(prenom);
	    patientDTO.setGenre(genre);
	    patientDTO.setDateNaissance(LocalDate.of(1980, 1, 1));
		return patientDTO;
	}
	/**
	 * méthode pour créer un patient sans Id
	 * @return
	 */
	public static Patient creationPatientSansId(String nom, String prenom, Genre genre) {
		Patient patient = new Patient();
	    patient.setNom(nom);
	    patient.setPrenom(prenom);
	    patient.setGenre(genre);
	    patient.setDateNaissance(LocalDate.of(1980, 1, 1));
		return patient;
	}
}
