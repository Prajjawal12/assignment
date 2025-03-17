import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { Subscription } from 'rxjs';
import { DeviceService } from '../device.service';
import { Device } from '../models/device.model';

@Component({
  selector: 'app-save-device',
  imports: [ReactiveFormsModule],
  templateUrl: './save-device.component.html',
  styleUrl: './save-device.component.css'
})
export class SaveDeviceComponent implements OnDestroy {
  deviceForm: FormGroup;
  deviceExists = false;
  private subscriptions: Subscription[] = []

  constructor(private fb: FormBuilder, private deviceService: DeviceService) {
    this.deviceForm = this.fb.group({
      id: ['', Validators.required],
      name: ['', Validators.required],
      deviceType: ['', Validators.required]
    });
  }



  ngOnDestroy(): void {
    this.subscriptions.forEach(sub => sub.unsubscribe());
  }

  onSubmit(): void {
    if (this.deviceForm.valid) {
      const device: Device = this.deviceForm.value;
      const id = device.id;

      this.subscriptions.push(this.deviceService.getDeviceById(id).subscribe(
        {
          next: (existingDevice) => {
            this.deviceExists = true;
            if (confirm('Device ID already exists. Do you want to modify?')) {
              this.saveDevice(device, true);
            }
          },
          error: (error) => {
            this.deviceExists = false;
            this.saveDevice(device, false)
          }
        }
      ))
    }
  }

  private saveDevice(device: Device, confirmModification: boolean): void {
    this.subscriptions.push(this.deviceService.saveDevice(device, confirmModification).subscribe({
      next: (savedDevice) => {
        alert('Device saved succesfully.');
        this.deviceForm.reset()
      },
      error: (error) => {
        alert('Error saving device : ' + error);
      }
    }))
  }



}
