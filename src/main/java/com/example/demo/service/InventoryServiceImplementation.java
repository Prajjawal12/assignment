package com.example.demo.service;

import java.util.Map;

import org.neo4j.driver.Driver;
import org.neo4j.driver.Values;

import org.springframework.stereotype.Service;

import com.example.demo.config.DatabaseConfig;
import com.example.demo.entity.ShelfPositionV0;
import com.example.demo.entity.ShelfV0;

@Service
public class InventoryServiceImplementation implements InventoryService {

  private final Driver driver = DatabaseConfig.getDriver();

  @Override
  public Map<String, Object> saveShelf(ShelfV0 shelf) {

    try (var session = driver.session()) {
      return session.executeWrite(tx -> {
        String query = """
                MERGE (s:ShelfV0 {id: $shelfId})
                SET s.name = $shelfName, s.shelfType = $shelfType
                RETURN s AS savedShelf
            """;

        var result = tx.run(query, Values.parameters("shelfId", shelf.getId(),
            "shelfName", shelf.getName(), "shelfType", shelf.getShelfType())).single();

        return result.get("savedShelf").asNode().asMap();
      });
    }
  }

  @Override
  public Map<String, Object> getShelf(Long shelfId) {

    try (var session = driver.session()) {
      return session.executeRead(tx -> {
        String query = """
                MATCH (s:ShelfV0 {id: $shelfId})
                RETURN s AS shelf
            """;

        var result = tx.run(query, Values.parameters("shelfId", shelfId)).single();

        return result.get("shelf").asNode().asMap();
      });
    }
  }

  @Override
  public Map<String, Object> saveShelfPosition(ShelfPositionV0 shelfPosition) {

    try (var session = driver.session()) {
      return session.executeWrite(tx -> {
        String query = """
                MERGE (sp:ShelfPositionV0 {id: $positionId})
                SET sp.name = $positionName
                RETURN sp AS savedShelfPosition
            """;

        var result = tx.run(query, Values.parameters("positionId", shelfPosition.getId(),
            "positionName", shelfPosition.getName())).single();

        return result.get("savedShelfPosition").asNode().asMap();
      });
    }
  }

  @Override
  public Map<String, Object> getShelfPosition(long shelfPositionId) {

    try (var session = driver.session()) {
      return session.executeRead(tx -> {
        String query = """
                MATCH (sp:ShelfPositionV0 {id: $positionId})
                RETURN sp AS shelfPosition
            """;

        var result = tx.run(query, Values.parameters("positionId", shelfPositionId)).single();

        return result.get("shelfPosition").asNode().asMap();
      });
    }
  }

  @Override
  public void addShelfPositionToDevice(Long deviceId, Long shelfPositionId) {

    try (var session = driver.session()) {
      session.executeWrite(tx -> {
        String query = """
                MATCH (d:Device {id: $deviceId}), (sp:ShelfPositionV0 {id: $shelfPositionId})
                MERGE (d)-[:HAS]->(sp)
                RETURN d, sp
            """;

        tx.run(query, Values.parameters("deviceId", deviceId, "shelfPositionId", shelfPositionId));

        return null;
      });
    }
  }

  @Override
  public void addShelfToShelfPosition(Long shelfId, Long shelfPositionId) {
    try (var session = driver.session()) {
      session.executeWrite(tx -> {
        String query = """
                MATCH (sp:ShelfPositionV0 {id: $shelfPositionId}), (s:ShelfV0 {id: $shelfId})
                MERGE (sp)-[:HAS]->(s)
                RETURN sp, s
            """;

        tx.run(query, Values.parameters("shelfPositionId", shelfPositionId, "shelfId", shelfId));

        return null;
      });
    }
  }
}
