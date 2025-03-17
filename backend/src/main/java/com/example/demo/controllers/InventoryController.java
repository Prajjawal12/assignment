package com.example.demo.controllers;

import com.example.demo.entity.ShelfV0;
import com.example.demo.service.InventoryServiceImplementation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {

  @Autowired
  private InventoryServiceImplementation inventoryServiceImplementation;

  // Saves a new shelf to the inventory
  // @PostMapping("/shelf")
  @RequestMapping(value = "/shelf", method = RequestMethod.POST)
  public ResponseEntity<Map<String, Object>> saveShelf(@RequestBody ShelfV0 shelf,
      @RequestParam Boolean confirmModification) {
    Map<String, Object> result = inventoryServiceImplementation.saveShelf(shelf, confirmModification);
    return ResponseEntity.ok(result);

  }

  @RequestMapping(value = "/shelf/{shelfId}", method = RequestMethod.GET)
  public ResponseEntity<Map<String, Object>> getShelfById(@PathVariable Long shelfId) {
    return ResponseEntity.ok(inventoryServiceImplementation.getShelfById(shelfId));
  }

  // @GetMapping("/connected-positions")
  @RequestMapping(value = "/connected-positions", method = RequestMethod.GET)
  public ResponseEntity<List<Map<String, Object>>> getAllConnectedPositions() {
    return ResponseEntity.ok(inventoryServiceImplementation.getAllConnectedShelfPositions());
  }

  // @DeleteMapping("/connected-positions")
  @RequestMapping(value = "/connected-positions", method = RequestMethod.DELETE)
  public ResponseEntity<Void> deleteConnectedPosition(@RequestParam Long deviceId, @RequestParam Long shelfId,
      @RequestParam Long position) {
    inventoryServiceImplementation.removeDeviceFromShelfPosition(deviceId, shelfId, position);
    return ResponseEntity.ok().build();
  }

  // @GetMapping("/shelves/{shelfId}/available-positions")
  @RequestMapping(value = "/shelves/{shelfId}/available-positions", method = RequestMethod.GET)
  public ResponseEntity<List<Map<String, Object>>> getAvailablePositions(@PathVariable Long shelfId) {
    return ResponseEntity.ok(inventoryServiceImplementation.getAvailableShelfPositions(shelfId));
  }

  // @PostMapping("/connected-positions")
  @RequestMapping(value = "/connected-positions", method = RequestMethod.POST)
  public ResponseEntity<Void> addConnectedPosition(@RequestParam Long deviceId, @RequestParam Long shelfId,
      @RequestParam Long position) {
    inventoryServiceImplementation.addDeviceToShelfPosition(deviceId, shelfId, position);
    return ResponseEntity.ok().build();
  }

  @RequestMapping(value = "/shelves", method = RequestMethod.GET)
  public ResponseEntity<List<Map<String, Object>>> getAllShelfNodes() {
    return ResponseEntity.ok(inventoryServiceImplementation.getAllShelves());
  }
}
