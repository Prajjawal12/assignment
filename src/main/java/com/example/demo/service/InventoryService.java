package com.example.demo.service;

import java.util.Map;

import com.example.demo.entity.ShelfPositionV0;
import com.example.demo.entity.ShelfV0;

public interface InventoryService {

  // Save a new shelf to the database
  public Map<String, Object> saveShelf(ShelfV0 shelf);

  // Retrieve a shelf by its ID
  public Map<String, Object> getShelf(Long shelfId);

  // Save a new shelf position to the database
  public Map<String, Object> saveShelfPosition(ShelfPositionV0 shelfPositionV0);

  // Retrieve a shelf position by its ID
  public Map<String, Object> getShelfPosition(long shelfPositionId);

  // Associate a shelf position with a device
  public void addShelfPositionToDevice(Long deviceId, Long shelfPositionId);

  // Associate a shelf with a shelf position
  public void addShelfToShelfPosition(Long shelfId, Long shelfPositionId);

}
