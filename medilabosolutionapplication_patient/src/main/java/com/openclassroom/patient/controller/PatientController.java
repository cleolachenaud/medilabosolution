package com.openclassroom.patient.controller;

import java.util.Optional;

import org.springframework.http.HttpStatus;
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
    public ResponseEntity<Patient> getPatientByName(
           @NotBlank @RequestParam String nom,
           @NotBlank @RequestParam String prenom) {

        Optional<Patient> patient = patientService.getPatientByName(nom, prenom);
        //System.out.println("Patient = "+ patient.toString());
        return patient
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Patient> updatePatient(
    		@PathVariable Integer id, 
    		@RequestBody Patient patient) {
    	System.out.println("id = " + id);
    	System.out.println("patient = " + patient);
        if (!id.equals(patient.getId())) {
            return ResponseEntity.badRequest().build();
        }

        Patient updated = patientService.updatePatient(patient);
        return ResponseEntity.ok(updated);
    }

    @PostMapping("/")
    public ResponseEntity<Patient> createPatient(
    		@Valid @RequestBody Patient patient) {
    	
        Patient created = patientService.createPatient(patient);
        return ResponseEntity.status(201).body(created);
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
