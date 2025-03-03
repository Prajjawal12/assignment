// package com.example.demo.service;

// import static org.junit.jupiter.api.Assertions.assertEquals;
// import static org.junit.jupiter.api.Assertions.assertThrows;
// import static org.mockito.ArgumentMatchers.any;
// import static org.mockito.Mockito.doNothing;
// import static org.mockito.Mockito.doThrow;
// import static org.mockito.Mockito.verify;
// import static org.mockito.Mockito.when;

// import java.util.HashMap;
// import java.util.Map;

// import org.junit.jupiter.api.Test;
// import org.junit.jupiter.api.extension.ExtendWith;
// import org.mockito.InjectMocks;
// import org.mockito.Mock;
// import org.mockito.junit.jupiter.MockitoExtension;
// import org.neo4j.driver.Driver;
// import org.neo4j.driver.Result;
// import org.neo4j.driver.Session;
// import org.neo4j.driver.Transaction;

// import com.example.demo.customExceptions.RecordNotFoundException;
// import com.example.demo.entity.ShelfPositionV0;
// import com.example.demo.entity.ShelfV0;

// @ExtendWith(MockitoExtension.class)
// public class InventoryServiceTest {

// @Mock
// private Driver driver;

// @Mock
// private Session session;

// @Mock
// private Transaction transaction;

// @Mock
// private Result result;

// @InjectMocks
// private InventoryServiceImplementation inventoryServiceImplementation;

// @Test
// void saveShelf_success() {
// ShelfV0 shelfV0 = new ShelfV0();
// shelfV0.setId(1L);
// shelfV0.setName("Test Shelf");
// shelfV0.setShelfType("Type A");
// Map<String, Object> expected = new HashMap<>();
// expected.put("id", shelfV0.getId());
// expected.put("name", shelfV0.getName());
// expected.put("shelfType", shelfV0.getShelfType());

// when(driver.session()).thenReturn(session);
// when(session.executeWrite(any())).thenReturn(expected);

// Map<String, Object> actual =
// inventoryServiceImplementation.saveShelf(shelfV0);

// assertEquals(expected, actual);
// verify(driver).session();
// verify(session).close();
// }

// @Test
// void saveShelf_recordNotFound() {
// ShelfV0 shelf = new ShelfV0();
// shelf.setId(1L);
// when(driver.session()).thenReturn(session);
// when(session.executeWrite(any())).thenThrow(RecordNotFoundException.class);
// assertThrows(RecordNotFoundException.class, () ->
// inventoryServiceImplementation.saveShelf(shelf));
// verify(driver).session();
// verify(session).close();
// }

// @Test
// void saveShelf_exception() {
// ShelfV0 shelf = new ShelfV0();
// shelf.setId(1L);
// when(driver.session()).thenReturn(session);
// when(session.executeWrite(any())).thenThrow(new RuntimeException("Neo4j
// Error"));
// assertThrows(RuntimeException.class, () ->
// inventoryServiceImplementation.saveShelf(shelf));
// verify(driver).session();
// verify(session).close();
// }

// @Test
// void getShelf_success() {
// long shelfId = 1L;
// Map<String, Object> expected = new HashMap<>();
// expected.put("id", shelfId);
// expected.put("name", "Test Shelf");
// expected.put("shelfType", "Type A");
// when(driver.session()).thenReturn(session);
// when(session.executeRead(any())).thenReturn(expected);
// Map<String, Object> actual =
// inventoryServiceImplementation.getShelf(shelfId);
// assertEquals(expected, actual);
// verify(driver).session();
// verify(session).close();
// }

// @Test
// void getShelf_recordNotFound() {
// long shelfId = 1L;
// when(driver.session()).thenReturn(session);
// when(session.executeRead(any())).thenThrow(RecordNotFoundException.class);
// assertThrows(RecordNotFoundException.class, () ->
// inventoryServiceImplementation.getShelf(shelfId));
// verify(driver).session();
// verify(session).close();
// }

// @Test
// void getShelf_exception() {
// long shelfId = 1L;
// when(driver.session()).thenReturn(session);
// when(session.executeRead(any())).thenThrow(new RuntimeException("Neo4j
// Error"));
// assertThrows(RuntimeException.class, () ->
// inventoryServiceImplementation.getShelf(shelfId));
// verify(driver).session();
// verify(session).close();
// }

// @Test
// void saveShelfPosition_success() {
// ShelfPositionV0 shelfPosition = new ShelfPositionV0();
// shelfPosition.setId(1L);
// shelfPosition.setName("Test Position");
// Map<String, Object> expected = new HashMap<>();
// expected.put("id", shelfPosition.getId());
// expected.put("name", shelfPosition.getName());
// when(driver.session()).thenReturn(session);
// when(session.executeWrite(any())).thenReturn(expected);
// Map<String, Object> actual =
// inventoryServiceImplementation.saveShelfPosition(shelfPosition);
// assertEquals(expected, actual);
// verify(driver).session();
// verify(session).close();
// }

// @Test
// void saveShelfPosition_recordNotFound() {
// ShelfPositionV0 shelfPosition = new ShelfPositionV0();
// shelfPosition.setId(1L);
// when(driver.session()).thenReturn(session);
// when(session.executeWrite(any())).thenThrow(RecordNotFoundException.class);
// assertThrows(RecordNotFoundException.class,
// () -> inventoryServiceImplementation.saveShelfPosition(shelfPosition));
// verify(driver).session();
// verify(session).close();
// }

// @Test
// void saveShelfPosition_exception() {
// ShelfPositionV0 shelfPosition = new ShelfPositionV0();
// shelfPosition.setId(1L);
// when(driver.session()).thenReturn(session);
// when(session.executeWrite(any())).thenThrow(new RuntimeException("Neo4j
// Error"));
// assertThrows(RuntimeException.class, () ->
// inventoryServiceImplementation.saveShelfPosition(shelfPosition));
// verify(driver).session();
// verify(session).close();
// }

// @Test
// void getShelfPosition_success() {
// long shelfPositionId = 1L;
// Map<String, Object> expected = new HashMap<>();
// expected.put("id", shelfPositionId);
// expected.put("name", "Test Position");
// when(driver.session()).thenReturn(session);
// when(session.executeRead(any())).thenReturn(expected);
// Map<String, Object> actual =
// inventoryServiceImplementation.getShelfPosition(shelfPositionId);
// assertEquals(expected, actual);
// verify(driver).session();
// verify(session).close();
// }

// @Test
// void getShelfPosition_recordNotFound() {
// long shelfPositionId = 1L;
// when(driver.session()).thenReturn(session);
// when(session.executeRead(any())).thenThrow(RecordNotFoundException.class);
// assertThrows(RecordNotFoundException.class,
// () -> inventoryServiceImplementation.getShelfPosition(shelfPositionId));
// verify(driver).session();
// verify(session).close();
// }

// @Test
// void getShelfPosition_exception() {
// long shelfPositionId = 1L;
// when(driver.session()).thenReturn(session);
// when(session.executeRead(any())).thenThrow(new RuntimeException("Neo4j
// Error"));
// assertThrows(RuntimeException.class, () ->
// inventoryServiceImplementation.getShelfPosition(shelfPositionId));
// verify(driver).session();
// verify(session).close();
// }

// @Test
// void addShelfPositionToDevice_success() {
// long deviceId = 1L;
// long shelfPositionId = 1L;

// when(driver.session()).thenReturn(session);
// doNothing().when(session).executeWriteWithoutResult(any());

// inventoryServiceImplementation.addShelfPositionToDevice(deviceId,
// shelfPositionId);

// verify(driver).session();
// verify(session).executeWriteWithoutResult(any());
// verify(session).close();
// }

// @Test
// void addShelfPositionToDevice_exception() {
// long deviceId = 1L;
// long shelfPositionId = 1L;

// when(driver.session()).thenReturn(session);

// doThrow(new RuntimeException("Neo4j
// Error")).when(session).executeWriteWithoutResult(any());

// assertThrows(RuntimeException.class,
// () -> inventoryServiceImplementation.addShelfPositionToDevice(deviceId,
// shelfPositionId));

// verify(driver).session();
// verify(session).close();
// }

// @Test
// void addShelfToShelfPosition_success() {
// long shelfId = 1L;
// long shelfPositionId = 1L;
// when(driver.session()).thenReturn(session);
// doNothing().when(session).executeWriteWithoutResult(any());
// inventoryServiceImplementation.addShelfToShelfPosition(shelfId,
// shelfPositionId);
// verify(driver).session();
// verify(session).executeWriteWithoutResult(any());
// verify(session).close();
// }

// @Test
// void addShelfToShelfPosition_exception() {
// long shelfId = 1L;
// long shelfPositionId = 1L;
// when(driver.session()).thenReturn(session);

// doThrow(new RuntimeException("Neo4j
// Error")).when(session).executeWriteWithoutResult(any());
// assertThrows(RuntimeException.class,
// () -> inventoryServiceImplementation.addShelfToShelfPosition(shelfId,
// shelfPositionId));
// verify(driver).session();
// verify(session).close();
// }

// }
