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
            SET d.deviceName = $deviceName , d.deviceType = $deviceType , d.credentialsModifiedAt = datetime()
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
            MERGE (d: Device {id:$deviceId})
            ON MATCH SET d.isDeleted = d.isDeleted
            ON CREATE SET d.name = $deviceName , d.deviceType = $deviceType , d.isDeleted = 'N'
            RETURN d
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
        if (result.hasNext()) {
          Node node = result.single().get("d").asNode();
          return new HashMap<>(node.asMap());
        } else {
          return null;
        }
      });
    }
  }

  @Override
  public Map<String, Map<String, Object>> saveDevice(Device device) {
    try (Session session = driver.session()) {
      String operation = session.executeRead(tx -> {
        String query = """
            MATCH (d:Device {id:$deviceId})
            WHERE d.isDeleted <> 'Y'
            RETURN d AS locatedDevice;
            """;
        Result resultCheck = tx.run(query, Values.parameters("deviceId", device.getId()));
        if (resultCheck.hasNext()) {
          modifyDevice(device.getId(), device);
          return "modified";
        } else {
          createDevice(device);
          return "created";
        }
      });

      Map<String, Map<String, Object>> response = new HashMap<>();
      Map<String, Object> deviceMap = getDeviceById(device.getId());
      if (deviceMap != null) {
        response.put(operation, deviceMap);
      }
      return response;
    }
  }

  @Override
  public Long deleteDevice(Long deviceId) {

    try (Session session = driver.session()) {
      Long deviceIdDeleted = session.executeWrite(tx -> {
        String query = """
            MATCH (d:Device {id: $deviceId})
            WHERE d.isDeleted = 'N'
            SET d.isDeleted = 'Y'
            RETURN d.id AS deletedDeviceId;
            """;

        Result result = tx.run(query, Values.parameters("deviceId", deviceId));

        return result.single().get("deletedDeviceId").asLong();
      });

      return deviceIdDeleted;
    }

  }

  @Override
  public List<Map<String, Object>> listAllDevices() {

    try (Session session = driver.session()) {
      List<Map<String, Object>> deviceList = session.executeRead(tx -> {
        String query = """
            MATCH (d:Device)
            WHERE d.isDeleted = 'N'
            RETURN d;
            """;

        Result result = tx.run(query);

        List<Map<String, Object>> fetchedDevices = new ArrayList<>();

        while (result.hasNext()) {
          Record record = result.next();
          Map<String, Object> fetchedDevice = record.get("d").asMap();
          fetchedDevices.add(fetchedDevice);
        }

        return fetchedDevices;
      });
      return deviceList;
    }

  }

}