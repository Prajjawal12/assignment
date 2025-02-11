package com.example.demo.customExceptions;

// Custom exception for handling cases when a record is not found in the system
public class RecordNotFoundException extends RuntimeException {

  // Constructor that takes a custom error message as a parameter
  public RecordNotFoundException(String message) {
    super(message); // Pass the error message to the superclass (RuntimeException)
  }
}
