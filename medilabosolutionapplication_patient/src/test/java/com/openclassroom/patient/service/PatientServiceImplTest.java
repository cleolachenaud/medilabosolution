package com.openclassroom.patient.service;

import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.openclassroom.common.util.Genre;
import com.openclassroom.patient.util.MessageErreur;
import com.openclassroom.patient.model.Patient;
import com.openclassroom.patient.repository.IPatientRepository;
import com.openclassroom.patient.util.PatientTestFactory;

public class PatientServiceImplTest {
	private Patient patient;
	private IPatientRepository mockRepo;
	
	@BeforeEach
	void setUp() {
		// création du patient pour simuler le renseignement des données via IHM
	    patient = PatientTestFactory.creationPatient("Dupont", "Jean", Genre.M);
	    // mock du repository
	    mockRepo = Mockito.mock(IPatientRepository.class);
	    
	}
	@Test
	void creerPatientNominal() {
		// création des données
	    Patient savedPatient = PatientTestFactory.creationPatient("Dupont", "Jean", Genre.M);
	    savedPatient.setDateNaissance(patient.getDateNaissance());
	    // init des mocks
	    Mockito.when(mockRepo.save(patient)).thenReturn(savedPatient);
	    PatientServiceImpl service = new PatientServiceImpl(mockRepo);
	    // appel du service
	    Patient result = service.createPatient(patient);
	    // vérifications
	    Assertions.assertNotNull(result);
	    Assertions.assertEquals("Dupont", result.getNom());
	}
	
	@Test
	void creerPatientExisteDeja() {
		
	    Mockito.when(mockRepo.existsByNomAndPrenomAndDateNaissance(
	        patient.getNom(), patient.getPrenom(), patient.getDateNaissance()))
	        .thenReturn(true);
	   // appel du service
	    PatientServiceImpl service = new PatientServiceImpl(mockRepo);

	    IllegalArgumentException thrown = Assertions.assertThrows(IllegalArgumentException.class, () -> {
	        service.createPatient(patient);
	    });
	    // vérifications
	    Assertions.assertEquals(MessageErreur.PATIENT_EXISTE, thrown.getMessage());
	    Mockito.verify(mockRepo, Mockito.never()).save(Mockito.any());
	}
	
	@Test
	void updatePatientNominal() {
		// création des données
	    Patient patientAMaj = PatientTestFactory.creationPatient("Dupont", "NouveauPrenom", Genre.M);
	    patientAMaj.setDateNaissance(patient.getDateNaissance());
	    // init des mocks
	    Mockito.when(mockRepo.findById(1)).thenReturn(Optional.of(patient));
	    Mockito.when(mockRepo.save(Mockito.any(Patient.class))).thenAnswer(i -> i.getArgument(0));
	    // appel du service
	    PatientServiceImpl service = new PatientServiceImpl(mockRepo);

	    Patient misAJour = service.updatePatient(patientAMaj);
	    // vérifications
	    Assertions.assertEquals("Dupont", misAJour.getNom());
	    Assertions.assertEquals("NouveauPrenom", misAJour.getPrenom());
	    Assertions.assertEquals(Genre.M, misAJour.getGenre());
	    Assertions.assertEquals(patient.getDateNaissance(), misAJour.getDateNaissance());

	    Mockito.verify(mockRepo).save(patient);
	}

}
