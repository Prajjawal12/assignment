import { Routes } from '@angular/router';
import path from 'node:path';
import { HomeComponent } from './home/home.component';
import { DeviceComponent } from './device/device.component';
import { InventoryComponent } from './inventory/inventory.component';
import { DeviceListComponent } from './navbar/listing/device-list/device-list.component';
import { ShelfPositionsListComponent } from './navbar/listing/shelf-positions-list/shelf-positions-list.component';
import { ShelfListComponent } from './navbar/listing/shelf-list/shelf-list.component';
export const routes: Routes = [
  { path: '', component: HomeComponent },
  { path: 'home', component: HomeComponent },
  { path: 'device', component: DeviceComponent },
  { path: 'inventory', component: InventoryComponent },
  { path: 'device-list', component: DeviceListComponent },
  { path: 'shelf-position-list', component: ShelfPositionsListComponent },
  { path: 'shelf-list', component: ShelfListComponent }
];
