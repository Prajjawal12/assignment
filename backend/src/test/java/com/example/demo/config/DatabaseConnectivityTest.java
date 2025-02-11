package com.example.demo.config;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.neo4j.driver.Driver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class DatabaseConnectivityTest {

  @Autowired
  private Driver driver;

  @Test
  void createDriverAndConnectToServer() {
    assertNotNull(driver, "Driver should be instantiated");

    assertDoesNotThrow(() -> driver.verifyConnectivity(), "Unable to verify connectivity.");
  }
}
