package com.example.demo.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.neo4j.driver.Driver;
import org.neo4j.driver.Record;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;
import org.neo4j.driver.Values;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.customExceptions.ShelfNotFoundException;
import com.example.demo.entity.ShelfV0;

@Service
public class InventoryServiceImplementation implements InventoryService {

  @Autowired
  private Driver driver;

  @Override
  public Map<String, Object> saveShelf(ShelfV0 shelf, boolean confirmModification) {

    if (confirmModification) {
      modifyShelf(shelf.getId(), shelf);
    } else {
      createShelf(shelf);
    }
    return getShelfById(shelf.getId());

  }

  @Override
  public void createShelf(ShelfV0 shelf) {
    try (Session session = driver.session()) {
      session.executeWriteWithoutResult(tx -> {
        String query = """
            CREATE (s: ShelfV0 {id: $shelfId})
            SET s.name = $shelfName, s.shelfType = $shelfType, s.isDeleted = 'N'
            WITH s, range(1, $associatedShelfPositions) AS positionNumbers
            FOREACH (position IN positionNumbers |
                CREATE (sp: ShelfPositionV0 {position: position , isActive : 'N'})
                CREATE (s)-[r:HAS_SHELF_POSITION]->(sp)
                SET r.isDeleted = 'Y'
            )
            RETURN s;
            """;

        tx.run(query, Values.parameters(
            "shelfId", shelf.getId(),
            "shelfName", shelf.getName(),
            "shelfType", shelf.getShelfType(),
            "associatedShelfPositions", shelf.getAssociatedShelfPositions()));
      });
    }
  }

  @Override
  public void modifyShelf(Long shelfId, ShelfV0 shelf) {
    try (Session session = driver.session()) {
      session.executeWriteWithoutResult(tx -> {
        String query = """
            MATCH (s: ShelfV0 {id: $shelfId})
            WHERE s.isDeleted = 'N'
            SET s.name = $shelfName, s.shelfType = $shelfType , s.modifiedCredentialsAt = datetime()
            RETURN s;
            """;

        tx.run(query, Values.parameters(
            "shelfId", shelfId,
            "shelfName", shelf.getName(),
            "shelfType", shelf.getShelfType()));
      });
    }
  }

  @Override
  public Map<String, Object> getShelfById(Long shelfId) {
    try (Session session = driver.session()) {
      return session.executeRead(tx -> {
        String query = """
            MATCH (s:ShelfV0 {id: $shelfId})-[r:HAS_SHELF_POSITION]->(sp:ShelfPositionV0)
            WHERE s.isDeleted = 'N' AND r.isDeleted = 'Y'
            RETURN s AS shelf, collect(sp.position) AS positions;
            """;

        Result result = tx.run(query, Values.parameters("shelfId", shelfId));
        if (!result.hasNext()) {
          throw new ShelfNotFoundException("Shelf with id " + shelfId + " is not present in the database");
        }

        Record record = result.single();
        Map<String, Object> shelfData = new HashMap<>(record.get("shelf").asNode().asMap());
        shelfData.put("positions", record.get("positions").asList());

        return shelfData;
      });
    }
  }

  public Optional<Map<String, Object>> getConnectedShelfPosition(Long deviceId) {
    try (Session session = driver.session()) {
      return session.executeRead(tx -> {
        String query = """
            MATCH (d:Device {id: $deviceId})-[:HAS_SHELF]->(s:ShelfV0)-[:HAS_SHELF_POSITION]->(sp:ShelfPositionV0)
            WHERE d.isDeleted = 'N' AND sp.isActive = 'Y'
            RETURN s.id AS shelfId, properties(sp) AS shelfPosition;
            """;

        Result result = tx.run(query, Values.parameters("deviceId", deviceId));

        if (result.hasNext()) {
          Record record = result.next();
          Map<String, Object> response = new HashMap<>();
          response.put("shelfId", record.get("shelfId").asLong());
          response.put("shelfPosition", record.get("shelfPosition").asMap());
          return Optional.of(response);
        }
        return Optional.empty();
      });
    }
  }

  public List<Map<String, Object>> getAvailableShelfPositions(Long shelfId) {
    List<Map<String, Object>> availablePositions = new ArrayList<>();

    try (Session session = driver.session()) {
      session.executeRead(tx -> {
        String query = """
            MATCH (s:ShelfV0 {id: $shelfId})-[:HAS_SHELF_POSITION]->(sp:ShelfPositionV0)
            WHERE sp.isActive = 'N'
            RETURN properties(sp) AS shelfPosition;
            """;

        Result result = tx.run(query, Values.parameters("shelfId", shelfId));

        while (result.hasNext()) {
          Record record = result.next();
          availablePositions.add(record.get("shelfPosition").asMap());
        }
        return availablePositions;
      });
    }

    return availablePositions;
  }

  public void addDeviceToShelfPosition(Long deviceId, Long shelfId, Map<String, Object> shelfPosition) {
    try (Session session = driver.session()) {
      session.executeWriteWithoutResult(tx -> {
        String query = """
            MATCH (s:ShelfV0 {id: $shelfId})-[:HAS_SHELF_POSITION]->(sp:ShelfPositionV0)
            MATCH (d:Device {id: $deviceId})
            WHERE d.isDeleted = 'N' AND sp.isActive = 'N'
            MERGE (d)-[r1:HAS_SHELF]->(s)
            ON CREATE SET r1.isDeleted = 'N'
            SET sp.isActive = 'Y'
            RETURN d, s, sp;
            """;

        tx.run(query, Values.parameters(
            "deviceId", deviceId,
            "shelfId", shelfId));
      });
    }
  }

  public void removeDeviceFromShelfPosition(Long deviceId) {
    try (Session session = driver.session()) {
      session.executeWriteWithoutResult(tx -> {
        String query = """
            MATCH (d:Device {id: $deviceId})-[r1:HAS_SHELF]->(s:ShelfV0)
            MATCH (s)-[:HAS_SHELF_POSITION]->(sp:ShelfPositionV0)
            WHERE d.isDeleted = 'N' AND sp.isActive = 'Y'
            SET r1.isDeleted = 'Y', sp.isActive = 'N'
            RETURN d, s, sp;
            """;

        tx.run(query, Values.parameters("deviceId", deviceId));
      });
    }
  }

}
