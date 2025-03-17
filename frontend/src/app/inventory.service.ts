import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Shelf } from './models/shelf.model';
import { Observable } from 'rxjs';
import { ConnectedPosition } from './models/connected-position.model';
import { ShelfPosition } from './models/shelf-position.model';

@Injectable({
  providedIn: 'root'
})
export class InventoryService {

  private apiUrl = 'http://localhost:8080/api/inventory';
  constructor(private http: HttpClient) { }

  saveShelf(shelf: Shelf, confirmModification: boolean): Observable<Shelf> {
    return this.http.post<Shelf>(`${this.apiUrl}/shelf?confirmModification=${confirmModification}`, shelf)
  }

  getShelfById(shelfId: number): Observable<Shelf> {
    return this.http.get<Shelf>(`${this.apiUrl}/shelf/${shelfId}`)
  }

  addDeviceToShelfPosition(deviceId: number, shelfId: number, position: number): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/connected-positions?deviceId=${deviceId}&shelfId=${shelfId}&position=${position}`, null)

  }

  removeDeviceFromShelfPosition(deviceId: number, shelfId: number, position: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/connected-positions?deviceId=${deviceId}&shelfId=${shelfId}&position=${position}`)
  }


  getAllConnectedShelfPositions(): Observable<ConnectedPosition[]> {
    return this.http.get<ConnectedPosition[]>(`${this.apiUrl}/connected-positions`)
  }

  getAvailableShelfPositions(shelfId: number): Observable<ShelfPosition[]> {
    return this.http.get<ShelfPosition[]>(`${this.apiUrl}/shelves/${shelfId}/available-positions`)
  }

  getAllShelves(): Observable<Shelf[]> {
    return this.http.get<Shelf[]>(`${this.apiUrl}/shelves`)
  }
}
