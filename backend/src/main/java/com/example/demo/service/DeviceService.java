package com.example.demo.service;

import java.util.List;
import java.util.Map;
import com.example.demo.entity.Device;

// Service interface defining methods for managing devices
public interface DeviceService {

  // Method to retrieve a device by its ID
  public Map<String, Object> getDeviceById(Long deviceId);

  // Method to modify a device by its ID
  public void modifyDevice(Long id, Device device);

  // Method to delete a device by its ID
  public Long deleteDevice(Long deviceId);

  // Method to list all devices present in the database
  public List<Map<String, Object>> listAllDevices();

  // Method to create a device
  public void createDevice(Device device);

  // Method to save (create or modify) a device
  public Map<String, Object> saveDevice(Device device, boolean confirmModification);
}
