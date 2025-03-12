package com.example.modified_assignment_backend.controllers;

import java.util.Map;

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

import com.example.modified_assignment_backend.entity.Device;
import com.example.modified_assignment_backend.service.DeviceServiceImplementation;

@RestController
@RequestMapping("/api/device")
public class DeviceController {

    @Autowired
    private DeviceServiceImplementation deviceServiceImplementation;

    // Deletes a device by its ID
    @DeleteMapping("/{deviceId}")
    ResponseEntity<Long> deleteDevice(@PathVariable long deviceId) throws Exception {
        Long deletedDeviceId = deviceServiceImplementation.deleteDevice(deviceId);
        return ResponseEntity.ok(deletedDeviceId);
    }

    // Saves a new device to the database
    @PostMapping("/save")
    public ResponseEntity<Map<String, Object>> saveDevice(@RequestBody Device device) throws Exception {
        Map<String, Object> savedDevice = deviceServiceImplementation.saveDevice(device);
        return ResponseEntity.ok(savedDevice);
    }

    // Retrieves a device by its ID
    @GetMapping("/{deviceId}")
    ResponseEntity<Map<String, Object>> getDevice(@PathVariable long deviceId) throws Exception {
        Map<String, Object> getDeviceFromDB = deviceServiceImplementation.getDevice(deviceId);
        return ResponseEntity.ok(getDeviceFromDB);
    }

    // Modifies a device by its ID
    @PutMapping("/{deviceId}")
    public ResponseEntity<Map<String, Object>> modifyDeviceById(@PathVariable long deviceId, @RequestBody Device device)
            throws Exception {

        Map<String, Object> modifiedDevice = deviceServiceImplementation.modifyDevice(deviceId, device);
        return ResponseEntity.ok(modifiedDevice);
    }
}