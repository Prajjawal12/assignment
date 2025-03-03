package com.example.demo.customExceptions;

public class DeletedDevicesCannotBeModified extends RuntimeException {
    public DeletedDevicesCannotBeModified(String message) {
        super(message);
    }
}
