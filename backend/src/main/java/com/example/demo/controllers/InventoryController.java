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

}
