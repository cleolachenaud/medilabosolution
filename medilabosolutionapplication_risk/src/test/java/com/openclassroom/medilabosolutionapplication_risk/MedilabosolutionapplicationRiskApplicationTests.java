package com.openclassroom.medilabosolutionapplication_risk;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.openclassroom.medilabosolutionapplication_risk.proxies.IMicroserviceNotesProxy;
import com.openclassroom.medilabosolutionapplication_risk.proxies.IMicroservicePatientsProxy;

@SpringBootTest (classes = MedilabosolutionapplicationRiskApplication.class)
class MedilabosolutionapplicationRiskApplicationTests {

    @MockBean
    private IMicroservicePatientsProxy patientProxy;
    @MockBean
    private IMicroserviceNotesProxy noteProxy;
    
	@Test
	void contextLoads() {
	}

}
