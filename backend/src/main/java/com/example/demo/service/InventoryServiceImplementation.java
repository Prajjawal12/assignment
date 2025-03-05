package com.example.demo.service;

import java.util.HashMap;
import java.util.Map;
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
    try (Session session = driver.session()) {
      return session.executeWrite(tx -> {
        if (confirmModification) {
          modifyShelf(shelf.getId(), shelf);
        } else {
          createShelf(shelf);
        }
        return getShelfById(shelf.getId());
      });
    }
  }

  @Override
  public void createShelf(ShelfV0 shelf) {
    try (Session session = driver.session()) {
      session.executeWriteWithoutResult(tx -> {
        String query = """
            CREATE (s: Shelf {id: $shelfId})
            SET s.name = $shelfName, s.shelfType = $shelfType, s.isDeleted = 'N'
            WITH s, range(1, $associatedShelfPositions) AS positionNumbers
            FOREACH (position IN positionNumbers |
                CREATE (sp: ShelfPosition {position: position})
                MERGE (s)-[r:HAS_POSITION]->(sp)
                SET r.isDeleted = 'N'
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
            MATCH (s: Shelf {id: $shelfId})
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
            MATCH (s:Shelf {id: $shelfId})-[:HAS_POSITION]->(sp:ShelfPosition)
            WHERE s.isDeleted = 'N'
            RETURN s AS shelf, collect(sp.position) AS positions
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

}
