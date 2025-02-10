import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class DeviceService {
  private apiUrl = 'http://localhost:8080/api/device'; // Your backend URL

  constructor(private http: HttpClient) {}

  // Save a device
  saveDevice(device: any): Observable<any> {
    return this.http.post(`${this.apiUrl}/save`, device);
  }

  // Get a device by ID
  getDevice(deviceId: number): Observable<any> {
    return this.http.get(`${this.apiUrl}/${deviceId}`);
  }

  // Modify a device
  modifyDevice(id: number, device: any): Observable<any> {
    return this.http.put(`${this.apiUrl}/modify/${id}`, device);
  }

  // Delete a device
  deleteDevice(deviceId: number): Observable<any> {
    return this.http.delete(`${this.apiUrl}/delete/${deviceId}`);
  }
}
