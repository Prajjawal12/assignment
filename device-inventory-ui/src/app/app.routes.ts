import { Routes } from '@angular/router';
import { DeviceComponent } from './device/device.component';
import { HomeComponent } from './home/home.component';
import { InventoryComponent } from './inventory/inventory.component';

export const routes: Routes = [
  { path: '', component: HomeComponent },
  { path: 'device', component: DeviceComponent },
  { path: 'inventory', component: InventoryComponent },
];
