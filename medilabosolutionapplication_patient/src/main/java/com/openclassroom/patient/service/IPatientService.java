package com.openclassroom.patient.service;

import java.util.Optional;

import com.openclassroom.common.model.PatientDTO;
import com.openclassroom.patient.model.Patient;

/**
 * interface qui liste les actions que le service va proposer : lire, crééer, modifier, supprimer. 
 */
public interface IPatientService {
	
	Patient createPatient(Patient patient);
    Optional<Patient> getPatientByName(String nom, String prenom);
    Optional<Patient> getPatientById(Integer id);
    Patient updatePatient(Patient patient);

    public Patient patientDTOToPatient(PatientDTO patientDTO);
    public PatientDTO patientToPatientDTO(Patient patient);
}
