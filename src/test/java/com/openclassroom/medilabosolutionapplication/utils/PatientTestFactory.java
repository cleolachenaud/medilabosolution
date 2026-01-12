package com.openclassroom.medilabosolutionapplication.utils;

import java.time.LocalDate;

import com.openclassroom.medilabosolutionapplication.model.Patient;
import com.openclassroom.medilabosolutionapplication.util.Genre;

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
