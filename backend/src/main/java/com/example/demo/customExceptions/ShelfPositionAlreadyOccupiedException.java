package com.example.demo.customExceptions;

public class ShelfPositionAlreadyOccupiedException extends RuntimeException {
    public ShelfPositionAlreadyOccupiedException(String message) {
        super(message);
    }
}
