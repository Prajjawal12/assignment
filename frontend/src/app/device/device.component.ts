import { NgIf } from '@angular/common';
import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms'
import { DeviceService } from '../device.service';
import { ToastrService } from 'ngx-toastr';

@Component({
  selector: 'app-device',
  imports: [FormsModule, NgIf],
  templateUrl: './device.component.html',
  styleUrl: './device.component.css'
})
export class DeviceComponent {
  showForm = false;
  formType: 'save' | 'modify' | 'get' | 'delete' = 'save';
  device = { id: NaN, name: '', deviceType: '' }
  deviceId: number = NaN;
  showSuccess(response: string) {
    this.toastr.success(response)
  }
  showError(error: string) {
    this.toastr.error(error)
  }
  constructor(private deviceService: DeviceService, private toastr: ToastrService) { }

  showSaveForm() {
    this.showForm = true;
    this.formType = 'save'
  }

  showModifyForm() {
    this.showForm = true;
    this.formType = 'modify'
  }

  showGetForm() {
    this.showForm = true;
    this.formType = 'get'
  }

  showDeleteForm() {
    this.showForm = true;
    this.formType = 'delete'
  }

  hideForm() {
    this.showForm = false;
    this.device = { id: NaN, name: '', deviceType: '' };
    this.deviceId = NaN
  }

  onSubmit() {
    if (this.formType === 'save') {
      this.deviceService.saveDevice(this.device).subscribe(
        (response) => {
          console.log('Device Saved : ', response);
          this.showSuccess(JSON.stringify(response))
          this.hideForm();
        }, (error) => {

          console.error('Error saving device:', error)
          this.showError(error)
        }
      )
    }
    else if (this.formType === 'modify') {
      this.deviceService.modifyDevice(this.deviceId, this.device).subscribe(
        (response) => {
          console.log('Device Modified', response);
          this.showSuccess(JSON.stringify(response))
          this.hideForm();
        },
        (error) => {
          console.error("Error modifying device: ", error)
          this.showError(error)
        }
      )
    } else if (this.formType === 'get') {
      this.deviceService.getDevice(this.deviceId).subscribe(
        (response) => {
          console.log("Received Device", response)
          this.showSuccess(JSON.stringify(response))
          this.hideForm()
        },
        (error) => {
          console.error("Error fetching device ", error);
          this.showError(error)


        }
      )
    }
    else if (this.formType === 'delete') {
      this.deviceService.deleteDevice(this.deviceId).subscribe(
        (response) => {
          console.log("Device Deleted", response)
          this.showSuccess(JSON.stringify(response))
          this.hideForm()
        },
        (error) => {
          console.error("Error deleting device", error)
          this.showError(error)

        }
      )
    }
  }
}
