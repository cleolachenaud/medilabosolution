package com.openclassroom.front.proxies;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.openclassroom.common.model.NotesDTO;
@FeignClient(name = "medilabosolutionapplication-notes", url = "${spring.cloud.openfeign.client.gateway.url}")
public interface IMicroserviceNotesProxy {
		   
	   @GetMapping(value = "/api/notes/patient/{patientId}")
	   ResponseEntity<List<NotesDTO>> getNotesByPatient(@PathVariable("patientId") Integer patientId);

	   @PostMapping(value = "/api/notes/patient/{patientId}")
	   ResponseEntity<NotesDTO> createNoteForPatient(
	       @PathVariable("patientId") Integer patientId,
	       @RequestBody NotesDTO noteDTO);

}