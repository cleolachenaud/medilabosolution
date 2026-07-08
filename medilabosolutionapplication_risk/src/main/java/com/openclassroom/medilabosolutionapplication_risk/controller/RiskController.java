package com.openclassroom.medilabosolutionapplication_risk.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.openclassroom.common.model.RiskDTO;
import com.openclassroom.medilabosolutionapplication_risk.service.IRiskService;


@RestController
@RequestMapping("/api/risk")
public class RiskController {
    private final IRiskService riskService;

    public RiskController(IRiskService riskService){
        this.riskService = riskService;
    }

    @GetMapping("/patient/{id}")
    public ResponseEntity<RiskDTO> geRiskByPatientId(
            @PathVariable("id") Integer id) {
        RiskDTO niveauRisk = new RiskDTO();
        try {
            niveauRisk = riskService.calculNiveauRisk(id);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(niveauRisk);
        }
        return ResponseEntity.ok(niveauRisk);
    }
}
