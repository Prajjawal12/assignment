package com.example.demo.customExceptions;

public class DeviceAlreadyAssignedToShelfPositionException extends RuntimeException {
    public DeviceAlreadyAssignedToShelfPositionException(String message) {
        super(message);
    }
}
