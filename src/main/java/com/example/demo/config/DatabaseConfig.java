package com.example.demo.config;

import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DatabaseConfig {

  private final static String dbUri = "neo4j+s://3d37f46e.databases.neo4j.io";
  private final static String username = "neo4j";
  private final static String password = "WIjbCbB2zWEwZz6l_j4Xt8xmd8Vv3YmK0t_pKTTKr8s";

  // Returns a new instance of the Neo4j Driver with authentication credentials
  @Bean
  public static Driver getDriver() {
    return GraphDatabase.driver(dbUri, AuthTokens.basic(username, password));
  }

  // Checks the connectivity to the Neo4j database by attempting to verify the
  // driver connection
  public static boolean checkConnection() {
    try (Driver driver = getDriver()) {
      driver.verifyConnectivity();
    } catch (Exception e) {
      return false;
    }
    return true;
  }
}
