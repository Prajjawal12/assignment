package com.example.modified_assignment_backend.config;

import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DatabaseConfig {
    private final static String dbUri = "bolt://localhost:7687";
    private final static String username = "neo4j";
    private final static String password = "12345678";

    @Bean
    public static Driver getDriver() {
        return GraphDatabase.driver(dbUri, AuthTokens.basic(username, password));
    }

    public static boolean checkConnection() {
        try (Driver driver = getDriver()) {
            driver.verifyConnectivity();

        } catch (Exception e) {
            return false;
        }

        return true;
    }
}
