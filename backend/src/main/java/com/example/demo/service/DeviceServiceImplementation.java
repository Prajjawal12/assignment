package com.example.demo.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.neo4j.driver.Driver;
import org.neo4j.driver.Record;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;
import org.neo4j.driver.Values;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.customExceptions.DeletedDeviceCannotSwitchStatus;
import com.example.demo.customExceptions.DeletedDeviceCantBeDeletedException;
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

  /*
   * Initial query
   * 
   * OPTIONAL MATCH (d:Device {id:$deviceId})
   * RETURN
   * CASE
   * WHEN d.status = 'active' THEN 'active'
   * WHEN d.status = 'inactive' THEN 'inactive'
   * WHEN d.status = 'deleted' THEN 'deleted'
   * ELSE NULL
   * END AS result
   * """;
   */
  @Override
  public Map<String, Object> saveDevice(Device device) {
    try (Session session = driver.session()) {
      session.executeRead(tx -> {
        String query = """
            OPTIONAL MATCH (d:Device {id:$deviceId})
            RETURN COALESCE(d.status, 'notPresent') AS result
            """;

        Result result = tx.run(query, Values.parameters("deviceId", device.getId()));
        boolean activeOrInactiveState = false;
        while (result.hasNext()) {
          Record record = result.next();
          String status = record.get("result").asString();

          if (status.equals("active") || status.equals("inactive")) {
            activeOrInactiveState = true;
            break;
          }

        }
        if (activeOrInactiveState) {
          throw new DeviceAlreadyPresentException("The device with the id " + device.getId()
              + " is already present in the database in either active or inactive state");
        }

        return null;

      });
      // reference :- https://stackoverflow.com/a/27538809
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
  public Map<String, Object> getDeviceById(long deviceId) {
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
  public Long deleteDevice(long deviceId) {

    try (Session session = driver.session()) {
      Map<String, Object> statusCheckForPresentDevices = session.executeRead(tx -> {
        String query = """
            MATCH (d:Device {id:$deviceId})
            RETURN d.status AS status
            """;

        Result result = tx.run(query, Values.parameters("deviceId", deviceId));
        boolean activeOrInactiveDevicePresenceCheck = false;
        int deletedDevicesCount = 0;

        while (result.hasNext()) {
          Record record = result.next();
          String status = record.get("status").asString();

          if (status.equals("active") || status.equals("inactive")) {
            activeOrInactiveDevicePresenceCheck = true;
          } else if (status.equals("deleted")) {
            deletedDevicesCount++;
          }

        }
        return Map.of("activeOrInactiveFound", activeOrInactiveDevicePresenceCheck, "deletedDevicesCount",
            deletedDevicesCount);

      });

      boolean activeOrInactiveDevicePresenceCheck = (boolean) statusCheckForPresentDevices.get("activeOrInactiveFound");
      int deletedDevicesCount = (int) statusCheckForPresentDevices.get("deletedDevicesCount");

      if (deletedDevicesCount == 0 && !activeOrInactiveDevicePresenceCheck) {
        throw new DeviceNotFoundException("The device with ID " + deviceId + " is not present in the database");
      }

      if (deletedDevicesCount > 0 && !activeOrInactiveDevicePresenceCheck) {
        throw new DeletedDeviceCantBeDeletedException("The device with id " + deviceId
            + " cannot be deleted as there are no active or inactive devices with the ID " + deviceId);
      }

      Long deletedDeviceId = session.executeWrite(tx -> {
        String query = """
            MATCH (d:Device {id:$deviceId})
            WHERE d.status IN ['active' , 'inactive']
            SET d.status = 'deleted' , d.deletedAt = $deletedAt
            RETURN d.id AS deletedDeviceId;
            """;

        Result result = tx.run(query,
            Values.parameters("deviceId", deviceId, "deletedAt", LocalDateTime.now().toString()));

        return result.single().get("deletedDeviceId").asLong();
      });
      return deletedDeviceId;
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

  @Override
  public void switchStatus(Long deviceId) {
    try (Session session = driver.session()) {
      Map<String, Object> statusCheck = session.executeRead(tx -> {
        String query = """
            OPTIONAL MATCH (d:Device {id:$deviceId})
             RETURN COALESCE(d.status,'notPresent') AS deviceStatus;
             """;
        boolean isActiveOrInactiveDevicePresent = false;
        boolean isDeviceWithGivenIdPresent = true;
        int deletedDevicesCount = 0;
        Result result = tx.run(query, Values.parameters("deviceId", deviceId));
        while (result.hasNext()) {
          Record record = result.next();
          String status = record.get("deviceStatus").asString();
          if (status.equals("active") || status.equals("inactive")) {
            isActiveOrInactiveDevicePresent = true;
          } else if (status.equals("deleted")) {
            deletedDevicesCount++;
          } else if (status.equals("notPresent")) {
            isDeviceWithGivenIdPresent = false;
          }
        }
        return Map.of("isActiveOrInactiveDevicePresent", isActiveOrInactiveDevicePresent, "isDeviceWithGivenIdPresent",
            isDeviceWithGivenIdPresent, "deletedDevicesCount", deletedDevicesCount);
      });

      boolean isActiveOrInactiveDevicePresent = (boolean) statusCheck.get("isActiveOrInactiveDevicePresent");
      int deletedDevicesCount = (int) statusCheck.get("deletedDevicesCount");
      boolean isDeviceWithGivenIdPresent = (boolean) statusCheck.get("isDeviceWithGivenIdPresent");

      if (isDeviceWithGivenIdPresent) {
        throw new DeviceNotFoundException("Device with ID " + deviceId + " is not present in the database");
      }

      if (!isActiveOrInactiveDevicePresent && deletedDevicesCount > 0) {
        throw new DeletedDeviceCannotSwitchStatus("There is no device with ID " + deviceId
            + " which is in active or inactive state to switch it's state.They are present in deleted state.");
      }

      session.executeWriteWithoutResult(tx -> {
        String query = """
            MATCH (d:Device {id:$deviceId})
            WHERE d.status <> 'deleted'
            SET d.modifiedAt = datetime(),
            d.status =
            CASE
            WHEN d.status = 'active' THEN 'inactive'
            WHEN d.status = 'inactive' THEN 'active'
            END
            RETURN d  AS updatedDevice
            """;

        tx.run(query, Values.parameters("deviceId", deviceId));

      });
    }
  }
}
