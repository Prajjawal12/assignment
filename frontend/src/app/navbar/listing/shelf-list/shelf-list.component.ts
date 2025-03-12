import { Component } from '@angular/core';
import { ShelfV0 } from '../../../inventory/shelfv0.model';
import { InventoryService } from '../../../inventory.service';
import { NgFor, NgIf } from '@angular/common';

@Component({
  selector: 'app-shelf-list',
  imports: [NgIf, NgFor],
  templateUrl: './shelf-list.component.html',
  styleUrl: './shelf-list.component.css'
})
export class ShelfListComponent {
  shelves: ShelfV0[] = []
  columns: string[] = ['id', 'name', 'shelfType', 'shelfPositionId']

  constructor(private inventoryService: InventoryService) { }

  ngOnInit(): void {
    this.inventoryService.getAllShelfNodes().subscribe(
      (shelves) => {
        this.shelves = shelves
      },
      (error) => {
        console.error("Error fetching device", error);
      }
    )
  }
}
