package controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.openclassroom.common.model.RiskDTO;
import service.IRiskService;


@RestController
@RequestMapping("/risk")
public class RiskController {
	@Autowired
    private final IRiskService riskService;
    public RiskController(IRiskService riskService){
    	this.riskService = riskService;
    }

	   
	@GetMapping("/{id}")
	public ResponseEntity<RiskDTO> geRiskByPatientId(@PathVariable Integer id) {
        RiskDTO niveauRisk = new RiskDTO();
		try {
			niveauRisk = riskService.calculNiveauRisk(id);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        // je retourne forcément une réponse positive étant donné que j'ai déjà identifié le patient. 
        return ResponseEntity.ok(niveauRisk);
    }
}
