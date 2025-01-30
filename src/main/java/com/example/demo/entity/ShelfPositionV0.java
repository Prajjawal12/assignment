package com.example.demo.entity;

import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import lombok.Data;

// Represents a ShelfPositionV0 node in the Neo4j graph database
@Node
@Data
public class ShelfPositionV0 {

  @Id
  @GeneratedValue
  private long id;

  private String name;

  private long deviceId;

  // Relationship to ShelfV0, indicating that this shelf position has a shelf
  // (OUTGOING relationship)
  @Relationship(type = "HAS", direction = Relationship.Direction.OUTGOING)
  private ShelfV0 shelfV0; // Shelf associated with this shelf position
}
