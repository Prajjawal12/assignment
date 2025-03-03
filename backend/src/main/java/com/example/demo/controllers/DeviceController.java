package com.example.demo.controllers;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.example.demo.entity.Device;
import com.example.demo.service.DeviceServiceImplementation;

@Controller
@RequestMapping("/api/device")
public class DeviceController {

  private static final Logger logger = LoggerFactory.getLogger(DeviceController.class);

  @Autowired
  private DeviceServiceImplementation deviceServiceImplementation;

  // Deletes a device by its ID
  // @DeleteMapping("delete/{deviceId}")
  @RequestMapping(value = "delete/{deviceId}", method = RequestMethod.DELETE)
  public ResponseEntity<Long> deleteDevice(@PathVariable long deviceId) {
    logger.info("Attempting to delete device with id: {}", deviceId);
    Long deletedDeviceId = deviceServiceImplementation.deleteDevice(deviceId);
    logger.info("Device with id: {} deleted successfully.", deletedDeviceId);
    return ResponseEntity.ok(deletedDeviceId);

  }

  // Saves a new device to the database
  // @PostMapping("/save")
  @RequestMapping(value = "/save", method = RequestMethod.POST)
  public ResponseEntity<Map<String, Object>> saveDevice(@RequestBody Device device) throws Exception {
    Map<String, Object> savedDevice = deviceServiceImplementation.saveDevice(device);
    logger.info("Device saved successfully: {}", savedDevice);
    return ResponseEntity.ok(savedDevice);
  }

  // Retrieves a device by its ID
  // @GetMapping("fetch/{deviceId}")
  @RequestMapping(value = "fetch/{deviceId}", method = RequestMethod.GET)
  public ResponseEntity<List<Map<String, Object>>> getDeviceById(@PathVariable Long deviceId) throws Exception {
    logger.info("Attempting to retrieve device with id: {}", deviceId);
    List<Map<String, Object>> getDevicesFromDB = deviceServiceImplementation.getDeviceById(deviceId);

    if (getDevicesFromDB.size() == 0) {
      throw new RuntimeException("There are no devices present with the Id " + deviceId + " in the database.");
    }
    logger.info("Device with id: {} retrieved successfully.", deviceId);
    return ResponseEntity.ok(getDevicesFromDB);

  }

  // Modifies a device by its ID
  // @PutMapping("modify/{deviceId}")
  @RequestMapping(value = "modify/{deviceId}", method = RequestMethod.PUT)
  public ResponseEntity<Map<String, Object>> modifyDeviceById(@PathVariable long deviceId, @RequestBody Device device) {
    logger.info("Attempting to modify device with id: {} and details: {}", deviceId, device);
    Map<String, Object> modifiedDevice = deviceServiceImplementation.modifyDevice(deviceId, device);
    logger.info("Device with id: {} modified successfully.", deviceId);
    return ResponseEntity.ok(modifiedDevice);

  }

  // @GetMapping("/list")
  @RequestMapping(value = "/list", method = RequestMethod.GET)
  public ResponseEntity<List<Map<String, Object>>> getAllDevices() {
    List<Map<String, Object>> deviceMap = deviceServiceImplementation.listAllDevices();
    return ResponseEntity.ok(deviceMap);

  }

  // @GetMapping("/list/active")
  @RequestMapping(value = "/list/active", method = RequestMethod.GET)
  public ResponseEntity<List<Map<String, Object>>> getAllActiveDevices() {
    List<Map<String, Object>> deviceMap = deviceServiceImplementation.listAllActiveDevices();
    return ResponseEntity.ok(deviceMap);

  }

  // @GetMapping("/list/inactive")
  @RequestMapping(value = "/list/inactive", method = RequestMethod.GET)
  public ResponseEntity<List<Map<String, Object>>> getAllInactiveDevices() {
    List<Map<String, Object>> deviceMap = deviceServiceImplementation.listAllInActiveDevices();
    return ResponseEntity.ok(deviceMap);

  }

  // @GetMapping("/list/deleted")
  @RequestMapping(value = "/list/deleted", method = RequestMethod.GET)
  public ResponseEntity<List<Map<String, Object>>> getAllDeletedDevices() {
    List<Map<String, Object>> deviceMap = deviceServiceImplementation.listAllDeletedDevices();
    return ResponseEntity.ok(deviceMap);

  }

  // @PutMapping("/switch-status/{deviceId}")
  @RequestMapping(value = "/switch-status/{deviceId}", method = RequestMethod.PUT)
  public ResponseEntity<String> switchDeviceStatus(@PathVariable Long deviceId) {
    deviceServiceImplementation.switchStatus(deviceId);
    return ResponseEntity.ok("Device Status Switched Succesfully for device with id " + deviceId);
  }

}
