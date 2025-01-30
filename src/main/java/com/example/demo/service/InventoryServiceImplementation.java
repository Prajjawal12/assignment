package com.example.demo.service;

import java.util.Map;
import org.neo4j.driver.Driver;
import org.neo4j.driver.Values;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.example.demo.config.DatabaseConfig;
import com.example.demo.customExceptions.RecordNotFoundException;
import com.example.demo.entity.ShelfPositionV0;
import com.example.demo.entity.ShelfV0;

@Service
public class InventoryServiceImplementation implements InventoryService {
  private static final Logger logger = LoggerFactory.getLogger(InventoryServiceImplementation.class);
  private final Driver driver = DatabaseConfig.getDriver();

  // Save a new shelf to the database
  @Override
  public Map<String, Object> saveShelf(ShelfV0 shelf) {
    logger.info("Saving shelf with ID: {}", shelf.getId());
    try (var session = driver.session()) {
      var res = session.executeWrite((tx) -> {
        String query = """
            MERGE (s:ShelfV0 {id:$shelfId})
            SET s.name = $shelfName
            RETURN s AS savedShelf;
            """;

        var record = tx.run(query, Values.parameters("shelfId", shelf.getId(), "shelfName", shelf.getName(),
            "shelfType", shelf.getShelfType()));

        logger.info("Shelf with ID {} saved successfully", shelf.getId());
        return record.single().get("savedShelf").asNode().asMap();
      });
      return res;
    } catch (Exception e) {
      logger.error("Error while saving shelf with ID {}: {}", shelf.getId(), e.getMessage(), e);
      throw e;
    }
  }

  // Retrieve a shelf by its ID
  @Override
  public Map<String, Object> getShelf(Long shelfId) {
    logger.info("Fetching shelf with ID: {}", shelfId);
    try (var session = driver.session()) {
      var res = session.executeRead(tx -> {
        String query = """
            MATCH (s:ShelfV0 {id:$shelfId})
            RETURN s;
            """;

        var record = tx.run(query, Values.parameters("shelfId", shelfId));
        if (record == null) {
          logger.warn("Shelf with ID {} not found", shelfId);
          throw new RecordNotFoundException("No ShelfV0 is present with the id: " + shelfId);
        }

        logger.info("Shelf with ID {} fetched successfully", shelfId);
        return record.single().get("s").asNode().asMap();
      });
      return res;
    } catch (Exception e) {
      logger.error("Error while fetching shelf with ID {}: {}", shelfId, e.getMessage(), e);
      throw e;
    }
  }

  // Save a new shelf position to the database
  @Override
  public Map<String, Object> saveShelfPosition(ShelfPositionV0 shelfPosition) {
    logger.info("Saving shelf position with ID: {}", shelfPosition.getId());
    try (var session = driver.session()) {
      var res = session.executeWrite(tx -> {
        String query = """
            MERGE (s:ShelfPositionV0 {id:$shelfPositionId})
            SET s.name = $shelfPositionName
            RETURN s as shelfPositionName
               """;

        var record = tx.run(query, Values.parameters(
            "shelfPositionId", shelfPosition.getId(),
            "shelfPositionName", shelfPosition.getName()));

        logger.info("Shelf position with ID {} saved successfully", shelfPosition.getId());
        return record.single().get("shelfPositionName").asNode().asMap();
      });
      return res;
    } catch (Exception e) {
      logger.error("Error while saving shelf position with ID {}: {}", shelfPosition.getId(), e.getMessage(), e);
      throw e;
    }
  }

  // Retrieve a shelf position by its ID
  @Override
  public Map<String, Object> getShelfPosition(long shelfPositionId) {
    logger.info("Fetching shelf position with ID: {}", shelfPositionId);
    try (var session = driver.session()) {
      var res = session.executeRead(tx -> {
        String query = """
            MATCH (s:ShelfPositionV0 {id:$shelfPositionId})
            RETURN s;
            """;
        var record = tx.run(query, Values.parameters("shelfPositionId", shelfPositionId));
        if (record == null) {
          logger.warn("Shelf position with ID {} not found", shelfPositionId);
          throw new RecordNotFoundException("No ShelfPositionV0 is present with the id: " + shelfPositionId);
        }
        logger.info("Shelf position with ID {} fetched successfully", shelfPositionId);
        return record.single().get("s").asNode().asMap();
      });
      return res;
    } catch (Exception e) {
      logger.error("Error while fetching shelf position with ID {}: {}", shelfPositionId, e.getMessage(), e);
      throw e;
    }
  }

  // Assign a shelf position to a device
  @Override
  public void addShelfPositionToDevice(Long deviceId, Long shelfPositionId) {
    logger.info("Assigning shelf position with ID {} to device with ID {}", shelfPositionId, deviceId);
    try (var session = driver.session()) {
      session.executeWriteWithoutResult(tx -> {
        String query = """
                MATCH (d:Device {id: $deviceId}), (s: ShelfPositionV0 {id: $shelfPositionId})
                MERGE (d)-[:HAS]->(s)
                SET s.deviceId = $deviceId
                RETURN d AS device, s AS shelf
            """;

        tx.run(query, Values.parameters("deviceId", deviceId, "shelfPositionId", shelfPositionId));
        logger.info("Shelf position with ID {} successfully assigned to device with ID {}", shelfPositionId, deviceId);
      });
    } catch (Exception e) {
      logger.error("Error while assigning shelf position with ID {} to device with ID {}: {}", shelfPositionId,
          deviceId, e.getMessage(), e);
      throw e;
    }
  }

  // Assign a shelf to a shelf position
  @Override
  public void addShelfToShelfPosition(Long shelfId, Long shelfPositionId) {
    logger.info("Assigning shelf with ID {} to shelf position with ID {}", shelfId, shelfPositionId);
    try (var session = driver.session()) {
      session.executeWriteWithoutResult(tx -> {
        String query = """
                MATCH (sp: ShelfPositionV0 {id: $shelfPositionId})
                MATCH (s: ShelfV0 {id: $shelfId})
                MERGE (sp)-[:HAS]->(s)
                SET s.shelfPositionId = $shelfPositionId
                RETURN sp AS shelfPosition, s AS shelfNode;
            """;

        tx.run(query, Values.parameters("shelfPositionId", shelfPositionId, "shelfId", shelfId));
        logger.info("Shelf with ID {} successfully assigned to shelf position with ID {}", shelfId, shelfPositionId);
      });
    } catch (Exception e) {
      logger.error("Error while assigning shelf with ID {} to shelf position with ID {}: {}", shelfId, shelfPositionId,
          e.getMessage(), e);
      throw e;
    }
  }
}
