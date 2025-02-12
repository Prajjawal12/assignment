import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { ShelfV0 } from './inventory/shelfv0.model'
import { ShelfPositionV0 } from './inventory/shelfPositionv0.model'
import { catchError, EMPTY, Observable } from 'rxjs';
@Injectable({
  providedIn: 'root'
})
export class InventoryService {
  private apiUrl = 'http://localhost:8080/api/inventory'
  constructor(private http: HttpClient) { }

  saveShelf(shelfV0: ShelfV0): Observable<ShelfV0> {
    return this.http.post<ShelfV0>(`${this.apiUrl}/shelf`, shelfV0);
  }

  getShelf(id: number): Observable<ShelfV0> {
    return this.http.get<ShelfV0>(`${this.apiUrl}/shelf/${id}`)
  }
  saveShelfPosition(shelfPositionV0: ShelfPositionV0): Observable<ShelfPositionV0> {
    return this.http.post<ShelfPositionV0>(`${this.apiUrl}/shelf-position`, shelfPositionV0);
  }

  getShelfPosition(id: number): Observable<ShelfPositionV0> {
    return this.http.get<ShelfPositionV0>(`${this.apiUrl}/shelf-position/${id}`);
  }
  addShelfPositionToDevice(shelfPositionId: number, deviceId: number): Observable<void> {
    const params = new HttpParams()
      .set('deviceId', deviceId.toString())
      .set('shelfPositionId', shelfPositionId.toString())

    return this.http.post<void>(`${this.apiUrl}/add-shelf-position-to-device`, null, { params }).pipe(
      catchError((error) => {
        console.error("Error adding shelf position to device:", error)
        return EMPTY;
      })
    )
  }
  addShelfToShelfPosition(shelfId: number, shelfPositionId: number): Observable<void> {
    const params = new HttpParams().set('shelfId', shelfId.toString()).set('shelfPositionId', shelfPositionId.toString())


    return this.http.post<void>(`${this.apiUrl}/add-shelf-to-shelf-position`, null, { params }).pipe(
      catchError((error) => {
        console.error('Error adding shelf to shelf-position: ', error)
        return EMPTY;
      })
    )
  }

}
