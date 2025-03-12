import { Component } from '@angular/core';
import { Device } from '../../../device/device.model';
import { DeviceService } from '../../../device.service';
import { NgFor, NgIf } from '@angular/common';

@Component({
  selector: 'app-device-list',
  imports: [NgIf, NgFor],
  templateUrl: './device-list.component.html',
  styleUrl: './device-list.component.css'
})
export class DeviceListComponent {
  devices: Device[] = []
  columns: string[] = ['id', 'name', 'deviceType']

  constructor(private deviceService: DeviceService) { }

  ngOnInit(): void {
    this.deviceService.getAllDevices().subscribe(
      (devices) => {
        this.devices = devices;
      },
      (error) => {
        console.error('Error fetching device', error);
      }
    )
  }

}
