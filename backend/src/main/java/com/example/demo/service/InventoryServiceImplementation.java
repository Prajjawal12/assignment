package com.example.demo.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.neo4j.driver.Driver;
import org.neo4j.driver.Record;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;
import org.neo4j.driver.Values;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.customExceptions.DeviceAlreadyAssignedToShelfPositionException;
import com.example.demo.customExceptions.ShelfNotFoundException;
import com.example.demo.customExceptions.ShelfPositionAlreadyOccupiedException;
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
                SET r.isDeleted = 'Y' , r.createdAt = datetime() , r.uuid = $uuid
            )
            RETURN s;
            """;

        tx.run(query, Values.parameters(
            "shelfId", shelf.getId(),
            "shelfName", shelf.getName(),
            "shelfType", shelf.getShelfType(),
            "associatedShelfPositions", shelf.getAssociatedShelfPositions(),
            "uuid", UUID.randomUUID().toString()));
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
            MATCH (s:ShelfV0 {id: $shelfId})-[:HAS_SHELF_POSITION]->(sp:ShelfPositionV0)
            WHERE s.isDeleted = 'N'
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

  public void addDeviceToShelfPosition(Long deviceId, Long shelfId, Long position) {
    try (Session session = driver.session()) {
      session.executeRead(tx -> {
        String checkQuery = """
            MATCH (d:Device {id: $deviceId})-[r1:HAS_SHELF]->(s:ShelfV0 {id:$shelfId})-[r2:HAS_SHELF_POSITION]->(sp:ShelfPositionV0 {position : $position})
            WHERE d.isDeleted = 'N' AND r1.isDeleted = 'N' AND r2.isDeleted = 'N' AND sp.isActive = 'Y'
            RETURN COUNT(d) > 0 AS deviceAlreadyAssigned;
            """;

        Result checkResult = tx.run(checkQuery,
            Values.parameters("deviceId", deviceId, "shelfId", shelfId, "position", position));
        boolean deviceAlreadyAssigned = checkResult.single().get("deviceAlreadyAssigned").asBoolean();
        if (deviceAlreadyAssigned) {
          throw new DeviceAlreadyAssignedToShelfPositionException(
              "Device with ID: " + deviceId + " is already assigned to this shelf position");
        }

        String occupiedQuery = """
            MATCH (:Device)-[r1:HAS_SHELF]->(:ShelfV0 {id:$shelfId})-[r2:HAS_SHELF_POSITION]->(sp:ShelfPosition {position: $position})
            WHERE r1.isDeleted = 'N' AND r2.isDeleted = 'N' AND sp.isActive = 'Y'
            RETURN COUNT(sp) > 0 AS positionOccupied;
            """;

        Result occupiedResult = tx.run(occupiedQuery, Values.parameters("shelfId", shelfId, "position", position));
        boolean positionOccupied = occupiedResult.single().get("positionOccupied").asBoolean();

        if (positionOccupied) {
          throw new ShelfPositionAlreadyOccupiedException("Shelf Position " + position + " belong to Shelf with ID "
              + shelfId + " is already assigned to a device");
        }

        return null;
      });

      session.executeWriteWithoutResult(tx -> {
        String assignDeviceToShelfQuery = """
            MATCH(d:Device {id:$deviceId}) , (s:ShelfV0 {id:$shelfId})
            WHERE d.isDeleted = 'N'
            CREATE (d)-[r1:HAS_SHELF]->(s)
            SET r1.isDeleted = 'N'
             RETURN d,s;
            """;

        tx.run(assignDeviceToShelfQuery, Values.parameters("deviceId", deviceId, "shelfId", shelfId));
      });

      session.executeWriteWithoutResult(tx -> {
        String assignShelfPositionQuery = """
            MATCH (s:ShelfV0 {id:$shelfId})-[r2:HAS_SHELF_POSITION]->(sp:ShelfPositionV0 {position : $position})
            WHERE sp.isActive = 'N'
            OPTIONAL MATCH (s)-[r2:HAS_SHELF_POSITION]->(sp)
            WHERE r2.isDeleted = 'Y'

            WITH s , sp , r2
            WHERE r2 IS NOT NULL


            SET r2.isDeleted = 'N' , sp.isActive = 'Y' , r2.createdAt = datetime() , r2.uuid = $uuid
            WITH s, sp
            WHERE r2 IS NULL

            CREATE (s)-[r3:HAS_SHELF_POSITION]->(sp)
            SET r3.isDeleted = 'N' , sp.isActive = 'Y' , r3.createdAt = datetime() , r3.uuid = $uuid

            RETURN s , sp;
            """;
        tx.run(assignShelfPositionQuery,
            Values.parameters("shelfId", shelfId, "position", position, "uuid", UUID.randomUUID().toString()));
      });
    }
  }

  public List<Map<String, Object>> getAllConnectedShelfPositions() {
    try (Session session = driver.session()) {
      return session.executeRead(tx -> {
        String query = """
            MATCH (d:Device)-[r1:HAS_SHELF]->(s:ShelfV0)-[r2:HAS_SHELF_POSITION]->(sp:ShelfPositionV0)
            WHERE d.isDeleted = 'N' AND sp.isActive = 'Y' AND r1.isDeleted = 'N' AND r2.isDeleted = 'N'
            RETURN d.id AS deviceId , s.id AS shelfId , sp.position AS shelfPosition, r1.uuid AS relationId1 , r2.uuid AS relationId2;
            """;
        Result result = tx.run(query);
        List<Map<String, Object>> results = new ArrayList<>();
        while (result.hasNext()) {
          Record record = result.next();

          Map<String, Object> recordMap = new HashMap<>();
          recordMap.put("deviceId", record.get("deviceId").asLong());
          recordMap.put("shelfId", record.get("shelfId").asLong());
          recordMap.put("position", record.get("shelfPosition").asLong());
          recordMap.put("relationId1", record.get("relationId1").asLong());
          recordMap.put("relationId2", record.get("relationId2").asLong());
          results.add(recordMap);
        }
        return results;
      });
    }
  }

  public void removeDeviceFromShelfPosition(Long relationId1, Long relationId2) {
    try (Session session = driver.session()) {
      session.executeWriteWithoutResult(tx -> {
        String query = """
            MATCH ()-[r1:HAS_SHELF]->(s:ShelfV0)-[r2:HAS_SHELF_POSITION]->(sp:ShelfPositionV0)
            WHERE id(r1) = $relationId1 AND id(r2)=$relationId2
            SET r1.isDeleted = 'Y' , r2.isDeleted = 'Y' , sp.isActive = 'N'

                """;

        tx.run(query, Values.parameters("relationId1", relationId1, "relationId2", relationId2));
      });
    }
  }

  public List<Map<String, Object>> getAvailableShelfPositions(Long shelfId) {
    List<Map<String, Object>> availablePositions = new ArrayList<>();

    try (Session session = driver.session()) {
      session.executeRead(tx -> {
        String query = """
            MATCH (s:ShelfV0 {id: $shelfId})-[r:HAS_SHELF_POSITION]->(sp:ShelfPositionV0)
            WHERE sp.isActive = 'N' AND r.isDeleted = 'Y'
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

  public List<Map<String, Object>> getAllShelves() {
    try (Session session = driver.session()) {
      return session.executeRead(tx -> {
        String query = """
            MATCH (s:ShelfV0)
            WHERE s.isDeleted = 'N'
            RETURN s;
            """;

        Result result = tx.run(query);
        return result.list(record -> record.get("s").asNode().asMap());
      });
    }
  }

}
