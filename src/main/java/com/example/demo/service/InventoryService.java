package com.example.demo.service;

import java.util.Map;

import com.example.demo.entity.ShelfPositionV0;
import com.example.demo.entity.ShelfV0;

public interface InventoryService {

  public Map<String, Object> saveShelf(ShelfV0 shelf);

  public Map<String, Object> getShelf(Long shelfId);

  public Map<String, Object> saveShelfPosition(ShelfPositionV0 shelfPositionV0);

  public Map<String, Object> getShelfPosition(long shelfPositionId);

  public void addShelfPositionToDevice(Long deviceId, Long shelfPositionId);

  public void addShelfToShelfPosition(Long shelfId, Long shelfPositionId);

}
