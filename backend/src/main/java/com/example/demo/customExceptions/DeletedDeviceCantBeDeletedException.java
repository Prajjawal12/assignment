package com.example.demo.customExceptions;

public class DeletedDeviceCantBeDeletedException extends RuntimeException {
    public DeletedDeviceCantBeDeletedException(String message) {
        super(message);
    }
}
