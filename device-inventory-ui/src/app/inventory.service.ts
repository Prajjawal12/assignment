import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class InventoryService {
  private apiUrl = 'http://localhost:8080/api/inventory'; // Your backend URL

  constructor(private http: HttpClient) {}

  // Save a shelf
  saveShelf(shelf: any): Observable<any> {
    return this.http.post(`${this.apiUrl}/saveShelf`, shelf);
  }

  // Get a shelf by ID
  getShelf(shelfId: number): Observable<any> {
    return this.http.get(`${this.apiUrl}/getShelf/${shelfId}`);
  }

  // Save a shelf position
  saveShelfPosition(shelfPosition: any): Observable<any> {
    return this.http.post(`${this.apiUrl}/saveShelfPosition`, shelfPosition);
  }

  // Get a shelf position by ID
  getShelfPosition(shelfPositionId: number): Observable<any> {
    return this.http.get(`${this.apiUrl}/getShelfPosition/${shelfPositionId}`);
  }

  // Add a shelf to a shelf position
  addShelfToShelfPosition(
    shelfId: number,
    shelfPositionId: number
  ): Observable<any> {
    return this.http.post(`${this.apiUrl}/addShelfToShelfPosition`, {
      shelfId,
      shelfPositionId,
    });
  }

  // Add a shelf position to a device
  addShelfPositionToDevice(
    deviceId: number,
    shelfPositionId: number
  ): Observable<any> {
    return this.http.post(`${this.apiUrl}/addShelfPositionToDevice`, {
      deviceId,
      shelfPositionId,
    });
  }
}
