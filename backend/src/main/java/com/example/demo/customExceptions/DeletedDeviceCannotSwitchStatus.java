package com.example.demo.customExceptions;

public class DeletedDeviceCannotSwitchStatus extends RuntimeException {
    public DeletedDeviceCannotSwitchStatus(String message) {
        super(message);
    }
}
