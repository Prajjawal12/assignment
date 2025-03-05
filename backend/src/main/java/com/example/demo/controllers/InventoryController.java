package com.example.demo.controllers;

import com.example.demo.entity.ShelfV0;
import com.example.demo.service.InventoryServiceImplementation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {

  private static final Logger logger = LoggerFactory.getLogger(InventoryController.class);

  @Autowired
  private InventoryServiceImplementation inventoryServiceImplementation;

  // Saves a new shelf to the inventory
  @PostMapping("/shelf")
  public ResponseEntity<Map<String, Object>> saveShelf(@RequestBody ShelfV0 shelf,
      @RequestParam Boolean confirmModification) {
    logger.info("Attempting to save shelf with details: {}", shelf);

    Map<String, Object> result = inventoryServiceImplementation.saveShelf(shelf, confirmModification);
    logger.info("Shelf saved successfully: {}", result);
    return ResponseEntity.ok(result);

  }

  // // Retrieves a shelf by its ID from the inventory
  // @GetMapping("/shelf/{shelfId}")
  // public ResponseEntity<Map<String, Object>> getShelf(@PathVariable Long
  // shelfId) {
  // logger.info("Attempting to retrieve shelf with ID: {}", shelfId);

  // Map<String, Object> result =
  // inventoryServiceImplementation.getShelf(shelfId);
  // logger.info("Shelf retrieved successfully with ID: {}", shelfId);
  // return ResponseEntity.ok(result);
  // }

  // // Saves a new shelf position to the inventory
  // @PostMapping("/shelf-position")
  // public ResponseEntity<Map<String, Object>> saveShelfPosition(@RequestBody
  // ShelfPositionV0 shelfPosition) {
  // logger.info("Attempting to save shelf position with details: {}",
  // shelfPosition);

  // Map<String, Object> result =
  // inventoryServiceImplementation.saveShelfPosition(shelfPosition);
  // logger.info("Shelf position saved successfully: {}", result);
  // return ResponseEntity.ok(result);
  // }

  // // Retrieves a shelf position by its ID from the inventory
  // @GetMapping("/shelf-position/{shelfPositionId}")
  // public ResponseEntity<Map<String, Object>> getShelfPosition(@PathVariable
  // long shelfPositionId) {
  // logger.info("Attempting to retrieve shelf position with ID: {}",
  // shelfPositionId);
  // Map<String, Object> result =
  // inventoryServiceImplementation.getShelfPosition(shelfPositionId);
  // logger.info("Shelf position retrieved successfully with ID: {}",
  // shelfPositionId);
  // return ResponseEntity.ok(result);
  // }

  // // Adds a shelf position to a device
  // @PostMapping("/add-shelf-position-to-device")
  // public ResponseEntity<Void> addShelfPositionToDevice(@RequestParam Long
  // deviceId,
  // @RequestParam Long shelfPositionId) {
  // logger.info("Attempting to add shelf position with ID: {} to device with ID:
  // {}", shelfPositionId, deviceId);

  // inventoryServiceImplementation.addShelfPositionToDevice(deviceId,
  // shelfPositionId);
  // logger.info("Shelf position with ID: {} successfully added to device with ID:
  // {}", shelfPositionId, deviceId);
  // return ResponseEntity.ok().build();
  // }

  // // Adds a shelf to a shelf position
  // @PostMapping("/add-shelf-to-shelf-position")
  // public ResponseEntity<Void> addShelfToShelfPosition(@RequestParam Long
  // shelfId, @RequestParam Long shelfPositionId) {
  // logger.info("Attempting to add shelf with ID: {} to shelf position with ID:
  // {}", shelfId, shelfPositionId);

  // inventoryServiceImplementation.addShelfToShelfPosition(shelfId,
  // shelfPositionId);
  // logger.info("Shelf with ID: {} successfully added to shelf position with ID:
  // {}", shelfId, shelfPositionId);
  // return ResponseEntity.ok().build();
  // }

  // // Lists all Shelf Position Nodes
  // @GetMapping("/shelf/list")
  // public ResponseEntity<List<Map<String, Object>>> fetchAllShelfNodes() {
  // return ResponseEntity.ok(inventoryServiceImplementation.listAllShelfNodes());
  // }
  // // Lists all Shelf Nodes

  // @GetMapping("/shelf-position/list")
  // public ResponseEntity<List<Map<String, Object>>> fetchAllShelfPositionNodes()
  // {
  // return
  // ResponseEntity.ok(inventoryServiceImplementation.listAllShelfPositionNodes());
  // }
}
