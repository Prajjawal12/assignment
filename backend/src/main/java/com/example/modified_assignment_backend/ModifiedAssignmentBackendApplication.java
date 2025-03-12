package com.example.modified_assignment_backend;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.example.modified_assignment_backend.config.DatabaseConfig;

@SpringBootApplication
public class ModifiedAssignmentBackendApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(ModifiedAssignmentBackendApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		if (DatabaseConfig.checkConnection()) {
			System.out.println("Connection to Database is succesful!");
		}
	}

}
