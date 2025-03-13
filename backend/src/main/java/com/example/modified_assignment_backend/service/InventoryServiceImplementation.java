package com.example.modified_assignment_backend.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.neo4j.driver.Driver;
import org.neo4j.driver.Record;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;
import org.neo4j.driver.Values;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.modified_assignment_backend.customExceptions.DeviceNotFoundException;
import com.example.modified_assignment_backend.customExceptions.RecordNotFoundException;
import com.example.modified_assignment_backend.entity.ShelfPositionV0;
import com.example.modified_assignment_backend.entity.ShelfV0;

@Service
public class InventoryServiceImplementation implements InventoryService {
    public static final Logger logger = LoggerFactory.getLogger(InventoryServiceImplementation.class);
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

                logger.info("value of sheld id here is {}", shelf.getId());
                Result result = tx.run(query,
                        Values.parameters("shelfId", shelf.getId(), "shelfName", shelf.getName(),
                                "shelfType", shelf.getShelfType()));

                if (!result.hasNext()) {
                    throw new RecordNotFoundException("Failed to save Shelf with ID: " + shelf.getId());
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
                        "shelfPositionId", shelfPositionV0.getId(),
                        "shelfPositionName", shelfPositionV0.getName()));

                if (!result.hasNext()) {
                    throw new RecordNotFoundException(
                            "Failed to save Shelf Position with ID: " + shelfPositionV0.getId());
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
                        MATCH (d:Device {id:$deviceId}) RETURN count(d) AS deviceCount;
                        """;
                Result result = tx.run(query, Values.parameters("deviceId", deviceId));

                int deviceCount = result.single().get("deviceCount").asInt();

                if (deviceCount == 0) {
                    throw new DeviceNotFoundException("Device with Id " + deviceId + " does not exist in the database");
                }

                String checkShelfPositionQuery = """
                        MATCH (sp:ShelfPositionV0 {id:$shelfPositionId})
                        RETURN count(sp) AS shelfPositionCount;
                        """;
                Result result2 = tx.run(checkShelfPositionQuery, Values.parameters("shelfPositionId", shelfPositionId));

                int shelfPositionCount = result2.single().get("shelfPositionCount").asInt();

                if (shelfPositionCount == 0) {
                    throw new RuntimeException(
                            "Shelf Position with Id " + shelfPositionId + " does not exist in the database");
                }

                return null;
            });

            session.executeWriteWithoutResult(tx -> {
                String query = """
                        MATCH (d:Device {id:$deviceId}) , (sp:ShelfPositionV0 {id:$shelfPositionId})
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

                String checkShelfQuery = """
                        MATCH (s:ShelfV0 {id:$shelfId})
                        RETURN count(s) AS shelfNodes;
                        """;

                if (tx.run(checkShelfQuery, Values.parameters("shelfId", shelfId)).single().get("shelfNodes")
                        .asInt() == 0) {
                    throw new RuntimeException("Shelf with Id " + shelfId + " is not present in the database");
                }

                String checkShelfPositionQuery = """
                        MATCH (s:ShelfPositionV0 {id:$shelfPositionId})
                        RETURN count(s) AS shelfPositionNodes;
                        """;

                if (tx.run(checkShelfPositionQuery, Values.parameters("shelfPositionId", shelfPositionId)).single()
                        .get("shelfPositionNodes")
                        .asInt() == 0) {
                    throw new RuntimeException(
                            "Shelf Position with Id " + shelfPositionId + " is not present in the database");
                }

                String checkExistingShelfPositionRelationQuery = """
                        MATCH (sp:ShelfPositionV0 {id:$shelfPositionId})-[:HAS_SHELF]->()
                        RETURN count(sp) > 0 AS existingShelfPositionRelations;
                        """;
                boolean hasRelations = tx.run(checkExistingShelfPositionRelationQuery,
                        Values.parameters("shelfId", shelfId, "shelfPositionId", shelfPositionId)).single()
                        .get("existingShelfPositionRelations").asBoolean();
                if (hasRelations) {
                    throw new RuntimeException(
                            "There is already an existing relationship between specified shelf position node and other shelf node");
                }

                String checkExistingShelfRelationQuery = """
                        MATCH (s:ShelfV0 {id:$shelfId})-[:HAS_SHELF_POSITION]->()
                        RETURN count(*) > 0 AS existingShelfRelations;
                        """;
                boolean numRelations = tx.run(checkExistingShelfRelationQuery,
                        Values.parameters("shelfId", shelfId, "shelfPositionId", shelfPositionId)).single()
                        .get("existingShelfRelations").asBoolean();
                if (numRelations) {
                    throw new RuntimeException(
                            "There is already an existing relationship between specified shelf node and other shelf position node");
                }

                return null;
            });

            session.executeWriteWithoutResult(tx -> {
                String query = """
                        MATCH (sp:ShelfPositionV0 {id:$shelfPositionId}) , (s:ShelfV0 {id: $shelfId})
                        MERGE (sp)-[:HAS_SHELF]->(s)
                        MERGE (s)-[:HAS_SHELF_POSITION]->(sp)
                        RETURN sp,s;
                        """;

                tx.run(query, Values.parameters("shelfPositionId", shelfPositionId, "shelfId", shelfId));

            });

        }
    }

    @Override
    public List<Map<String, Object>> listAllShelf() {
        try (Session session = driver.session()) {
            List<Map<String, Object>> list = session.executeRead(tx -> {
                String query = "MATCH (s:ShelfV0) RETURN s AS shelfNodes";

                Result result = tx.run(query);
                List<Map<String, Object>> shelfList = new ArrayList<>();
                while (result.hasNext()) {
                    Record record = result.next();
                    Map<String, Object> shelf = record.get("shelfNodes").asNode().asMap();
                    shelfList.add(shelf);
                }
                return shelfList;
            });

            return list;
        }
    }

    @Override
    public Map<String, Object> listAssociatedShelfDetails(Long shelfId) {
        try (Session session = driver.session()) {
            Map<String, Object> shelfDetails = session.executeRead(tx -> {
                String query = """
                            MATCH (s:ShelfV0 {id:$shelfId})
                            MATCH (s)-[:HAS_SHELF_POSITION]->(sp:ShelfPositionV0)
                            WITH s,sp
                            MATCH (sp)-[:HAS_DEVICE]->(d:Device)
                            WITH s , sp , d
                            RETURN s , sp  , d;
                        """;

                Result result = tx.run(query, Values.parameters("shelfId", shelfId));
                Record record = result.single();
                Map<String, Object> consolidatedInfo = new HashMap<>();

                Map<String, Object> shelfMap = record.get("s").asNode().asMap();

                Map<String, Object> shelfPositionMap = record.get("sp").asNode().asMap();

                Map<String, Object> deviceMap = record.get("d").asNode().asMap();

                consolidatedInfo.put("deviceDetails", deviceMap);
                consolidatedInfo.put("shelfDetails", shelfMap);
                consolidatedInfo.put("shelfPositionDetails", shelfPositionMap);

                return consolidatedInfo;
            });
            return shelfDetails;
        }
    }

}
