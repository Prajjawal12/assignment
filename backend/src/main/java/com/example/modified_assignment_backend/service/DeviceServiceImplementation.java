package com.example.modified_assignment_backend.service;

import java.util.Map;

import org.neo4j.driver.Driver;
import org.neo4j.driver.Record;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;
import org.neo4j.driver.Values;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.modified_assignment_backend.customExceptions.DeviceNotFoundException;
import com.example.modified_assignment_backend.entity.Device;

@Service
public class DeviceServiceImplementation implements DeviceService {

    // Neo4j driver instance for interacting with the Neo4j database
    @Autowired
    private Driver driver;

    @Override
    public Map<String, Object> saveDevice(Device device) {
        try (Session session = driver.session()) {
            Map<String, Object> res = session.executeWrite(tx -> {
                String query = """
                        MERGE (d:Device {id:$deviceId})
                        SET d.name = $deviceName, d.deviceType = $deviceType
                        RETURN d AS savedDevice
                           """;

                Record record = tx.run(query, Values.parameters("deviceId", device.getId(),
                        "deviceName", device.getName(), "deviceType", device.getDeviceType())).single();

                return record.get("savedDevice").asNode().asMap();
            });
            return res;
        }
    }

    @Override
    public Map<String, Object> getDevice(Long deviceId) {
        try (Session session = driver.session()) {
            Map<String, Object> res = session.executeRead(tx -> {
                String query = """
                        MATCH (d:Device {id:$deviceId})
                        RETURN d AS deviceFound;
                          """;

                Result result = tx.run(query, Values.parameters("deviceId", deviceId));

                if (!result.hasNext()) {
                    throw new DeviceNotFoundException("Device with ID " + deviceId + " not found.");
                }

                return result.single().get("deviceFound").asNode().asMap();
            });
            return res;
        }
    }

    @Override
    public Map<String, Object> modifyDevice(Long id, Device device) {
        try (Session session = driver.session()) {
            // Perform write transaction to modify the device by its ID
            Map<String, Object> res = session.executeWrite(tx -> {
                String query = """
                        MATCH (d:Device {id: $deviceId})
                        SET d.name = $deviceName, d.deviceType = $deviceType
                        RETURN d AS modifiedDevice;
                            """;

                Result result = tx.run(query, Values.parameters("deviceId", id,
                        "deviceName", device.getName(), "deviceType", device.getDeviceType()));

                return result.single().get("modifiedDevice").asNode().asMap();
            });
            return res;
        }
    }

    @Override
    public Long deleteDevice(Long deviceId) {
        try (var session = driver.session()) {
            // Perform write transaction to delete the device by its ID
            Long res = session.executeWrite(tx -> {
                String query = """
                        MATCH (d:Device {id:$deviceId})
                        WITH d, d.id AS deviceIdDeleted
                        DETACH DELETE d
                        RETURN deviceIdDeleted;
                            """;

                Result record = tx.run(query, Values.parameters("deviceId", deviceId));

                if (!record.hasNext()) {
                    throw new DeviceNotFoundException("Device with ID " + deviceId + " not found.");
                }

                return record.single().get("deviceIdDeleted").asLong();
            });
            return res;
        }
    }
}
