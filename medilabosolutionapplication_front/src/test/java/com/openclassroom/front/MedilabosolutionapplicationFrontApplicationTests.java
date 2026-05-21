package com.openclassroom.front;

import com.openclassroom.front.proxies.IMicroservicePatientsProxy;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest(classes = MedilabosolutionapplicationFrontApplication.class)
class MedilabosolutionapplicationFrontApplicationTests {

    @MockBean
    private IMicroservicePatientsProxy patientProxy;

    @Test
    void contextLoads() {
        // Le test vérifie uniquement que le contexte Spring se charge sans erreur
    }
}