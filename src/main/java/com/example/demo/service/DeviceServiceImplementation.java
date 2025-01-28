package com.example.demo.service;

import java.util.Map;

import org.neo4j.driver.Driver;
import org.neo4j.driver.Values;
import org.springframework.stereotype.Service;

import com.example.demo.config.DatabaseConfig;
import com.example.demo.customExceptions.DeviceNotFoundException;
import com.example.demo.entity.Device;

@Service
public class DeviceServiceImplementation implements DeviceService {

  Driver driver = DatabaseConfig.getDriver();

  @Override
  public Map<String, Object> saveDevice(Device device) {
    try (var session = driver.session()) {
      var res = session.executeWrite(tx -> {
        String query = """
            MERGE (d:Device {id:$deviceId})
            SET d.name = $deviceName
            RETURN d AS savedDevice
               """;

        var record = tx.run(query, Values.parameters("deviceId", device.getId(),
            "deviceName", device.getName())).single();

        return record.get("savedDevice").asNode().asMap();
      });

      return res;

    }
  }

  @Override
  public Map<String, Object> getDevice(long deviceId) {
    try (var session = driver.session()) {
      var res = session.executeRead(tx -> {
        String query = """
            MATCH (d:Device {id:$deviceId})
            RETURN d AS deviceFound
              """;

        var record = tx.run(query, Values.parameters("deviceId", deviceId)).single();
        if (record == null) {
          // add a log here later
          throw new DeviceNotFoundException("Device with ID " + deviceId + " not found.");
        }
        return record.get("deviceFound").asNode().asMap();
      });
      return res;
    }
  }

  @Override
  public Map<String, Object> modifyDevice(long deviceId, String modifiedName) {
    try (var session = driver.session()) {
      var res = session.executeWrite(tx -> {
        String query = """
            MATCH (d:Device {id:$deviceId})
            SET d.name = $modifiedName
            RETURN d AS modifiedDeviceName
            """;
        var record = tx.run(query, Values.parameters("deviceId", deviceId, "modifiedName", modifiedName)).single();
        if (record == null) {
          // add a log here later
          throw new DeviceNotFoundException("Device with ID " + deviceId + " not found.");
        }
        return record.get("modifiedDeviceName").asNode().asMap();
      });
      return res;
    }
  }

  @Override
  public long deleteDevice(long deviceId) {
    try (var session = driver.session()) {
      var res = session.executeWrite((tx) -> {
        String query = """
            MATCH (d:Device {id:$deviceId})
            DETACH DELETE d
            RETURN d.id AS deviceIdDeleted;
                """;

        var record = tx.run(query, Values.parameters("deviceId", deviceId)).single();

        if (record == null) {
          // add a log here later
          throw new DeviceNotFoundException("Device with ID " + deviceId + " not found.");
        }
        return record.get("deviceIdDeleted").asLong();
      });
      return res;
    }
  }
}
