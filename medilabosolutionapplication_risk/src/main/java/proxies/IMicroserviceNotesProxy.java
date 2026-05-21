package proxies;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.openclassroom.common.model.NotesDTO;
@FeignClient(name = "medilabosolutionapplication-notes", url = "http://localhost:8080")
public interface IMicroserviceNotesProxy {
		   
	   @GetMapping(value = "/notes/patient/{patientId}")
	   ResponseEntity<List<NotesDTO>> getNotesByPatient(@PathVariable("patientId") Integer patientId);

}
