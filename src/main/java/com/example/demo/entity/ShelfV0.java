package com.example.demo.entity;

import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import lombok.Data;

@Node
@Data
public class ShelfV0 {

  @Id
  @GeneratedValue
  private long id;

  private String name;

  private String shelfType;

  private long shelfPositionId;

  // Relationship to another ShelfV0, indicating that this shelf has a shelf
  // (OUTGOING relationship)
  @Relationship(type = "HAS", direction = Relationship.Direction.OUTGOING)
  private ShelfV0 shelfV0; // Another shelf associated with this shelf
}
