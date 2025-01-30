package com.example.demo.entity;

import java.util.List;

import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import lombok.Data;

// Represents a Device node in the Neo4j graph database
@Node
@Data
public class Device {

  @Id
  @GeneratedValue
  private long id;

  private String name;

  private String deviceType;

  // Relationship to ShelfPositionV0, indicating the device has shelf positions
  // (OUTGOING relationship)
  @Relationship(type = "HAS", direction = Relationship.Direction.OUTGOING)
  List<ShelfPositionV0> shelfPositions;
}
