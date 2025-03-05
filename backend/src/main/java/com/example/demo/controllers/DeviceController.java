package com.example.demo.controllers;

import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import com.example.demo.customExceptions.DeviceNotFoundException;
import com.example.demo.entity.Device;
import com.example.demo.service.DeviceServiceImplementation;

@Controller
@RequestMapping("/api/device")
public class DeviceController {

  @Autowired
  private DeviceServiceImplementation deviceServiceImplementation;

  @RequestMapping(value = "delete/{deviceId}", method = RequestMethod.DELETE)
  public ResponseEntity<?> deleteDevice(@PathVariable long deviceId) {
    try {
      Long deletedDeviceId = deviceServiceImplementation.deleteDevice(deviceId);
      return ResponseEntity.ok(deletedDeviceId);
    } catch (DeviceNotFoundException e) {
      return new ResponseEntity<>("The device with Id " + deviceId + " is not present in the database",
          HttpStatus.NOT_FOUND);
    }
  }

  @RequestMapping(value = "fetch/{deviceId}", method = RequestMethod.GET)
  public ResponseEntity<?> getDeviceById(@PathVariable Long deviceId) {
    try {
      Map<String, Object> getDevicesFromDB = deviceServiceImplementation.getDeviceById(deviceId);
      return ResponseEntity.ok(getDevicesFromDB);
    } catch (DeviceNotFoundException e) {
      return new ResponseEntity<>("There is no device present with id " + deviceId + " in the database.",
          HttpStatus.NOT_FOUND);
    }
  }

  @RequestMapping(value = "modify/{deviceId}", method = RequestMethod.PUT)
  public ResponseEntity<String> modifyDeviceById(@PathVariable long deviceId, @RequestBody Device device) {
    try {
      deviceServiceImplementation.modifyDevice(deviceId, device);
      return ResponseEntity.ok("Device modified successfully");
    } catch (DeviceNotFoundException e) {
      return new ResponseEntity<>("The device with Id " + deviceId + " is not present in the database",
          HttpStatus.NOT_FOUND);
    }
  }

  @RequestMapping(value = "/list", method = RequestMethod.GET)
  public ResponseEntity<?> getAllDevices() {
    try {
      List<Map<String, Object>> deviceMap = deviceServiceImplementation.listAllDevices();
      return ResponseEntity.ok(deviceMap);
    } catch (DeviceNotFoundException e) {
      return new ResponseEntity<>("No Devices Present in the database", HttpStatus.NOT_FOUND);
    }
  }

  @RequestMapping(value = "/save", method = RequestMethod.POST)
  public ResponseEntity<Map<String, Object>> saveDevice(@RequestBody Device device,
      @RequestParam boolean confirmModification) {
    Map<String, Object> savedDevice = deviceServiceImplementation.saveDevice(device, confirmModification);
    return ResponseEntity.ok(savedDevice);
  }
}
