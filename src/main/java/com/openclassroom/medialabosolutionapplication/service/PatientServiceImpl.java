package com.openclassroom.medialabosolutionapplication.service;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.openclassroom.medialabosolutionapplication.repository.IPatientRepository;
import com.openclassroom.medialabosolutionapplication.util.Genre;
import com.openclassroom.medialabosolutionapplication.util.MessageErreur;
import com.openclassroom.medilabosolutionapplication.model.Patient;

/**
 * classe qui implémente la logique métier relative à la création modification suppression et lecture d'une 
 * fiche d'information patient
 */
@Service
public class PatientServiceImpl implements IPatientService {
	
	private final IPatientRepository patientRepository;
	
	public PatientServiceImpl(IPatientRepository patientRepository) {
		this.patientRepository = patientRepository;
	}

	@Override
	/**
	 * méthode pour créer un patient
	 */
	public Patient createPatient(Patient patient) {
		// vérifie que le patient envoyé n'est pas null
		if(patient == null) {
			throw new IllegalArgumentException(MessageErreur.PATIENT_NULL);
		}
		// rechercher que ce patient n'existe pas déjà avant de le créer en bdd
		if(patientRepository.existsByNomAndPrenomAndDateNaissance(patient.getNom(), patient.getPrenom(), patient.getDateNaissance())) {
	        throw new IllegalArgumentException(MessageErreur.PATIENT_EXISTE);
	    }
		// vérifier que les infos ne sont ni vides ni null 
		verificationNom(patient.getNom());
		verificationPrenom(patient.getPrenom());
        verificationGenre(patient.getGenre());
        verificationDateNaissance(patient.getDateNaissance());
        verificationTelephone(patient.getTelephone());
        verificationAdresse(patient.getAdresse());
		return patientRepository.save(patient);
    }	
	
/**
 * méthode pour trouver un patient par son nom
 */
	@Override
	public Optional<Patient> getPatientByName(String nom, String prenom) {
		verificationNom(nom);
		verificationPrenom(prenom);
		Optional<Patient> patient =  patientRepository.findByNomAndPrenom(nom, prenom);
		return patient;
	}

	/**
	 * permet de mettre à jour une fiche info patient
	 */
	@Override
	public Patient updatePatient(Patient patient) {
	// vérifie que l'ID du patient existe en base de données
		if (patient.getId() == null) {
	        throw new IllegalArgumentException(MessageErreur.PATIENT_NULL);
	    }

	    Patient existingPatient = patientRepository.findById(patient.getId())
	        .orElseThrow(() -> new IllegalArgumentException(MessageErreur.PATIENT_EXISTEPAS + patient.getId()));

	    // Mise à jour des champs si non nuls et non vides
	    if (patient.getNom() != null && !patient.getNom().trim().isEmpty()) {
	        existingPatient.setNom(patient.getNom());
	    }
	    if (patient.getPrenom() != null && !patient.getPrenom().trim().isEmpty()) {
	        existingPatient.setPrenom(patient.getPrenom());
	    }
	    if (patient.getGenre() != null) {
	        existingPatient.setGenre(patient.getGenre());
	    }
	    if (patient.getDateNaissance() != null) {
	        existingPatient.setDateNaissance(patient.getDateNaissance());
	    }
	    verificationTelephone(patient.getTelephone());
	    if (patient.getTelephone() != null) {
	        existingPatient.setTelephone(patient.getTelephone());
	    }
	    
	    verificationAdresse(patient.getAdresse());
	    if (patient.getAdresse() != null) {
	        existingPatient.setAdresse(patient.getAdresse());
	    }

	    return patientRepository.save(existingPatient);
	}

	@Override
	public void deletePatient(int id) {
		// TODO Auto-generated method stub
		
	}
	
	private void verificationPrenom(String prenom) {
		if(prenom == null || prenom.trim().isEmpty()) {
			throw new IllegalArgumentException(MessageErreur.PRENOM_OBLIGATOIRE);
		}
	}

	private void verificationNom(String nom) {
		if(nom == null || nom.trim().isEmpty()) {
			throw new IllegalArgumentException(MessageErreur.NOM_OBLIGATOIRE);
		}
	}
	private void verificationGenre(Genre genre) {
		if (genre == null || genre.toString().trim().isEmpty()) {
	        throw new IllegalArgumentException(MessageErreur.GENRE_OBLIGATOIRE);
	    }
	}
	private void verificationDateNaissance(LocalDate dateNaissance) {
		if (dateNaissance == null) {
	        throw new IllegalArgumentException(MessageErreur.DATENAISSANCE_OBLIGATOIRE);
	    }
	}

	private void verificationTelephone(Integer telephone) {
		if (telephone!= null && !telephone.toString().trim().isEmpty()) {
	        if (!telephone.toString().trim().matches("\\d{10}")) {
	        throw new IllegalArgumentException(MessageErreur.TELEPHONE_INVALIDE);
	        }
	    }
	}
	private void verificationAdresse(String adresse) {
		if(adresse != null && adresse.trim().isEmpty()) {
			throw new IllegalArgumentException(MessageErreur.ADRESSE_OBLIGATOIRE);
		}
	}


}
