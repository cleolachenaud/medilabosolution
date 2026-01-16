package com.openclassroom.medilabosolutionapplication.service;

import java.util.List;
import java.util.Optional;

import com.openclassroom.medilabosolutionapplication.model.Patient;

/**
 * interface qui liste les actions que le service va proposer : lire, crééer, modifier, supprimer. 
 */
public interface IPatientService {
	
	Patient createPatient(Patient patient);
    Optional<Patient> getPatientByName(String nom, String prenom);
    Patient updatePatient(Patient patient);
    

}
