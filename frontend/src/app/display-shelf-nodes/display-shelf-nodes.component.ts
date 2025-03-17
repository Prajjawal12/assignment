import { Component, OnDestroy, OnInit } from '@angular/core';
import { ConnectedPosition } from '../models/connected-position.model';
import { Device } from '../models/device.model';
import { Shelf } from '../models/shelf.model';
import { ShelfPosition } from '../models/shelf-position.model';
import { Subscription } from 'rxjs';
import { InventoryService } from '../inventory.service';
import { DeviceService } from '../device.service';
import { NgFor } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-display-shelf-nodes',
  imports: [NgFor, FormsModule],
  templateUrl: './display-shelf-nodes.component.html',
  styleUrl: './display-shelf-nodes.component.css'
})
export class DisplayShelfNodesComponent implements OnInit, OnDestroy {
  connectedPositions: ConnectedPosition[] = []
  devices: Device[] = []
  shelves: Shelf[] = []
  shelfPosition: ShelfPosition[] = []


  selectedDevice: number | null = null;
  selectedShelf: number | null = null;
  selectedPosition: number | null = null;

  private subscriptions: Subscription[] = []

  constructor(private inventoryService: InventoryService, private deviceService: DeviceService) { }
  ngOnDestroy(): void {
    this.subscriptions.forEach(sub => sub.unsubscribe());
  }

  ngOnInit(): void {
    this.fetchConnectedPositions();
    this.fetchDevices();
  }

  fetchConnectedPositions(): void {
    this.subscriptions.push(this.inventoryService.getAllConnectedShelfPositions().subscribe({
      next: (positions) => {
        this.connectedPositions = positions
      },
      error: (error) => {
        alert('Error fetching connected positions :' + error)
      }
    }))
  }
  fetchDevices(): void {
    this.subscriptions.push(this.deviceService.listAllDevices().subscribe({
      next: (devices) => {
        this.devices = devices;
      },
      error: (error) => {
        alert('Error fetching devices :' + error)
      }
    }))
  }

  deletedDeviceFromShelfPosition(position: ConnectedPosition): void {
    this.subscriptions.push(this.inventoryService.removeDeviceFromShelfPosition(position.deviceId, position.shelfId, position.position).subscribe({
      next: () => {
        alert(`Device ${position.deviceId} is succesfully removed from position ${position.position} connected to shelf with id ${position.shelfId}`)
        this.fetchConnectedPositions();
      },
      error: (error) => {
        alert(`Error in performing deletetion ${error}`)
      }
    }))
  }
  fetchShelves(): void {
    if (this.selectedDevice) {
      this.subscriptions.push(this.inventoryService.getAllShelves().subscribe({
        next: (shelves) => {
          this.shelves = shelves.filter(shelf => {
            return this.connectedPositions.find(connectedPosition => connectedPosition.deviceId === this.selectedDevice && connectedPosition.shelfId === shelf.id) === undefined;
          })
        },
        error: (error) => {
          alert('Error fetching shelves : ' + error)
        }
      }))
    }
  }
  fetchShelfPositions(): void {
    if (this.selectedShelf) {
      this.subscriptions.push(this.inventoryService.getAvailableShelfPositions(this.selectedShelf).subscribe({
        next: (positions) => {
          this.shelfPosition = positions;
        },
        error: (error) => {
          alert('Error fetching shelf positions: ' + error)
        }
      }))
    }
  }

  addConnection(): void {
    if (this.selectedDevice && this.selectedShelf && this.selectedPosition) {
      this.subscriptions.push(this.inventoryService.addDeviceToShelfPosition(this.selectedDevice, this.selectedShelf, this.selectedPosition).subscribe({
        next: () => {
          alert('Connection added succesfully.')
          this.fetchConnectedPositions();
          this.selectedDevice = null;
          this.selectedShelf = null;
          this.selectedPosition = null;
        },
        error: (error) => {
          alert('Error adding connection: ' + error)
        }
      }))
    }
  }

  deleteDevice(deviceId: number): void {
    if (confirm('Are you sure you want to delete this device?')) {
      this.subscriptions.push(this.deviceService.deleteDevice(deviceId).subscribe({
        next: () => {
          alert('Device deleted succesfully.')
          this.fetchDevices();
          this.fetchConnectedPositions()
        },
        error: (error) => {
          alert('Error deleting devices :' + error)
        }
      }))
    }
  }

}
