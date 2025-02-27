package com.example.demo.customExceptions;

public class DeviceAlreadyPresentException extends RuntimeException {
    public DeviceAlreadyPresentException(String message) {
        super(message);
    }
}
