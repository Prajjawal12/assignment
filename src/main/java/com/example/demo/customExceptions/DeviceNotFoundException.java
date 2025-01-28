package com.example.demo.customExceptions;

public class DeviceNotFoundException extends RuntimeException {

  public DeviceNotFoundException(String message) {
    super(message);
  }
}
