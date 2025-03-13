import { Component, OnInit } from '@angular/core';
import { ShelfV0 } from '../../inventory/shelfv0.model';
import { InventoryService } from '../../inventory.service';
import { NgFor, NgIf } from '@angular/common';

@Component({
  selector: 'app-shelf-list',
  imports: [NgIf, NgFor],
  templateUrl: './shelf-list.component.html',
  styleUrl: './shelf-list.component.css'
})
export class ShelfListComponent implements OnInit {
  shelves: ShelfV0[] = [];
  columns: string[] = ['id', 'name', 'shelfType']

  constructor(private inventoryService: InventoryService) {
    console.log('Columns', this.columns);
  }

  ngOnInit(): void {
    console.log('fetching shelves');

    this.inventoryService.getAllShelfNodes().subscribe(
      (shelves: ShelfV0[]) => {
        console.log('shelves received', shelves);
        this.shelves = shelves

        console.log("data", this.shelves)

      },
      (error) => {
        console.error('Error fetching shelf nodes', error)
      }

    )
  }
}
