package com.openclassroom.patient.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.openclassroom.patient.model.Patient;
import com.openclassroom.patient.service.IPatientService;
import com.openclassroom.patient.util.Genre;
import com.openclassroom.patient.util.PatientTestFactory;

@SpringBootTest(classes = com.openclassroom.patient.PatientApplication.class)
@AutoConfigureMockMvc(addFilters = false)
public class PatientControllerTest {

	@Autowired
    private MockMvc mockMvc;
	@MockBean
	private IPatientService patientService;
	
	private Patient patient;
	private String patientJson = """
            {
            "id": 1,
            "nom": "Dupont",
            "prenom": "Jean"
        }
    """;
	private String patientJsonCreation = """
			{ 
			"nom": "Dupont",
			"prenom": "Jean",
			"dateNaissance": "1987-11-01",
			"genre": "F",
			"telephone": "0611213147"
  }
			"""
			;
	
	@BeforeEach
	void setUp() {
		// création du patient pour simuler le renseignement des données via IHM
	    patient = PatientTestFactory.creationPatient("Dupont", "Jean", Genre.M);
	}

    @Test
    public void getPatientNominal() throws Exception {
       
        when(patientService.getPatientByName("Dupont", "Jean")).thenReturn(Optional.of(patient));

        mockMvc.perform(get("/patients/")
                .param("nom", "Dupont")
                .param("prenom", "Jean")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.nom").value("Dupont"))
            .andExpect(jsonPath("$.prenom").value("Jean"));
    }

    @Test
    public void getPatientNomNonTrouve() throws Exception {
        when(patientService.getPatientByName("Inexistant", "Patient")).thenReturn(Optional.empty());

        mockMvc.perform(get("/patients/")
                .param("nom", "Inexistant")
                .param("prenom", "Patient")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());
    }

    @Test
    public void updatePatientNominal() throws Exception {

        Patient updatedPatient = PatientTestFactory.creationPatient("DupontUpdated", "Jean", Genre.M);

        // Mock du service : retourne le patient mis à jour
        when(patientService.updatePatient(org.mockito.ArgumentMatchers.any(Patient.class)))
            .thenReturn(updatedPatient);

        mockMvc.perform(put("/patients/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(patientJson))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.nom").value("DupontUpdated"))
            .andExpect(jsonPath("$.prenom").value("Jean"));
    }
    @Test
    public void creerPatientNominal() throws Exception {
        // On mocke le service createPatient pour qu’il retourne le patient créé
        when(patientService.createPatient(org.mockito.ArgumentMatchers.any(Patient.class)))
            .thenReturn(patient);

        mockMvc.perform(post("/patients/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(patientJsonCreation))
            .andExpect(status().isCreated()) // HTTP 201
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.nom").value("Dupont"))
            .andExpect(jsonPath("$.prenom").value("Jean"));
    }
}