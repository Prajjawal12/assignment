package com.example.demo.service;

import java.util.List;
import java.util.Map;

import com.example.demo.entity.Device;

// Service interface defining methods for managing devices
public interface DeviceService {

  // Method to save a device
  public Map<String, Object> saveDevice(Device device);

  // Method to retrieve a device by its ID
  public Map<String, Object> getDeviceById(long deviceId);

  // Method to modify a device by its ID
  public Map<String, Object> modifyDevice(Long id, Device device);

  // Method to delete a device by its ID
  public Long deleteDevice(long deviceId);

  // Method to list all devices present in the database
  public List<Map<String, Object>> listAllDevices();

  // Method to switch the status of the device
  public void switchStatus(Long deviceId);
}
