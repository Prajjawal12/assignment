package com.example.demo.service;

import java.util.Map;

import com.example.demo.entity.Device;

// Service interface defining methods for managing devices
public interface DeviceService {

  // Method to save a device
  public Map<String, Object> saveDevice(Device device);

  // Method to retrieve a device by its ID
  public Map<String, Object> getDevice(long deviceId);

  // Method to modify a device by its ID
  public Map<String, Object> modifyDevice(Long id, Device device);

  // Method to delete a device by its ID
  public long deleteDevice(long deviceId);
}
