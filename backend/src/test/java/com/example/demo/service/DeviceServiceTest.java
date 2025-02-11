package com.example.demo.service;

import com.example.demo.entity.Device;
import com.example.demo.customExceptions.DeviceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.neo4j.driver.Driver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;
// The driver is injected and used internally by the DeviceServiceImplementation, so it's not directly used in the test methods.
import org.junit.jupiter.api.AfterEach;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class DeviceServiceImplementationTest {

  @Autowired
  private DeviceServiceImplementation deviceService;

  @Autowired
  private Driver driver;

  private Device testDevice;

  @BeforeEach
  void setUp() {
    testDevice = new Device();
    testDevice.setId(1055L);
    testDevice.setName("Test Device");
    testDevice.setDeviceType("Smartphone");
  }

  @AfterEach
  void tearDown() {
    // Delete the device created in the test if it exists
    try {
      if (testDevice.getId() > 0) {
        deviceService.deleteDevice(testDevice.getId());
      }
    } catch (DeviceNotFoundException e) {
      // Ignore if the device doesn't exist (already deleted)

    }
  }

  @Test
  void saveDeviceTest() {
    // Test saveDevice method
    var result = deviceService.saveDevice(testDevice);
    assertNotNull(result);
    assertEquals(testDevice.getId(), result.get("id"));
    assertEquals(testDevice.getName(), result.get("name"));
    assertEquals(testDevice.getDeviceType(), result.get("deviceType"));
  }

  @Test
  void getDeviceTest() {
    // Save device first
    var savedDevice = deviceService.saveDevice(testDevice);
    var deviceId = (Long) savedDevice.get("id");

    // Test getDevice method
    var result = deviceService.getDevice(deviceId);
    assertNotNull(result);
    assertEquals(testDevice.getId(), result.get("id"));
    assertEquals(testDevice.getName(), result.get("name"));
    assertEquals(testDevice.getDeviceType(), result.get("deviceType"));
  }

  @Test
  void getDeviceNotFoundTest() {
    // Test getDevice method when device is not found
    long nonExistentDeviceId = 9999L;

    assertThrows(DeviceNotFoundException.class, () -> deviceService.getDevice(nonExistentDeviceId));
  }

  @Test
  void modifyDeviceTest() {
    // Save device first
    var savedDevice = deviceService.saveDevice(testDevice);
    var deviceId = (Long) savedDevice.get("id");

    // Modify the device
    testDevice.setName("Updated Device");
    testDevice.setDeviceType("Tablet");
    var result = deviceService.modifyDevice(deviceId, testDevice);

    assertNotNull(result);
    assertEquals("Updated Device", result.get("name"));
    assertEquals("Tablet", result.get("deviceType"));
  }

  @Test
  void deleteDeviceTest() {
    // Save device first
    var savedDevice = deviceService.saveDevice(testDevice);
    var deviceId = (Long) savedDevice.get("id");

    // Test deleteDevice method
    long deletedDeviceId = deviceService.deleteDevice(deviceId);
    assertEquals(deviceId, deletedDeviceId);

    // Verify the device is deleted
    assertThrows(DeviceNotFoundException.class, () -> deviceService.getDevice(deviceId));
  }

  @Test
  void deleteDeviceNotFoundTest() {
    // Test deleteDevice method when device is not found
    long nonExistentDeviceId = 9999L;

    assertThrows(DeviceNotFoundException.class, () -> deviceService.deleteDevice(nonExistentDeviceId));
  }
}
