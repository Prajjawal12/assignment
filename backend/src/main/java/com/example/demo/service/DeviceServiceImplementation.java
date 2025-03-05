package com.example.demo.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.neo4j.driver.Driver;
import org.neo4j.driver.Record;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;
import org.neo4j.driver.Values;
import org.neo4j.driver.types.Node;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.customExceptions.DeviceNotFoundException;
import com.example.demo.entity.Device;

@Service
public class DeviceServiceImplementation implements DeviceService {

  @Autowired
  private Driver driver;

  @Override
  public void modifyDevice(Long id, Device device) {
    try (Session session = driver.session()) {
      session.executeWriteWithoutResult(tx -> {
        String query = """
            MATCH (d: Device {id:$deviceId})
            WHERE d.isDeleted = 'N'
            SET d.deviceName = $deviceName, d.deviceType = $deviceType, d.credentialsModifiedAt = datetime()
            """;

        tx.run(query, Values.parameters("deviceId", device.getId(), "deviceName", device.getName(),
            "deviceType", device.getDeviceType()));
      });
    }
  }

  @Override
  public void createDevice(Device device) {
    try (Session session = driver.session()) {
      session.executeWriteWithoutResult(tx -> {
        String query = """
            CREATE (d: Device {id:$deviceId})
            SET d.name = $deviceName, d.deviceType = $deviceType, d.isDeleted = 'N'
            """;

        tx.run(query, Values.parameters("deviceId", device.getId(),
            "deviceName", device.getName(), "deviceType", device.getDeviceType()));
      });
    }
  }

  @Override
  public Map<String, Object> getDeviceById(Long deviceId) {
    try (Session session = driver.session()) {
      return session.executeRead(tx -> {
        String query = """
            MATCH (d:Device {id:$deviceId})
            WHERE d.isDeleted = 'N'
            RETURN d;
            """;
        Result result = tx.run(query, Values.parameters("deviceId", deviceId));
        if (!result.hasNext()) {
          throw new DeviceNotFoundException("Device with id " + deviceId + " is not present in the database");
        }
        Node node = result.single().get("d").asNode();
        return new HashMap<>(node.asMap());
      });
    }
  }

  @Override
  public Map<String, Object> saveDevice(Device device, boolean confirmModification) {
    try (Session session = driver.session()) {
      return session.executeWrite(tx -> {
        if (confirmModification) {
          modifyDevice(device.getId(), device);
        } else {
          createDevice(device);
        }
        return getDeviceById(device.getId());
      });
    }
  }

  @Override
  public Long deleteDevice(Long deviceId) {
    try (Session session = driver.session()) {
      return session.executeWrite(tx -> {
        String query = """
            MATCH (d:Device {id: $deviceId})
            WHERE d.isDeleted = 'N'
            SET d.isDeleted = 'Y'
            RETURN d.id AS deletedDeviceId;
            """;

        Result result = tx.run(query, Values.parameters("deviceId", deviceId));
        if (!result.hasNext()) {
          throw new DeviceNotFoundException("Device with id " + deviceId + " is not present in the database.");
        }
        return result.single().get("deletedDeviceId").asLong();
      });
    }
  }

  @Override
  public List<Map<String, Object>> listAllDevices() {
    try (Session session = driver.session()) {
      return session.executeRead(tx -> {
        String query = """
            MATCH (d:Device)
            WHERE d.isDeleted = 'N'
            RETURN d;
            """;

        Result result = tx.run(query);
        List<Map<String, Object>> fetchedDevices = new ArrayList<>();

        while (result.hasNext()) {
          Record record = result.next();
          fetchedDevices.add(record.get("d").asMap());
        }

        if (fetchedDevices.isEmpty()) {
          throw new DeviceNotFoundException("There are no devices present in the database.");
        }
        return fetchedDevices;
      });
    }
  }
}
