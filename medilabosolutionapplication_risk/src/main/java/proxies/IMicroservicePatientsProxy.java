package proxies;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.openclassroom.common.model.PatientDTO;

@FeignClient(name = "medilabosolutionapplication-patient", url = "http://localhost:8080")
public interface IMicroservicePatientsProxy {
   
   @GetMapping(value = "/patients/{id}")
   ResponseEntity<PatientDTO> getPatientById(@PathVariable("id") Integer id);


}
