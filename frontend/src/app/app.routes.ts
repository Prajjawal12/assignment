import { Routes } from '@angular/router';
import path from 'node:path';
import { HomeComponent } from './home/home.component';
import { DeviceComponent } from './device/device.component';
import { InventoryComponent } from './inventory/inventory.component';

export const routes: Routes = [
  {path:'home' , component:HomeComponent},
  {path:'device', component:DeviceComponent},
  {path:'inventory' , component:InventoryComponent},
  {path:'' , redirectTo:'/home' , pathMatch:'full'}
];
