import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Device } from './device/device.model';

@Injectable({
  providedIn: 'root'
})
export class DeviceService {
  private apiUrl = 'http://localhost:8080/api/device';
  constructor(private http: HttpClient) { }

  getDevice(id: number): Observable<Device> {
    return this.http.get<Device>(`${this.apiUrl}/${id}`);
  }

  saveDevice(device: Device): Observable<Device> {
    return this.http.post<Device>(`${this.apiUrl}/save`, device);
  }
  modifyDevice(id: number, device: Device): Observable<Device> {
    return this.http.put<Device>(`${this.apiUrl}/${id}`, device)
  }
  deleteDevice(id: number): Observable<Device> {
    return this.http.delete<Device>(`${this.apiUrl}/${id}`)
  }
}
