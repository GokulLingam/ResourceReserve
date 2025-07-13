package com.example.ResourceReserve;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaRepositories
@EnableJpaAuditing
public class ResourceReserveBeApplication {

	public static void main(String[] args) {
		SpringApplication.run(ResourceReserveBeApplication.class, args);
	}

}
