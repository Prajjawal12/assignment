package com.example.demo.entity;

import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;

import com.example.demo.enums.DeviceStatus;

import lombok.Data;

// Represents a Device node in the Neo4j graph database
@Node
@Data
public class Device {

  @Id
  @GeneratedValue
  private Long id;

  private String name;

  private String deviceType;

  private DeviceStatus deviceStatus;
}