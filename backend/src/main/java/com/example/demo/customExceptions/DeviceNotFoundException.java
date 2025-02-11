package com.example.demo.customExceptions;

// Custom exception for handling cases when a device is not found in the system
public class DeviceNotFoundException extends RuntimeException {

  // Constructor that takes a custom error message as a parameter
  public DeviceNotFoundException(String message) {
    super(message); // Pass the error message to the superclass (RuntimeException)
  }
}
