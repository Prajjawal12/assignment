package com.example.modified_assignment_backend.controllers;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.modified_assignment_backend.entity.ShelfPositionV0;
import com.example.modified_assignment_backend.entity.ShelfV0;
import com.example.modified_assignment_backend.service.InventoryServiceImplementation;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {
    @Autowired
    private InventoryServiceImplementation inventoryServiceImplementation;

    // Saves a new shelf to the inventory
    @PostMapping("/shelf")
    public ResponseEntity<Map<String, Object>> saveShelf(@RequestBody ShelfV0 shelf) throws Exception {
        try {
            Map<String, Object> result = inventoryServiceImplementation.saveShelf(shelf);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            throw new Exception("Error occurred while saving shelf: " + e.getMessage());
        }
    }

    // Retrieves a shelf by its ID from the inventory
    @GetMapping("/shelf/{shelfId}")
    public ResponseEntity<Map<String, Object>> getShelf(@PathVariable Long shelfId) throws Exception {
        try {
            Map<String, Object> result = inventoryServiceImplementation.getShelf(shelfId);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            throw new Exception("Error occurred while retrieving shelf with ID " + shelfId + ": " + e.getMessage());
        }
    }

    // Saves a new shelf position to the inventory
    @PostMapping("/shelf-position")
    public ResponseEntity<Map<String, Object>> saveShelfPosition(@RequestBody ShelfPositionV0 shelfPosition)
            throws Exception {
        try {
            Map<String, Object> result = inventoryServiceImplementation.saveShelfPosition(shelfPosition);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            throw new Exception("Error occurred while saving shelf position: " + e.getMessage());
        }
    }

    // Retrieves a shelf position by its ID from the inventory
    @GetMapping("/shelf-position/{shelfPositionId}")
    public ResponseEntity<Map<String, Object>> getShelfPosition(@PathVariable long shelfPositionId) throws Exception {
        try {
            Map<String, Object> result = inventoryServiceImplementation.getShelfPosition(shelfPositionId);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            throw new Exception(
                    "Error occurred while retrieving shelf position with ID " + shelfPositionId + ": "
                            + e.getMessage());
        }
    }

    // Adds a shelf position to a device
    @PostMapping("/add-shelf-position-to-device")
    public ResponseEntity<Void> addShelfPositionToDevice(@RequestParam Long deviceId,
            @RequestParam Long shelfPositionId)
            throws Exception {
        try {
            inventoryServiceImplementation.addShelfPositionToDevice(deviceId, shelfPositionId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            throw new Exception("Error occurred while adding shelf position to device: " + e.getMessage());
        }
    }

    // Adds a shelf to a shelf position
    @PostMapping("/add-shelf-to-shelf-position")
    public ResponseEntity<Void> addShelfToShelfPosition(@RequestParam Long shelfId, @RequestParam Long shelfPositionId)
            throws Exception {
        try {
            inventoryServiceImplementation.addShelfToShelfPosition(shelfId, shelfPositionId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            throw new Exception("Error occurred while adding shelf to shelf position: " + e.getMessage());
        }
    }
}
