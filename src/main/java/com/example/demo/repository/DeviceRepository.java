package com.example.demo.repository;

import org.springframework.data.neo4j.repository.Neo4jRepository;

import com.example.demo.entity.Device;

public interface DeviceRepository extends Neo4jRepository<Device, Long> {

}
