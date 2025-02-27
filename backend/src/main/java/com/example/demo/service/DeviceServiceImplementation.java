package com.example.demo.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.neo4j.driver.Driver;
import org.neo4j.driver.Record;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;
import org.neo4j.driver.Values;
import org.neo4j.driver.exceptions.NoSuchRecordException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.customExceptions.DeviceAlreadyPresentException;
import com.example.demo.customExceptions.DeviceNotFoundException;
import com.example.demo.entity.Device;

@Service
public class DeviceServiceImplementation implements DeviceService {

  // Logger for logging relevant information
  private static final Logger logger = LoggerFactory.getLogger(DeviceServiceImplementation.class);

  // Neo4j driver instance for interacting with the Neo4j database
  @Autowired
  private Driver driver;

  @Override
  public Map<String, Object> saveDevice(Device device) {

    try (Session session = driver.session()) {
      String res = session.executeRead(tx -> {
        String query = """
            OPTIONAL MATCH (d:Device {id:$deviceId})
            RETURN
            CASE
            WHEN d.status = 'active' THEN 'active'
            WHEN d.status = 'inactive' THEN 'inactive'
            WHEN d.status = 'deleted' THEN 'deleted'
            ELSE NULL
            END AS result
            """;

        Result result = tx.run(query, Values.parameters("deviceId", device.getId()));
        try {
          return result.single().get("result").asString();
        } catch (NoSuchRecordException e) {
          return "notPresent";
        }

      });
      // reference :- https://stackoverflow.com/a/27538809
      System.out.println(res); // working until here
      if (res.equals("active") || res.equals("inactive")) {
        throw new DeviceAlreadyPresentException("Device with id " + device.getId()
            + " is already present in the database in either active or inactive state hence the id cannot be resued.");
      }

      // working until here
      Map<String, Object> createdDevice = session.executeWrite(
          tx -> {
            String query = """
                CREATE (d: Device { id: $deviceId })
                SET d.name = $deviceName , d.deviceType = $deviceType , d.status = $deviceStatus
                RETURN d AS savedDevice;
                """;

            Result resQuery = tx.run(query, Values.parameters("deviceId", device.getId(), "deviceName",
                device.getName(), "deviceType", device.getDeviceType(), "deviceStatus",
                device.getDeviceStatus().ACTIVE.toString().toLowerCase()));
            // System.out.println(resQuery.single().get("savedDevice").asNode().asMap()); //
            // Above statement ends up consuming the result
            // Note to self, never use sout again for testing results coming from DB as it
            // could end up consuming it
            // it is working fine as I can
            // see the map so created
            return resQuery.single().get("savedDevice").asNode().asMap();

          });
      System.out.println("I have created this device" + createdDevice);
      return createdDevice;
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
        System.out.println(record.peek());
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
      throw e;
    } catch (Exception e) {
      logger.error("Error while fetching device: {}", e.getMessage(), e);
      throw e;
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
    } catch (

    Exception e) {
      logger.error("Error while deleting device: {}", e.getMessage(), e);
      throw e;
    }
  }

  @Override
  public List<Map<String, Object>> listAllDevices() {
    try (var session = driver.session()) {
      var res = session.executeRead(tx -> {
        String query = """
            MATCH (d: Device)
            RETURN d
            """;
        var result = tx.run(query);
        List<Map<String, Object>> deviceList = new ArrayList<>();
        while (result.hasNext()) {
          Record record = result.next();
          Map<String, Object> deviceMap = record.get("d").asNode().asMap();
          deviceList.add(deviceMap);

        }
        return deviceList;
      });
      return res;
    }
  }
}
