import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { InventoryComponent } from './inventory.component';
import { DeviceComponent } from '../device/device.component';

@NgModule({
  declarations: [],
  imports: [CommonModule,DeviceComponent],
  exports: [],
})
export class InventoryModule {}
