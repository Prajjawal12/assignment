package com.example.demo.service;

import com.example.demo.entity.ShelfPositionV0;
import com.example.demo.entity.ShelfV0;
import com.example.demo.customExceptions.RecordNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.neo4j.driver.Driver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class InventoryServiceImplementationTest {

  @Autowired
  private InventoryServiceImplementation inventoryService;

  @Autowired
  private Driver driver;

  private ShelfV0 testShelf;
  private ShelfPositionV0 testShelfPosition;

  @BeforeEach
  void setUp() {
    // Create test shelf with default type
    testShelf = new ShelfV0();
    testShelf.setId(1L);
    testShelf.setName("Test Shelf");
    testShelf.setShelfType("Type A");

    // Create test shelf position
    testShelfPosition = new ShelfPositionV0();
    testShelfPosition.setId(1L);
    testShelfPosition.setName("Position 1");
  }

  @Test
  void saveShelfTest() {
    var result = inventoryService.saveShelf(testShelf);
    assertNotNull(result, "The result map should not be null");
    assertEquals(testShelf.getName(), result.get("name"));

    if (testShelf.getShelfType() != null) {
      assertEquals(testShelf.getShelfType(), result.get("shelfType"), "Shelf type should match");
    } else {
      assertNull(result.get("shelfType"), "Shelf type should be null if unset");
    }
  }

  @Test
  void getShelfTest() {
    inventoryService.saveShelf(testShelf);
    var result = inventoryService.getShelf(testShelf.getId());
    assertNotNull(result, "The result map should not be null");
    assertEquals(testShelf.getName(), result.get("name"));

    if (testShelf.getShelfType() != null) {
      assertEquals(testShelf.getShelfType(), result.get("shelfType"));
    } else {
      assertNull(result.get("shelfType"));
    }
  }

  @Test
  void getShelfNotFoundTest() {
    long nonExistentShelfId = 9999L;
    assertThrows(RecordNotFoundException.class,
        () -> inventoryService.getShelf(nonExistentShelfId),
        "Expected an exception for a non-existent shelf");
  }

  @Test
  void saveShelfPositionTest() {
    var result = inventoryService.saveShelfPosition(testShelfPosition);
    assertNotNull(result, "The result map should not be null");
    assertEquals(testShelfPosition.getName(), result.get("name"));
  }

  @Test
  void getShelfPositionTest() {
    inventoryService.saveShelfPosition(testShelfPosition);
    var result = inventoryService.getShelfPosition(testShelfPosition.getId());
    assertNotNull(result, "The result map should not be null");
    assertEquals(testShelfPosition.getName(), result.get("name"));
  }

  @Test
  void getShelfPositionNotFoundTest() {
    long nonExistentPositionId = 9999L;
    assertThrows(RecordNotFoundException.class,
        () -> inventoryService.getShelfPosition(nonExistentPositionId),
        "Expected an exception for a non-existent shelf position");
  }

  @Test
  void addShelfPositionToDeviceTest() {
    inventoryService.saveShelfPosition(testShelfPosition);
    assertDoesNotThrow(() -> inventoryService.addShelfPositionToDevice(1L, testShelfPosition.getId()),
        "Should not throw an exception when adding a valid shelf position to a device");
  }

  @Test
  void addShelfToShelfPositionTest() {
    inventoryService.saveShelf(testShelf);
    inventoryService.saveShelfPosition(testShelfPosition);
    assertDoesNotThrow(() -> inventoryService.addShelfToShelfPosition(testShelf.getId(), testShelfPosition.getId()),
        "Should not throw an exception when adding a shelf to a valid shelf position");
  }
}
