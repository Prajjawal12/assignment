package com.example.demo.service;

import java.util.Map;
import org.neo4j.driver.Driver;
import org.neo4j.driver.Values;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.customExceptions.DeviceNotFoundException;
import com.example.demo.entity.Device;

@Service
public class DeviceServiceImplementation implements DeviceService {

  // Logger for logging relevant information
  private static final Logger logger = LoggerFactory.getLogger(DeviceServiceImplementation.class);

  // Neo4j driver instance for interacting with the Neo4j database
  private final Driver driver;

  @Autowired
  public DeviceServiceImplementation(Driver driver) {
    this.driver = driver;
  }

  @Override
  public Map<String, Object> saveDevice(Device device) {
    logger.info("Saving device with ID: {}", device.getId());
    try (var session = driver.session()) {
      // Perform write transaction to save the device
      var res = session.executeWrite(tx -> {
        String query = """
            MERGE (d:Device {id:$deviceId})
            SET d.name = $deviceName, d.deviceType = $deviceType
            RETURN d AS savedDevice
               """;

        // Execute the query and capture the result
        var record = tx.run(query, Values.parameters("deviceId", device.getId(),
            "deviceName", device.getName(), "deviceType", device.getDeviceType())).single();

        logger.info("Device with ID {} saved successfully", device.getId());
        return record.get("savedDevice").asNode().asMap();
      });
      return res;
    } catch (Exception e) {
      logger.error("Error while saving device: {}", e.getMessage(), e);
      throw e;
    }
  }

  @Override
  public Map<String, Object> getDevice(long deviceId) {
    logger.info("Fetching device with ID: {}", deviceId);
    try (var session = driver.session()) {
      var res = session.executeRead(tx -> {
        String query = """
            MATCH (d:Device {id:$deviceId})
            RETURN d AS deviceFound
              """;
        // Perform read transaction to fetch the device by ID

        var record = tx.run(query, Values.parameters("deviceId", deviceId));

        if (!record.hasNext()) {
          logger.warn("Device with ID {} not found", deviceId);
          throw new DeviceNotFoundException("Device with ID " + deviceId + " not found.");
        }

        logger.info("Device with ID {} fetched successfully", deviceId);
        return record.single().get("deviceFound").asNode().asMap();
      });
      return res;
    } catch (DeviceNotFoundException e) {
      logger.error("Device not found with ID {}: {}", deviceId, e.getMessage());
      throw e; // Rethrow the custom exception
    } catch (Exception e) {
      logger.error("Error while fetching device: {}", e.getMessage(), e);
      throw e; // Rethrow the exception if it's not a DeviceNotFoundException
    }
  }

  @Override
  public Map<String, Object> modifyDevice(Long id, Device device) {
    logger.info("Modifying device with ID: {}", id);
    try (var session = driver.session()) {
      // Perform write transaction to modify the device by its ID
      var res = session.executeWrite(tx -> {
        String query = """
            MATCH (d:Device {id: $deviceId})
            SET d.name = $deviceName, d.deviceType = $deviceType
            RETURN d AS modifiedDevice
                """;

        var record = tx.run(query, Values.parameters("deviceId", id,
            "deviceName", device.getName(), "deviceType", device.getDeviceType())).single();

        logger.info("Device with ID {} modified successfully", id);
        return record.get("modifiedDevice").asNode().asMap();
      });
      return res;
    } catch (Exception e) {
      logger.error("Error while modifying device: {}", e.getMessage(), e);
      throw e;
    }
  }

  @Override
  public long deleteDevice(long deviceId) {
    logger.info("Deleting device with ID: {}", deviceId);
    try (var session = driver.session()) {
      // Perform write transaction to delete the device by its ID
      var res = session.executeWrite(tx -> {
        String query = """
            MATCH (d:Device {id:$deviceId})
            WITH d, d.id AS deviceIdDeleted
            DETACH DELETE d
            RETURN deviceIdDeleted;
                """;

        var record = tx.run(query, Values.parameters("deviceId", deviceId));

        if (!record.hasNext()) {
          logger.warn("Device with ID {} not found for deletion", deviceId);
          throw new DeviceNotFoundException("Device with ID " + deviceId + " not found.");
        }

        logger.info("Device with ID {} deleted successfully", deviceId);
        return record.single().get("deviceIdDeleted").asLong();
      });
      return res;
    } catch (DeviceNotFoundException e) {
      logger.error("Device not found with ID {}: {}", deviceId, e.getMessage());
      throw e; // Rethrow the custom exception
    } catch (Exception e) {
      logger.error("Error while deleting device: {}", e.getMessage(), e);
      throw e; // Rethrow the exception if it's not a DeviceNotFoundException
    }
  }
}
