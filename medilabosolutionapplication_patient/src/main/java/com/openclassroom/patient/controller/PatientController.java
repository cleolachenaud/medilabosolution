package com.openclassroom.patient.controller;

import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.openclassroom.common.model.PatientDTO;
import com.openclassroom.patient.model.Patient;
import com.openclassroom.patient.service.IPatientService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

@RestController
@RequestMapping("/patients")
@Validated
public class PatientController {

    private final IPatientService patientService;

    public PatientController(IPatientService patientService) {
        this.patientService = patientService;
    }

    @GetMapping({"", "/"})
    public ResponseEntity<PatientDTO> getPatientByName(
           @NotBlank @RequestParam String nom,
           @NotBlank @RequestParam String prenom) {

        Optional<Patient> patient = patientService.getPatientByName(nom, prenom);
        Optional<PatientDTO> patientDTO = Optional.empty();
        if (patient.isPresent() && patient.get() != null) {
        	patientDTO = Optional.of(patientService.patientToPatientDTO(patient.get()));
        }
        return patientDTO
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<PatientDTO> getPatientById(@PathVariable Integer id) {
        Optional<Patient> patient = patientService.getPatientById(id);
        if (patient.isPresent()) {
            PatientDTO patientDTO = patientService.patientToPatientDTO(patient.get());
            return ResponseEntity.ok(patientDTO);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<PatientDTO> updatePatient(
    		@PathVariable Integer id, 
    		@RequestBody PatientDTO patientDTO) {
    	System.out.println("id = " + id);
    	System.out.println("patient = " + patientDTO);
        if (!id.equals(patientDTO.getId())) {
            return ResponseEntity.badRequest().build();
        }

        Patient updated = patientService.updatePatient(patientService.patientDTOToPatient(patientDTO));
        return ResponseEntity.ok(patientService.patientToPatientDTO(updated));
    }

    @PostMapping({"", "/"})
    public ResponseEntity<PatientDTO> createPatient(
    		@Valid @RequestBody PatientDTO patientDTO) {
    	
        Patient created = patientService.createPatient(patientService.patientDTOToPatient(patientDTO));
        return ResponseEntity.status(201).body(patientService.patientToPatientDTO(created));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePatient(@PathVariable Integer id) {
        try {
            patientService.deletePatient(id);
            return ResponseEntity.noContent().build(); // 204 No Content si suppression OK
        } catch (IllegalArgumentException e) {
            // Si patient non trouvé ou id null
            return ResponseEntity.notFound().build();
        }
    }
}
