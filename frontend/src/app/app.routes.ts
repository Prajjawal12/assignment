import { Routes } from '@angular/router';
import { SaveDeviceComponent } from './save-device/save-device.component';
import { SaveShelfComponent } from './save-shelf/save-shelf.component';
import { DisplayShelfNodesComponent } from './display-shelf-nodes/display-shelf-nodes.component';

export const routes: Routes = [
    { path: 'save-device', component: SaveDeviceComponent },
    { path: 'save-shelf', component: SaveShelfComponent },
    { path: 'display-shelf-nodes', component: DisplayShelfNodesComponent },
    { path: '', redirectTo: '/display-shelf-nodes', pathMatch: 'full' }
];
