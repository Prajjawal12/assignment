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
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.customExceptions.DeletedDeviceCannotSwitchStatus;
import com.example.demo.customExceptions.DeletedDeviceCantBeDeletedException;
import com.example.demo.customExceptions.DeletedDevicesCannotBeModified;
import com.example.demo.customExceptions.DeviceAlreadyPresentException;
import com.example.demo.customExceptions.DeviceNotFoundException;
import com.example.demo.customExceptions.InactiveDeviceCannotBeModified;
import com.example.demo.entity.Device;

@Service
@Transactional
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
  @Transactional(propagation = Propagation.REQUIRED)

  public Map<String, Object> saveDevice(Device device) {
    try (Session session = driver.session()) {
      session.executeRead(tx -> {
        String query = """
            OPTIONAL MATCH (d:Device {id:$deviceId})
            RETURN COALESCE(d.status, 'notPresent') AS result;
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

            return resQuery.single().get("savedDevice").asNode().asMap();

          });
      return createdDevice;
    }

  }

  @Override
  @Transactional(propagation = Propagation.SUPPORTS)

  public List<Map<String, Object>> getDeviceById(Long deviceId) {
    try (Session session = driver.session()) {
      List<Map<String, Object>> deviceListById = session.executeRead(tx -> {
        String query = """
            MATCH (d:Device {id:$deviceId})
            RETURN d AS devicesFound;
            """;

        Result result = tx.run(query, Values.parameters("deviceId", deviceId));
        List<Map<String, Object>> deviceList = new ArrayList<>();
        while (result.hasNext()) {
          Record record = result.next();
          Map<String, Object> deviceFound = record.get("devicesFound").asNode().asMap();
          deviceList.add(deviceFound);
        }

        return deviceList;
      });
      return deviceListById;
    }

  }

  @Override
  @Transactional(propagation = Propagation.REQUIRED)

  public Map<String, Object> modifyDevice(Long id, Device device) {
    try (Session session = driver.session()) {
      Map<String, Object> statusCheckMapForModification = session.executeRead(tx -> {
        String query = """
                OPTIONAL MATCH (d:Device {id:$deviceId})
                RETURN COALESCE(d.status, 'notPresent') AS status;
            """;

        Result result = tx.run(query, Values.parameters("deviceId", id));

        boolean isActiveDevicePresent = false;
        boolean isInactiveDevicePresent = false;
        int deletedDevicesCount = 0;

        while (result.hasNext()) {
          Record record = result.next();
          String status = record.get("status").asString();

          if (status.equals("inactive")) {
            isInactiveDevicePresent = true;
          } else if (status.equals("active")) {
            isActiveDevicePresent = true;
          } else if (status.equals("deleted")) {
            deletedDevicesCount++;
          }
        }

        return Map.of(
            "isActiveDevicePresent", isActiveDevicePresent,
            "isInactiveDevicePresent", isInactiveDevicePresent,
            "deletedDevicesCount", deletedDevicesCount);
      });

      boolean isActiveDevicePresent = (boolean) statusCheckMapForModification.get("isActiveDevicePresent");
      boolean isInactiveDevicePresent = (boolean) statusCheckMapForModification.get("isInactiveDevicePresent");
      int deletedDevicesCount = (int) statusCheckMapForModification.get("deletedDevicesCount");

      if (!isActiveDevicePresent && deletedDevicesCount > 0) {
        throw new DeletedDevicesCannotBeModified(
            "The device with Id " + id + " cannot be modified as it is in deleted state.");
      }
      if (!isActiveDevicePresent && deletedDevicesCount == 0 && isInactiveDevicePresent) {
        throw new InactiveDeviceCannotBeModified(
            "The device with Id " + id + " cannot be modified as it is in inactive state.");
      }
      if (!isActiveDevicePresent && deletedDevicesCount == 0 && !isInactiveDevicePresent) {
        throw new DeviceNotFoundException(
            "The device with Id " + id + " cannot be modified as it is not present in our database.");
      }

      Map<String, Object> modifiedDevice = session.executeWrite(tx -> {
        String query = """
                MATCH (d: Device {id:$deviceId})
                WHERE d.status = 'active'
                SET d.name = $modifiedDeviceName,
                    d.deviceType = $modifiedDeviceType,
                    d.modifiedCredentialsAt = datetime()
                RETURN d AS modifiedDevice;
            """;

        Result result = tx.run(query, Values.parameters("deviceId", id, "modifiedDeviceName", device.getName(),
            "modifiedDeviceType", device.getDeviceType()));

        return result.single().get("modifiedDevice").asNode().asMap();
      });

      return modifiedDevice;
    }
  }

  @Override
  @Transactional(propagation = Propagation.REQUIRED)

  public Long deleteDevice(Long deviceId) {

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
  @Transactional(propagation = Propagation.SUPPORTS)
  public List<Map<String, Object>> listAllDevices() {
    try (Session session = driver.session()) {
      List<Map<String, Object>> res = session.executeRead(tx -> {
        String query = """
            MATCH (d: Device)
            RETURN d
            """;
        Result result = tx.run(query);
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
  @Transactional(propagation = Propagation.SUPPORTS)

  public List<Map<String, Object>> listAllActiveDevices() {
    try (Session session = driver.session()) {
      List<Map<String, Object>> res = session.executeRead(tx -> {
        String query = """
            MATCH (d: Device)
            WHERE d.status = 'active'
            RETURN d
            """;
        Result result = tx.run(query);
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
  @Transactional(propagation = Propagation.SUPPORTS)

  public List<Map<String, Object>> listAllInActiveDevices() {
    try (Session session = driver.session()) {
      List<Map<String, Object>> res = session.executeRead(tx -> {
        String query = """
            MATCH (d: Device)
            WHERE d.status = 'inactive'
            RETURN d
            """;
        Result result = tx.run(query);
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
  @Transactional(propagation = Propagation.SUPPORTS)

  public List<Map<String, Object>> listAllDeletedDevices() {
    try (Session session = driver.session()) {
      List<Map<String, Object>> res = session.executeRead(tx -> {
        String query = """
            MATCH (d: Device)
            WHERE d.status = 'deleted'
            RETURN d
            """;
        Result result = tx.run(query);
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
  @Transactional(propagation = Propagation.REQUIRED)

  public void switchStatus(Long deviceId) {
    try (Session session = driver.session()) {
      Map<String, Object> statusCheck = session.executeRead(tx -> {
        String query = """
            OPTIONAL MATCH (d:Device {id:$deviceId})
             RETURN COALESCE(d.status,'notPresent') AS deviceStatus;
             """;
        boolean isActiveOrInactiveDevicePresent = false;
        int deletedDevicesCount = 0;
        Result result = tx.run(query, Values.parameters("deviceId", deviceId));
        while (result.hasNext()) {
          Record record = result.next();
          String status = record.get("deviceStatus").asString();
          if (status.equals("active") || status.equals("inactive")) {
            isActiveOrInactiveDevicePresent = true;

          } else if (status.equals("deleted")) {
            deletedDevicesCount++;
          }
        }
        return Map.of("isActiveOrInactiveDevicePresent", isActiveOrInactiveDevicePresent, "deletedDevicesCount",
            deletedDevicesCount);
      });

      boolean isActiveOrInactiveDevicePresent = (boolean) statusCheck.get("isActiveOrInactiveDevicePresent");
      int deletedDevicesCount = (int) statusCheck.get("deletedDevicesCount");

      if (deletedDevicesCount == 0 && !isActiveOrInactiveDevicePresent) {
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
            SET d.statusSwitchTimeStamp = datetime(),
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
