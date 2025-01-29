package com.example.demo.entity;

import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import lombok.Data;

@Node
@Data

public class Device {

  @Id
  @GeneratedValue
  private long id;

  private String name;

  private String deviceType;
  @Relationship(type = "HAS", direction = Relationship.Direction.OUTGOING)
  private ShelfPositionV0 shelfPositionV0;

}
