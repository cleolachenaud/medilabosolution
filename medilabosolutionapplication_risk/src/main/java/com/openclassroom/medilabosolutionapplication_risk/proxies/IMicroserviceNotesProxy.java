package com.openclassroom.medilabosolutionapplication_risk.proxies;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

import com.openclassroom.common.model.NotesDTO;

/**
 * Appel direct vers le backend notes (pas via la gateway).
 * Le header X-Authenticated-User est propagé pour satisfaire le filtre de sécurité interne.
 */
@FeignClient(name = "medilabosolutionapplication-notes", url = "${spring.cloud.openfeign.client.notes.url}")
public interface IMicroserviceNotesProxy {

    @GetMapping(value = "/api/notes/patient/{patientId}")
    ResponseEntity<List<NotesDTO>> getNotesByPatient(
            @PathVariable("patientId") Integer patientId);
}
