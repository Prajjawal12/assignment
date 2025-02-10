import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { DeviceModule } from './device/device.module';
import { HttpClientModule } from '@angular/common/http';

import { AppComponent } from './app.component';
import { InventoryModule } from './inventory/inventory.module';
import { CommonModule } from '@angular/common';
import { DeviceService } from './device.service';
import { InventoryService } from './inventory.service';
import { DeviceComponent } from './device/device.component';
import { InventoryComponent } from './inventory/inventory.component';

@NgModule({
  declarations: [],
  imports: [
    BrowserModule,


    CommonModule,
    HttpClientModule,
  ],
  providers: [DeviceService, InventoryService],
  bootstrap: [],
})
export class AppModule {}
