import { Component } from '@angular/core';
import { DeviceService } from '../device.service';

@Component({
  selector: 'app-device',
  imports: [],
  templateUrl: './device.component.html',
  styleUrl: './device.component.css',
})
export class DeviceComponent {
  device: any;
  deviceId: number = 1;

  constructor(private deviceService: DeviceService) {}

  saveDevice(): void {
    const newDevice = {
      name: 'New Device',
      description: 'A newly added device',
    };

    this.deviceService.saveDevice(newDevice).subscribe(
      (response) => {
        this.device = response;
        console.log('Device saved successfully:', response);
      },
      (error) => {
        console.error('Error saving device:', error);
      }
    );
  }

  getDevice(): void {
    this.deviceService.getDevice(this.deviceId).subscribe(
      (response) => {
        this.device = response;
        console.log('Device fetched successfully:', response);
      },
      (error) => {
        console.error('Error fetching device:', error);
      }
    );
  }

  modifyDevice(): void {
    const updatedDevice = {
      name: 'Updated Device',
      description: 'An updated device description',
    };

    this.deviceService.modifyDevice(this.deviceId, updatedDevice).subscribe(
      (response) => {
        this.device = response;
        console.log('Device modified successfully:', response);
      },
      (error) => {
        console.error('Error modifying device:', error);
      }
    );
  }

  deleteDevice(): void {
    this.deviceService.deleteDevice(this.deviceId).subscribe(
      (response) => {
        console.log('Device deleted successfully:', response);
      },
      (error) => {
        console.error('Error deleting device:', error);
      }
    );
  }
}
