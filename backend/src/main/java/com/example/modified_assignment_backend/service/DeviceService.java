package com.example.modified_assignment_backend.service;

import java.util.Map;

import com.example.modified_assignment_backend.entity.Device;

public interface DeviceService {
    // Method to save a device
    public Map<String, Object> saveDevice(Device device);

    // Method to retrieve a device by its ID
    public Map<String, Object> getDevice(Long deviceId);

    // Method to modify a device by its ID
    public Map<String, Object> modifyDevice(Long id, Device device);

    // Method to delete a device by its ID
    public Long deleteDevice(Long deviceId);
}
