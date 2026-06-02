package com.openclassroom.medilabosolutionapplication_risk.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import com.openclassroom.common.model.NotesDTO;
import com.openclassroom.common.model.PatientDTO;
import com.openclassroom.common.model.RiskDTO;
import com.openclassroom.common.util.Genre;
import com.openclassroom.common.util.NiveauRisk;
import com.openclassroom.medilabosolutionapplication_risk.proxies.IMicroserviceNotesProxy;
import com.openclassroom.medilabosolutionapplication_risk.proxies.IMicroservicePatientsProxy;

public class RiskServiceTest {
// mock des proxies 
	    @Mock
	    private IMicroserviceNotesProxy notesProxy;

	    @Mock
	    private IMicroservicePatientsProxy patientProxy;
// injection du service a tester 
	    @InjectMocks
	    private RiskServiceImpl riskService;
// setup pour la mise en place des mocks
	    @BeforeEach
	    public void setUp() {
	        MockitoAnnotations.openMocks(this);
	        riskService = new RiskServiceImpl(notesProxy, patientProxy);
	    }

	    @Test
	    public void testAucunRisqueHomme() throws Exception {
	        int patientId = 1;
	        PatientDTO patient = createPatient(Genre.M, 40);
	        List<NotesDTO> notes = new ArrayList<>();
		    notes.add(createNote("Aucune information pertinente"));

	        when(patientProxy.getPatientById(patientId)).thenReturn(ResponseEntity.ok(patient));
	        when(notesProxy.getNotesByPatient(patientId)).thenReturn(ResponseEntity.ok(notes));

	        RiskDTO risk = riskService.calculNiveauRisk(patientId);

	        assertNotNull(risk);
	        assertEquals(NiveauRisk.AUCUN_RISQUE, risk.getNiveauRisk());
	    }
	    @Test
	    public void testRisqueLimitePlus30Ans() throws Exception {
	        int patientId = 1;
	        PatientDTO patient = createPatient(Genre.F, 40);
	        List<NotesDTO> notes = new ArrayList<>();
		    notes.add(createNote("taille et poids"));

	        when(patientProxy.getPatientById(patientId)).thenReturn(ResponseEntity.ok(patient));
	        when(notesProxy.getNotesByPatient(patientId)).thenReturn(ResponseEntity.ok(notes));

	        RiskDTO risk = riskService.calculNiveauRisk(patientId);

	        assertNotNull(risk);
	        assertEquals(NiveauRisk.RISQUE_LIMITE, risk.getNiveauRisk());
	    }
	    @Test
	    public void testDangerPlus30Ans() throws Exception {
	        int patientId = 1;
	        PatientDTO patient = createPatient(Genre.M, 40);
	        List<NotesDTO> notes = new ArrayList<>();
		    notes.add(createNote("taille et poids"));
		    notes.add(createNote("fumeur, fumeuse anormal et cholesterol sans vertiges"));
	        when(patientProxy.getPatientById(patientId)).thenReturn(ResponseEntity.ok(patient));
	        when(notesProxy.getNotesByPatient(patientId)).thenReturn(ResponseEntity.ok(notes));

	        RiskDTO risk = riskService.calculNiveauRisk(patientId);

	        assertNotNull(risk);
	        assertEquals(NiveauRisk.DANGER, risk.getNiveauRisk());
	    }
	    @Test
	    public void testApparitionPrecocePlus30Ans() throws Exception {
	        int patientId = 1;
	        PatientDTO patient = createPatient(Genre.M, 40);
	        List<NotesDTO> notes = new ArrayList<>();
		    notes.add(createNote("taille et poids"));
		    notes.add(createNote("hemoglobine a1c, microalbumine, fumeur, fumeuse anormal et cholesterol sans vertiges"));
	        when(patientProxy.getPatientById(patientId)).thenReturn(ResponseEntity.ok(patient));
	        when(notesProxy.getNotesByPatient(patientId)).thenReturn(ResponseEntity.ok(notes));

	        RiskDTO risk = riskService.calculNiveauRisk(patientId);

	        assertNotNull(risk);
	        assertEquals(NiveauRisk.APARITION_PRECOCE, risk.getNiveauRisk());
	    }
	    @Test
	    public void testADangerHomme() throws Exception {
	        int patientId = 3;
	        PatientDTO patient = createPatient(Genre.M, 25);
	        List<NotesDTO> notes = new ArrayList<>();
		    notes.add(createNote("Patient fumeur anormal"));
		    notes.add(createNote("Présence d'anticorps"));

	        when(patientProxy.getPatientById(patientId)).thenReturn(ResponseEntity.ok(patient));
	        when(notesProxy.getNotesByPatient(patientId)).thenReturn(ResponseEntity.ok(notes));

	        RiskDTO risk = riskService.calculNiveauRisk(patientId);

	        assertNotNull(risk);
	        assertEquals(NiveauRisk.DANGER, risk.getNiveauRisk());
	    }

	    public void testApparitionPrecoceHomme() throws Exception {
	        int patientId = 3;
	        PatientDTO patient = createPatient(Genre.M, 25);
	        List<NotesDTO> notes = new ArrayList<>();
		    notes.add(createNote("Patient fumeur anormal taille normal poids normal"));
		    notes.add(createNote("Présence d'anticorps"));

	        when(patientProxy.getPatientById(patientId)).thenReturn(ResponseEntity.ok(patient));
	        when(notesProxy.getNotesByPatient(patientId)).thenReturn(ResponseEntity.ok(notes));

	        RiskDTO risk = riskService.calculNiveauRisk(patientId);

	        assertNotNull(risk);
	        assertEquals(NiveauRisk.APARITION_PRECOCE, risk.getNiveauRisk());
	    }
	    
	    @Test
	    public void testADangerFemme() throws Exception {
	        int patientId = 2;
	        PatientDTO patient = createPatient(Genre.F, 25);
	        List<NotesDTO> notes = new ArrayList<>();
		    notes.add(createNote("Patient fumeur et avec taux de cholesterol anormal"));
		    notes.add(createNote("Présence d'anticorps et microalbumine élevée"));

	        when(patientProxy.getPatientById(patientId)).thenReturn(ResponseEntity.ok(patient));
	        when(notesProxy.getNotesByPatient(patientId)).thenReturn(ResponseEntity.ok(notes));

	        RiskDTO risk = riskService.calculNiveauRisk(patientId);

	        assertNotNull(risk);
	        assertEquals(NiveauRisk.DANGER, risk.getNiveauRisk());
	    }
	    
	    public void testApparitionPrecoceFemme() throws Exception {
	        int patientId = 3;
	        PatientDTO patient = createPatient(Genre.F, 25);
	        List<NotesDTO> notes = new ArrayList<>();
		    notes.add(createNote("Patient fumeur anormal taille normal poids normal"));
		    notes.add(createNote("Présence d'anticorps et vertiges"));

	        when(patientProxy.getPatientById(patientId)).thenReturn(ResponseEntity.ok(patient));
	        when(notesProxy.getNotesByPatient(patientId)).thenReturn(ResponseEntity.ok(notes));

	        RiskDTO risk = riskService.calculNiveauRisk(patientId);

	        assertNotNull(risk);
	        assertEquals(NiveauRisk.APARITION_PRECOCE, risk.getNiveauRisk());
	    }
	    
	    @Test
	    public void testSansNotesRisqueAucun() throws Exception {
	        int patientId = 4;
	        PatientDTO patient = new PatientDTO();
	        patient.setDateNaissance(LocalDate.now().minusYears(40));
	        patient.setGenre(Genre.M);

	        when(patientProxy.getPatientById(patientId)).thenReturn(ResponseEntity.ok(patient));
	        when(notesProxy.getNotesByPatient(patientId)).thenReturn(ResponseEntity.ok(new ArrayList<>())); // liste vide

	        RiskDTO risk = riskService.calculNiveauRisk(patientId);

	        assertNotNull(risk);
	        assertEquals(NiveauRisk.AUCUN_RISQUE, risk.getNiveauRisk());
	    }
// méthodes utiles pour créer patient et notes
	private PatientDTO createPatient(Genre genre, int age ) {
		PatientDTO patient = new PatientDTO();
        patient.setDateNaissance(LocalDate.now().minusYears(age));
        patient.setGenre(genre);
		return patient;
		
	}
	private NotesDTO createNote(String contenuNotes){
	    NotesDTO note = new NotesDTO();
	    note.setContenu(contenuNotes);
		return note;
	}
}
