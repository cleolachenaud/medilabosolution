package com.openclassroom.front.proxies;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.openclassroom.common.model.PatientDTO;

@FeignClient(name = "medilabosolutionapplication-patient", url = "${spring.cloud.openfeign.client.gateway.url}")
public interface IMicroservicePatientsProxy {
	
   @GetMapping(value = "/api/patients")
   ResponseEntity<PatientDTO> getPatient(@RequestParam("nom") String nom, @RequestParam("prenom") String prenom);
   
   @GetMapping(value = "/api/patients/{id}")
   ResponseEntity<PatientDTO> getPatientById(@PathVariable("id") Integer id);

   @PostMapping(value = "/api/patients")
   ResponseEntity<PatientDTO> createPatient(@RequestBody PatientDTO patientDTO);
   
   @PutMapping(value = "/api/patients/{id}")
   ResponseEntity<PatientDTO> updatePatient(@PathVariable("id") Integer id, @RequestBody PatientDTO patientDTO);

}