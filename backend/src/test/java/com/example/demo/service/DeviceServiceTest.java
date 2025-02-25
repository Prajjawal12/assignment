package com.example.demo.service;

import com.example.demo.customExceptions.DeviceNotFoundException;
import com.example.demo.entity.Device;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.neo4j.driver.*;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DeviceServiceTest {

  @Mock
  private Driver driver;

  @Mock
  private Session session;

  @Mock
  private Transaction transaction;

  @Mock
  private Result result;

  @InjectMocks
  private DeviceServiceImplementation deviceService;

  @Test
  void saveDevice_success() {
    Device device = new Device();
    device.setId(1L);
    device.setName("Test Device");
    device.setDeviceType("Type A");
    Map<String, Object> expected = new HashMap<>();
    expected.put("id", device.getId());
    expected.put("name", device.getName());
    expected.put("deviceType", device.getDeviceType());

    when(driver.session()).thenReturn(session);
    when(session.executeWrite(any())).thenReturn(expected);

    Map<String, Object> actual = deviceService.saveDevice(device);

    assertEquals(expected, actual);
    verify(driver).session();
    verify(session).close();
  }

  @Test
  void saveDevice_exception() {
    Device device = new Device();
    device.setId(1L);
    device.setName("Test Device");
    device.setDeviceType("Type A");
    when(driver.session()).thenReturn(session);
    when(session.executeWrite(any())).thenThrow(new RuntimeException("Neo4j Error"));

    assertThrows(RuntimeException.class, () -> deviceService.saveDevice(device));

    verify(driver).session();
    verify(session).close();
  }

  @Test
  void getDevice_success() {
    long deviceId = 1L;
    Map<String, Object> expected = new HashMap<>();
    expected.put("id", deviceId);
    expected.put("name", "Test Device");
    expected.put("deviceType", "Type A");

    when(driver.session()).thenReturn(session);
    when(session.executeRead(any())).thenReturn(expected);

    Map<String, Object> actual = deviceService.getDevice(deviceId);

    assertEquals(expected, actual);
    verify(driver).session();
    verify(session).close();
  }

  @Test
  void getDevice_notFound() {
    long deviceId = 1L;
    when(driver.session()).thenReturn(session);
    when(session.executeRead(any())).thenThrow(DeviceNotFoundException.class); // Corrected

    assertThrows(DeviceNotFoundException.class, () -> deviceService.getDevice(deviceId));
    verify(driver).session();
    verify(session).close();
  }

  @Test
  void getDevice_exception() {
    long deviceId = 1L;
    when(driver.session()).thenReturn(session);
    when(session.executeRead(any())).thenThrow(new RuntimeException("Neo4j Error"));

    assertThrows(RuntimeException.class, () -> deviceService.getDevice(deviceId));
    verify(driver).session();
    verify(session).close();
  }

  @Test
  void modifyDevice_success() {
    Long deviceId = 1L;
    Device device = new Device();
    device.setId(1L);
    device.setName("Test Device");
    device.setDeviceType("Type A");
    Map<String, Object> expected = new HashMap<>();
    expected.put("id", deviceId);
    expected.put("name", device.getName());
    expected.put("deviceType", device.getDeviceType());

    when(driver.session()).thenReturn(session);
    when(session.executeWrite(any())).thenReturn(expected);

    Map<String, Object> actual = deviceService.modifyDevice(deviceId, device);

    assertEquals(expected, actual);
    verify(driver).session();
    verify(session).close();
  }

  @Test
  void modifyDevice_exception() {
    Long deviceId = 1L;
    Device device = new Device();
    device.setId(1L);
    device.setName("Test Device");
    device.setDeviceType("Type A");
    when(driver.session()).thenReturn(session);
    when(session.executeWrite(any())).thenThrow(new RuntimeException("Neo4j Error"));

    assertThrows(RuntimeException.class, () -> deviceService.modifyDevice(deviceId, device));
    verify(driver).session();
    verify(session).close();
  }

  @Test
  void deleteDevice_success() {
    long deviceId = 1L;

    when(driver.session()).thenReturn(session);
    when(session.executeWrite(any())).thenReturn(deviceId);

    long actual = deviceService.deleteDevice(deviceId);

    assertEquals(deviceId, actual);
    verify(driver).session();
    verify(session).close();
  }

  @Test
  void deleteDevice_notFound() {
    long deviceId = 1L;
    when(driver.session()).thenReturn(session);
    when(session.executeWrite(any())).thenThrow(DeviceNotFoundException.class);

    assertThrows(DeviceNotFoundException.class, () -> deviceService.deleteDevice(deviceId));
    verify(driver).session();
    verify(session).close();
  }

  @Test
  void deleteDevice_exception() {
    long deviceId = 1L;
    when(driver.session()).thenReturn(session);
    when(session.executeWrite(any())).thenThrow(new RuntimeException("Neo4j Error"));

    assertThrows(RuntimeException.class, () -> deviceService.deleteDevice(deviceId));
    verify(driver).session();
    verify(session).close();
  }
}
