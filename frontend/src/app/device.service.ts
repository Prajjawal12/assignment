import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Device } from './models/device.model';
import { catchError, Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class DeviceService {
  private apiUrl = 'http://localhost:8080/api/device';
  constructor(private http: HttpClient) { }
  saveDevice(device: Device, confirmModification: boolean): Observable<Device> {
    return this.http.post<Device>(`${this.apiUrl}/save?confirmModification=${confirmModification}`, device);
  }

  getDeviceById(deviceId: number): Observable<Device> {
    return this.http.get<Device>(`${this.apiUrl}/fetch/${deviceId}`)
  }

  deleteDevice(deviceId: number): Observable<number> {
    return this.http.delete<number>(`${this.apiUrl}/delete/${deviceId}`)
  }

  listAllDevices(): Observable<Device[]> {
    return this.http.get<Device[]>(`${this.apiUrl}/list`)
  }
}
