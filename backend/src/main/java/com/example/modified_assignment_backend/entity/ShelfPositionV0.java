package com.example.modified_assignment_backend.entity;

import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;

import lombok.Data;

@Data
@Node
public class ShelfPositionV0 {

    @Id
    @GeneratedValue
    private Long shelfPositionId;

    private String shelfPositionName;
}
