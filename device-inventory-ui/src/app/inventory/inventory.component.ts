import { Component } from '@angular/core';
import { InventoryService } from '../inventory.service';

@Component({
  selector: 'app-inventory',
  templateUrl: './inventory.component.html',
  styleUrls: ['./inventory.component.css'],
})
export class InventoryComponent {
  inventory: any;
  shelfId: number = 1;
  shelfPositionId: number = 1;

  constructor(private inventoryService: InventoryService) {}

  saveShelf(): void {
    const newShelf = {
      name: 'New Shelf',
      description: 'A newly added shelf',
    };

    this.inventoryService.saveShelf(newShelf).subscribe(
      (response) => {
        this.inventory = response;
        console.log('Shelf saved successfully:', response);
      },
      (error) => {
        console.error('Error saving shelf:', error);
      }
    );
  }

  getShelf(): void {
    this.inventoryService.getShelf(this.shelfId).subscribe(
      (response) => {
        this.inventory = response;
        console.log('Shelf fetched successfully:', response);
      },
      (error) => {
        console.error('Error fetching shelf:', error);
      }
    );
  }

  saveShelfPosition(): void {
    const newShelfPosition = {
      name: 'New Shelf Position',
      description: 'A newly added shelf position',
    };

    this.inventoryService.saveShelfPosition(newShelfPosition).subscribe(
      (response) => {
        this.inventory = response;
        console.log('Shelf position saved successfully:', response);
      },
      (error) => {
        console.error('Error saving shelf position:', error);
      }
    );
  }

  getShelfPosition(): void {
    this.inventoryService.getShelfPosition(this.shelfPositionId).subscribe(
      (response) => {
        this.inventory = response;
        console.log('Shelf position fetched successfully:', response);
      },
      (error) => {
        console.error('Error fetching shelf position:', error);
      }
    );
  }
}
