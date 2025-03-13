import { Routes } from '@angular/router';
import path from 'node:path';
import { HomeComponent } from './home/home.component';
import { DeviceComponent } from './device/device.component';
import { InventoryComponent } from './inventory/inventory.component';
import { ShelfListComponent } from './navbar/shelf-list/shelf-list.component';


export const routes: Routes = [
  { path: '', component: HomeComponent },
  { path: 'home', component: HomeComponent },
  { path: 'device', component: DeviceComponent },
  { path: 'inventory', component: InventoryComponent },
  { path: 'shelf-nodes', component: ShelfListComponent }
];
