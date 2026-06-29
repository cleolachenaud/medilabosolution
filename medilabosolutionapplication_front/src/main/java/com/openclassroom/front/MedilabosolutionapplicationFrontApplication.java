package com.openclassroom.front;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
//@EnableDiscoveryClient
@SpringBootApplication(exclude = {})
public class MedilabosolutionapplicationFrontApplication {

	public static void main(String[] args) {
		SpringApplication.run(MedilabosolutionapplicationFrontApplication.class, args);
	}

}
