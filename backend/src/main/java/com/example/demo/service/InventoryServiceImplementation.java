package com.example.demo.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.neo4j.driver.Driver;
import org.neo4j.driver.Record;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;
import org.neo4j.driver.Values;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.customExceptions.ShelfNotFoundException;
import com.example.demo.entity.ShelfV0;

@Service
public class InventoryServiceImplementation implements InventoryService {

  @Autowired
  private Driver driver;

  @Override
  @Transactional(propagation = Propagation.REQUIRED)

  public Map<String, Object> saveShelf(ShelfV0 shelf, boolean confirmModification) {

    if (confirmModification) {
      modifyShelf(shelf.getId(), shelf);
    } else {
      createShelf(shelf);
    }
    return getShelfById(shelf.getId());

  }

  @Override
  @Transactional(propagation = Propagation.REQUIRED)
  public void createShelf(ShelfV0 shelf) {
    try (Session session = driver.session()) {
      session.executeWriteWithoutResult(tx -> {
        String query = """
            CREATE (s: ShelfV0 {id: $shelfId})
            SET s.name = $shelfName, s.shelfType = $shelfType, s.isDeleted = 'N'
            WITH s, range(1, $associatedShelfPositions) AS positionNumbers
            FOREACH (position IN positionNumbers |
                CREATE (sp: ShelfPositionV0 {position: position , deviceAssigned : null})
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
  @Transactional(propagation = Propagation.REQUIRED)
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
  @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
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

  @Override
  @Transactional(propagation = Propagation.REQUIRED)
  public void addDeviceToShelfPosition(Long deviceId, Long shelfId, Long position) {
    try (Session session = driver.session()) {

      session.executeWriteWithoutResult(tx -> {
        String assignDeviceToShelfQuery = """
            MATCH(d:Device {id:$deviceId}) , (s:ShelfV0 {id:$shelfId})
            WHERE d.isDeleted = 'N'
            CREATE (d)-[r1:HAS_SHELF]->(s)
            SET r1.isDeleted = 'N'
            RETURN d,s;
            """;

        tx.run(assignDeviceToShelfQuery,
            Values.parameters("deviceId", deviceId, "shelfId", shelfId));

        String assignShelfPositionQuery = """
            MATCH (s:ShelfV0 {id:$shelfId})-[r2:HAS_SHELF_POSITION]->(sp:ShelfPositionV0 {position : $position})
            SET sp.deviceAssigned = $deviceId , r2.isDeleted = 'N'
            RETURN s, sp;
              """;
        tx.run(assignShelfPositionQuery,
            Values.parameters("deviceId", deviceId, "shelfId", shelfId, "position", position));
      });

    }
  }

  @Override
  @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
  public List<Map<String, Object>> getAllConnectedShelfPositions() {
    try (Session session = driver.session()) {
      return session.executeRead(tx -> {
        String query = """
            MATCH (d:Device)-[r1:HAS_SHELF]->(s:ShelfV0)-[r2:HAS_SHELF_POSITION]->(sp:ShelfPositionV0)
            WHERE d.isDeleted = 'N' AND r1.isDeleted = 'N' AND r2.isDeleted = 'N' AND sp.deviceAssigned = d.id
            RETURN d.id AS deviceId , s.id AS shelfId , sp.position AS shelfPosition;
            """;
        Result result = tx.run(query);
        List<Map<String, Object>> results = new ArrayList<>();
        while (result.hasNext()) {
          Record record = result.next();

          Map<String, Object> recordMap = new HashMap<>();
          recordMap.put("deviceId", record.get("deviceId").asLong());
          recordMap.put("shelfId", record.get("shelfId").asLong());
          recordMap.put("position", record.get("shelfPosition").asLong());
          results.add(recordMap);
        }
        return results;
      });
    }
  }

  @Override
  @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
  public void removeDeviceFromShelfPosition(Long deviceId, Long shelfId, Long position) {
    try (Session session = driver.session()) {
      session.executeWriteWithoutResult(tx -> {
        String query = """
            MATCH (d:Device {id:$deviceId})-[r1:HAS_SHELF]->(s:ShelfV0 {id:$shelfId})-[r2:HAS_SHELF_POSITION]->(sp:ShelfPositionV0 {position:$position , deviceAssigned : $deviceId})
            WHERE r1.isDeleted = 'N' AND r2.isDeleted = 'N' AND sp.deviceAssigned = d.id AND d.isDeleted = 'N'
            SET sp.deviceAssigned = null , r2.isDeleted = 'Y' , r1.isDeleted = 'Y'
                """;

        tx.run(query, Values.parameters("deviceId", deviceId, "shelfId", shelfId, "position", position));
      });
    }
  }

  @Override
  @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
  public List<Map<String, Object>> getAvailableShelfPositions(Long shelfId) {
    List<Map<String, Object>> availablePositions = new ArrayList<>();

    try (Session session = driver.session()) {
      session.executeRead(tx -> {
        String query = """
            MATCH (s:ShelfV0 {id: $shelfId})-[r:HAS_SHELF_POSITION]->(sp:ShelfPositionV0)
            WHERE sp.deviceAssigned IS NULL AND r.isDeleted = 'Y'
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

  @Override
  @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
  public List<Map<String, Object>> getAllShelves() {
    try (Session session = driver.session()) {
      return session.executeRead(tx -> {
        String query = """
            MATCH (s:ShelfV0)
            WHERE s.isDeleted = 'N' AND EXISTS {
            (s)-[r:HAS_SHELF_POSITION]->(sp)
            WHERE r.isDeleted = 'Y' AND sp.deviceAssigned IS NULL
            }
            RETURN s;
            """;

        Result result = tx.run(query);
        return result.list(record -> record.get("s").asNode().asMap());
      });
    }
  }

}
