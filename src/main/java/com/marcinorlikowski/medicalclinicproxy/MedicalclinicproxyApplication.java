package com.marcinorlikowski.medicalclinicproxy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class MedicalclinicproxyApplication {
	public static void main(String[] args) {
		SpringApplication.run(MedicalclinicproxyApplication.class, args);
	}
}
