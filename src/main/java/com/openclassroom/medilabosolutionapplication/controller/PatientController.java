package com.openclassroom.medilabosolutionapplication.controller;

import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.openclassroom.medilabosolutionapplication.model.Patient;
import com.openclassroom.medilabosolutionapplication.service.IPatientService;

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
    
    @GetMapping
    public ResponseEntity<Patient> getPatientByName(
           @NotBlank @RequestParam String nom,
           @NotBlank @RequestParam String prenom) {

        Optional<Patient> patient = patientService.getPatientByName(nom, prenom);

        return patient
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
    

    @PutMapping("/{id}")
    public ResponseEntity<Patient> updatePatient(
    		@PathVariable Integer id, 
    		@Valid @RequestBody Patient patient) {
        if (!id.equals(patient.getId())) {
            return ResponseEntity.badRequest().build();
        }

        Patient updated = patientService.updatePatient(patient);
        return ResponseEntity.ok(updated);
    }

    @PostMapping
    public ResponseEntity<Patient> createPatient(
    		@Valid @RequestBody Patient patient) {
    	
        Patient created = patientService.createPatient(patient);
        return ResponseEntity.status(201).body(created);
    }
}