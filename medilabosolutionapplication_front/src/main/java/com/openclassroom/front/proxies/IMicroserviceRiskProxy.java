package com.openclassroom.front.proxies;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.openclassroom.common.model.RiskDTO;


@FeignClient(name = "medilabosolutionapplication-risk", url = "${spring.cloud.openfeign.client.gateway.url}")
public interface IMicroserviceRiskProxy {

    @GetMapping(value = "/risk/patient/{id}")
    ResponseEntity<RiskDTO> getRiskByPatient(@PathVariable("id") Integer id);


}
