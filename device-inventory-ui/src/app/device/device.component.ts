import { NgIf } from '@angular/common';
import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-device',
  imports: [NgIf , FormsModule],
  templateUrl: './device.component.html',
  styleUrl: './device.component.css',
})
export class DeviceComponent {
showSaveDevicePopup = false;
deviceForm = {
  id:null,
  name: '',
  type:''
}

openSaveDevicePopup():void {
  this.showSaveDevicePopup = true;
  console.log(this.showSaveDevicePopup);
}

closeSaveDevicePopup(): void {
  this.showSaveDevicePopup = false;
  console.log(this.showSaveDevicePopup);



}
submitDeviceForm(): void {
  console.log("Form is submitted");
}
}
