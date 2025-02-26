import { Component } from '@angular/core';
import { ShelfPositionV0 } from '../../../inventory/shelfPositionv0.model';
import { NgFor, NgIf } from '@angular/common';
import { InventoryService } from '../../../inventory.service';

@Component({
  selector: 'app-shelf-positions-list',
  imports: [NgIf, NgFor],
  templateUrl: './shelf-positions-list.component.html',
  styleUrl: './shelf-positions-list.component.css'
})
export class ShelfPositionsListComponent {
  shelfPositions: ShelfPositionV0[] = []
  columns: string[] = ['id', 'name', 'deviceId']
  constructor(private inventoryService: InventoryService) { }
  ngOnInit(): void {
    this.inventoryService.getAllShelfPositionNodes().subscribe(
      (shelfPositions) => {
        this.shelfPositions = shelfPositions
      },
      (error) => {
        console.error('Error fetching all shelf position nodes', error)
      }
    )
  }
}
