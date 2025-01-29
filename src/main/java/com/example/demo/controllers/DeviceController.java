package com.example.demo.controllers;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.customExceptions.DeviceNotFoundException;
import com.example.demo.entity.Device;
import com.example.demo.service.DeviceServiceImplementation;

@RestController
@RequestMapping("/api/device")
public class DeviceController {

  private static final Logger logger = LoggerFactory.getLogger(DeviceController.class);

  @Autowired
  private DeviceServiceImplementation deviceServiceImplementation;

  // Delete device
  @DeleteMapping("/{deviceId}")
  ResponseEntity<Long> deleteDevice(@PathVariable long deviceId) throws Exception {
    logger.info("Attempting to delete device with id: {}", deviceId);
    try {
      Long deletedDeviceId = deviceServiceImplementation.deleteDevice(deviceId);
      logger.info("Device with id: {} deleted successfully.", deletedDeviceId);
      return ResponseEntity.ok(deletedDeviceId);
    } catch (DeviceNotFoundException e) {
      logger.error("Device with id: {} not found. Error: {}", deviceId, e.getMessage());
      throw new DeviceNotFoundException("Device with id: " + deviceId + " does not exist");
    } catch (Exception e) {
      logger.error("Unable to delete device with id: {}. Error: {}", deviceId, e.getMessage());
      throw new Exception("Unable to delete device with Id: " + deviceId + " " + e.getMessage());
    }
  }

  // Save device
  @PostMapping("/save")
  public ResponseEntity<Map<String, Object>> saveDevice(@RequestBody Device device) throws Exception {
    logger.info("Attempting to save device: {}", device);
    try {
      Map<String, Object> savedDevice = deviceServiceImplementation.saveDevice(device);
      logger.info("Device saved successfully: {}", savedDevice);
      return ResponseEntity.ok(savedDevice);
    } catch (Exception e) {
      logger.error("Error occurred while trying to save device. Error: {}", e.getMessage());
      throw new Exception("Error occurred while trying to save device " + e.getMessage());
    }
  }

  // Get device
  @GetMapping("/{deviceId}")
  ResponseEntity<Map<String, Object>> getDevice(@PathVariable long deviceId) throws Exception {
    logger.info("Attempting to retrieve device with id: {}", deviceId);
    try {
      Map<String, Object> getDeviceFromDB = deviceServiceImplementation.getDevice(deviceId);
      logger.info("Device with id: {} retrieved successfully.", deviceId);
      return ResponseEntity.ok(getDeviceFromDB);
    } catch (DeviceNotFoundException e) {
      logger.error("Device with id: {} not found. Error: {}", deviceId, e.getMessage());
      throw new DeviceNotFoundException("Device with Id: " + deviceId + " does not exist");
    } catch (Exception e) {
      logger.error("Unable to retrieve device with id: {}. Error: {}", deviceId, e.getMessage());
      throw new Exception("Unable to retrieve device with Id: " + deviceId + " " + e.getMessage());
    }
  }

  // Modify device
  @PutMapping("/{deviceId}")
  public ResponseEntity<Map<String, Object>> modifyDeviceById(@PathVariable long deviceId, @RequestBody Device device)
      throws Exception {

    logger.info("Attempting to modify device with id: {} and details: {}", deviceId, device);
    try {
      Map<String, Object> modifiedDevice = deviceServiceImplementation.modifyDevice(deviceId, device);
      logger.info("Device with id: {} modified successfully.", deviceId);
      return ResponseEntity.ok(modifiedDevice);
    } catch (DeviceNotFoundException e) {
      logger.error("Device with id: {} not found. Error: {}", deviceId, e.getMessage());
      throw new DeviceNotFoundException("Device with Id: " + deviceId + " does not exist. " + e.getMessage());
    } catch (Exception e) {
      logger.error("Unable to modify device with id: {}. Error: {}", deviceId, e.getMessage());
      throw new Exception("Unable to modify device with Id: " + deviceId + " " + e.getMessage());
    }
  }
}
