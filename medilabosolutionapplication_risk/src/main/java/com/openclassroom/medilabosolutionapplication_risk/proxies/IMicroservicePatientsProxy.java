package com.openclassroom.medilabosolutionapplication_risk.proxies;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

import com.openclassroom.common.model.PatientDTO;

/**
 * Appel direct vers le backend patient (pas via la gateway).
 * Le header X-Authenticated-User est propagé pour satisfaire le filtre de sécurité interne.
 */
@FeignClient(name = "medilabosolutionapplication-patient", url = "${spring.cloud.openfeign.client.patient.url}")
public interface IMicroservicePatientsProxy {

    @GetMapping(value = "/api/patients/{id}")
    ResponseEntity<PatientDTO> getPatientById(
            @PathVariable("id") Integer id,
            @RequestHeader("X-Authenticated-User") String authenticatedUser);
}
