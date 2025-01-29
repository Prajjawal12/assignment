package com.example.demo.service;

import java.util.Map;

import com.example.demo.entity.Device;

public interface DeviceService {

  public Map<String, Object> saveDevice(Device device);

  public Map<String, Object> getDevice(long deviceId);

  public Map<String, Object> modifyDevice(Long id, Device device);

  public long deleteDevice(long deviceId);
}
