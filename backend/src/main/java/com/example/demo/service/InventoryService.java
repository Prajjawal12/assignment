package com.example.demo.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.example.demo.entity.ShelfV0;

public interface InventoryService {

  // Save a new shelf to the database
  public Map<String, Object> saveShelf(ShelfV0 shelf, boolean confirmModification);

  public void createShelf(ShelfV0 shelf);

  public void modifyShelf(Long shelfId, ShelfV0 shelf);

  public Map<String, Object> getShelfById(Long shelfId);

  public Optional<Map<String, Object>> getConnectedShelfPosition(Long deviceId);

  public List<Map<String, Object>> getAvailableShelfPositions(Long shelfId);

  public void addDeviceToShelfPosition(Long deviceId, Long shelfId, Map<String, Object> shelfPosition);

  public void removeDeviceFromShelfPosition(Long deviceId);
}
