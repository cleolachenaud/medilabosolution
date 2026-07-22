package com.openclassroom.patient.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.openclassroom.common.model.PatientDTO;
import com.openclassroom.patient.util.MessageErreur;
import com.openclassroom.patient.model.Patient;
import com.openclassroom.patient.repository.IPatientRepository;

@Service
public class PatientServiceImpl implements IPatientService{

	private final IPatientRepository patientRepository;

	
	public PatientServiceImpl(IPatientRepository patientRepository) {
		this.patientRepository = patientRepository;
	}

	/**
	 * méthode pour créer un patient
	 */
	@Override
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
        verificationTelephone(patient.getTelephone());
        verificationAdresse(patient.getAdresse());
		return patientRepository.save(patient);
    }	
	
/**
 * méthode pour trouver un patient par son nom
 */
	@Override
	public Optional<Patient> getPatientByName(String nom, String prenom) {
		Optional<Patient> patient =  patientRepository.findByNomAndPrenom(nom, prenom);
		return patient;
	}
	/**
	 * méthode pour trouver un patient par son Id, utile pour mettre à jour le patient
	 */
	@Override
	public Optional<Patient> getPatientById(Integer id) {
	    return patientRepository.findById(id);
	}

	/**
	 * permet de mettre à jour une fiche info patient
	 */
	@Override
	public Patient updatePatient(Patient patient) {
	// vérifie que l'ID du patient existe en base de données et évite un appel inutil a la base
		if (patient.getId() == null) {
	        throw new IllegalArgumentException(MessageErreur.PATIENT_NULL);
	    }
// je récupère le patient déjà en bdd pour pouvoir le comparer avec celui passé en paramètres et ainsi ne modifier que les infos nécéssaires 
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
	


	private void verificationTelephone(String telephone) {
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
/**
 * permet de convertir un patientDTO (classe common) en un patient (model) 
 */
	@Override
	public Patient patientDTOToPatient(PatientDTO patientDTO) {
		Patient patient = new Patient();
		patient.setId(patientDTO.getId());
		patient.setNom(patientDTO.getNom());
		patient.setPrenom(patientDTO.getPrenom());
		patient.setDateNaissance(patientDTO.getDateNaissance());
		patient.setGenre(patientDTO.getGenre());
		patient.setAdresse(normaliserChampFacultatif(patientDTO.getAdresse()));
		patient.setTelephone(normaliserChampFacultatif(patientDTO.getTelephone()));
		return patient;
	}
/**
 * permet de convertir un patient(model) en un patient DTO (common) pour pouvoir le retourner facilement aux autres microservices
 */
	@Override
	public PatientDTO patientToPatientDTO(Patient patient) {
		PatientDTO patientDTO = new PatientDTO();
		patientDTO.setId(patient.getId());
		patientDTO.setNom(patient.getNom());
		patientDTO.setPrenom(patient.getPrenom());
		patientDTO.setDateNaissance(patient.getDateNaissance());
		patientDTO.setGenre(patient.getGenre());
		patientDTO.setAdresse(patient.getAdresse());
		patientDTO.setTelephone(patient.getTelephone());
		return patientDTO;
	}
	/**
	 * permet de s'assurer que l'adresse ou le téléphone reste null et non vide si jamais ce champ n'est pas renseigné dans le front étant donné que 
	 * c'est un champ non obligatoire
	 * @param adresse
	 * @return
	 */
	private String normaliserChampFacultatif(String champFacultatif) {
	    if (champFacultatif == null || champFacultatif.trim().isEmpty()) {
	        return null;
	    }
	    return champFacultatif.trim();
	}
}
