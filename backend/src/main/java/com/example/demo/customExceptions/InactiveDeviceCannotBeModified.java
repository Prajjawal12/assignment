package com.example.demo.customExceptions;

public class InactiveDeviceCannotBeModified extends RuntimeException {
    public InactiveDeviceCannotBeModified(String message) {
        super(message);
    }
}
