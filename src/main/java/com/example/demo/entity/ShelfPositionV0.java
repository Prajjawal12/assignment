package com.example.demo.entity;

import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;

import lombok.Data;

@Node
@Data
public class ShelfPositionV0 {

  @Id
  @GeneratedValue
  private long id;

  private String name;

  private long deviceId;
}
