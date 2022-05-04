package com.example.demo;

import org.keycloak.adapters.springboot.KeycloakSpringBootConfigResolver;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class KeycloakDemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(KeycloakDemoApplication.class, args);
	}


	//Creating bean keycloakConfigResolver
	@Bean
	public KeycloakSpringBootConfigResolver keycloakConfigResolver() {
		return new KeycloakSpringBootConfigResolver();
	}
}
