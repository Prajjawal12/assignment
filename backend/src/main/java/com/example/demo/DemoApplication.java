package com.example.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.example.demo.config.DatabaseConfig;

@SpringBootApplication
public class DemoApplication implements CommandLineRunner {
	private static final Logger logger = LoggerFactory.getLogger(DemoApplication.class);

	// Main entry point for the application, runs the Spring Boot application
	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

	// Run method that checks the connection to the Neo4j database when the
	// application starts
	@Override
	public void run(String... args) throws Exception {
		logger.info("Attempting to establish connection to an instance of Neo4j ...");

		// Check if the connection to the database is successful
		if (DatabaseConfig.checkConnection()) {
			logger.info("Connection to Database is succesful");
		} else {
			logger.error("Failure in connecting to database");
		}
	}
}
