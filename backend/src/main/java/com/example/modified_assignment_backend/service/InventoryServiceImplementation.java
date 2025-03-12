package com.example.modified_assignment_backend.service;

import java.util.Map;

import org.neo4j.driver.Driver;
import org.neo4j.driver.Record;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;
import org.neo4j.driver.Values;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.modified_assignment_backend.customExceptions.DeviceNotFoundException;
import com.example.modified_assignment_backend.customExceptions.RecordNotFoundException;
import com.example.modified_assignment_backend.entity.ShelfPositionV0;
import com.example.modified_assignment_backend.entity.ShelfV0;

@Service
public class InventoryServiceImplementation implements InventoryService {

    @Autowired
    private Driver driver;

    @Override
    public Map<String, Object> saveShelf(ShelfV0 shelf) {
        try (var session = driver.session()) {
            return session.executeWrite((tx) -> {
                String query = """
                        MERGE (s:ShelfV0 {id:$shelfId})
                        SET s.name = $shelfName, s.shelfType = $shelfType
                        RETURN s AS savedShelf;
                        """;

                Result result = tx.run(query,
                        Values.parameters("shelfId", shelf.getShelfId(), "shelfName", shelf.getShelfName(),
                                "shelfType", shelf.getShelfType()));

                if (!result.hasNext()) {
                    throw new RecordNotFoundException("Failed to save Shelf with ID: " + shelf.getShelfId());
                }

                return result.single().get("savedShelf").asNode().asMap();
            });
        }
    }

    @Override
    public Map<String, Object> getShelf(Long shelfId) {
        try (Session session = driver.session()) {
            return session.executeRead(tx -> {
                String query = """
                        MATCH (s:ShelfV0 {id:$shelfId})
                        RETURN s;
                        """;

                Result result = tx.run(query, Values.parameters("shelfId", shelfId));
                if (!result.hasNext()) {
                    throw new RecordNotFoundException("No ShelfV0 is present with the id: " + shelfId);
                }

                return result.single().get("s").asNode().asMap();
            });
        }
    }

    @Override
    public Map<String, Object> saveShelfPosition(ShelfPositionV0 shelfPositionV0) {
        try (Session session = driver.session()) {
            return session.executeWrite(tx -> {
                String query = """
                        MERGE (s:ShelfPositionV0 {id:$shelfPositionId})
                        SET s.name = $shelfPositionName
                        RETURN s as shelfPositionName
                           """;

                Result result = tx.run(query, Values.parameters(
                        "shelfPositionId", shelfPositionV0.getShelfPositionId(),
                        "shelfPositionName", shelfPositionV0.getShelfPositionName()));

                if (!result.hasNext()) {
                    throw new RecordNotFoundException(
                            "Failed to save Shelf Position with ID: " + shelfPositionV0.getShelfPositionId());
                }

                return result.single().get("shelfPositionName").asNode().asMap();

            });
        }
    }

    @Override
    public Map<String, Object> getShelfPosition(long shelfPositionId) {
        try (Session session = driver.session()) {
            return session.executeRead(tx -> {
                String query = """
                        MATCH (s:ShelfPositionV0 {id:$shelfPositionId})
                        RETURN s;
                        """;
                Result result = tx.run(query, Values.parameters("shelfPositionId", shelfPositionId));
                if (!result.hasNext()) {
                    throw new RecordNotFoundException("No ShelfPositionV0 is present with the id: " + shelfPositionId);
                }
                return result.single().get("s").asNode().asMap();
            });
        }
    }

    // one device can have many shelf positions
    @Override
    public void addShelfPositionToDevice(Long deviceId, Long shelfPositionId) {
        try (Session session = driver.session()) {
            session.executeRead(tx -> {
                String query = """
                        MATCH (d:Device {id:$deviceId}), (s:ShelfPositionV0 {id:$shelfPositionId})
                        RETURN count(d) AS deviceCount , count(s) AS shelfPositionCount;
                        """;
                Result result = tx.run(query,
                        Values.parameters("deviceId", deviceId, "shelfPositionId", shelfPositionId));
                Record record = result.single();
                int deviceCount = record.get("deviceCount").asInt();
                int shelfPositionCount = record.get("shelfPositionCount").asInt();

                if (deviceCount == 0) {
                    throw new DeviceNotFoundException("Device with ID " + deviceId + " does not exist in the database");
                }

                if (shelfPositionCount == 0) {
                    throw new RecordNotFoundException(
                            "Shelf Position with Id " + shelfPositionId + " does not exist in the database");
                }

                return null;
            });

            session.executeWriteWithoutResult(tx -> {
                String query = """
                        MATCH (d:Device {id:$deviceId}) , (sp:ShelfPosition {id:$shelfPositionId})
                        MERGE (d)-[:HAS_SHELF_POSITION]->(sp)
                        MERGE (sp)-[:HAS_DEVICE]->(d)
                        RETURN d,sp;
                        """;

                tx.run(query, Values.parameters("deviceId", deviceId, "shelfPositionId", shelfPositionId));

            });
        }
    }

    @Override
    public void addShelfToShelfPosition(Long shelfId, Long shelfPositionId) {
        try (Session session = driver.session()) {
            session.executeRead(tx -> {
                String query = """
                        MATCH (s:ShelfV0 {id:$shelfId})
                        MATCH (sp:ShelfPositionV0 {id:$shelfPositionId})
                        RETURN count(s) AS shelves , count(sp) AS shelfPositions;
                        """;

                Result result = tx.run(query,
                        Values.parameters("shelfId", shelfId, "shelfPositionId", shelfPositionId));
                Record record = result.single();
                int shelfCount = record.get("shelves").asInt();
                int shelfPositionCount = record.get("shelfPositions").asInt();
                if (shelfCount == 0) {
                    throw new RecordNotFoundException(
                            "No shelf node is present with Id " + shelfId + " in the database");
                }

                if (shelfPositionCount == 0) {
                    throw new RuntimeException(
                            "No shelf position node with ID " + shelfPositionId + " is present in the database");
                }

                String relationCheckQuery = """
                        MATCH (sp:ShelfPosition {id:shelfPositionId})-[r]-(s:ShelfV0 {id:$shelfId})
                        RETURN count(r) AS relationCount;
                        """;
                Result result2 = tx.run(relationCheckQuery,
                        Values.parameters("shelfPositionId", shelfPositionId, "shelfId", shelfId));

                Record record2 = result2.single();
                int relationCount = record2.get("relationCount").asInt();

                if (relationCount > 0) {
                    throw new RuntimeException("Relation already exists between Shelf and ShelfPosition");
                }

                return null;
            });

            session.executeWriteWithoutResult(tx -> {
                String query = """
                        MATCH (sp:ShelfPositionV0 {id:shelfPositionId}) , (s:ShelfV0 {id: $shelfId})
                        MERGE (sp)-[:HAS_SHELF]->(s)
                        MERGE (s)-[:HAS_SHELF_POSITION]->(sp)
                        RETURN sp,s;
                        """;

                tx.run(query, Values.parameters("shelfPositionId", shelfPositionId, "shelfId", shelfId));

            });

        }
    }

}
