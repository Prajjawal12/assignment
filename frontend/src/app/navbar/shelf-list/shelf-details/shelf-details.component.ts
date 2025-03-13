import { Component } from '@angular/core';
import { ShelfDetail } from './shelf-details.model';
import { ActivatedRoute } from '@angular/router';
import { InventoryService } from '../../../inventory.service';
import { NgFor, NgIf } from '@angular/common';

@Component({
  selector: 'app-shelf-details',
  imports: [NgIf, NgFor],
  templateUrl: './shelf-details.component.html',
  styleUrl: './shelf-details.component.css'
})
export class ShelfDetailsComponent {
  shelfId: number = 0;
  shelfDetails: ShelfDetail | undefined;

  constructor(private route: ActivatedRoute, private inventoryService: InventoryService) { }

  ngOnInit(): void {
    this.route.params.subscribe(params => {
      this.shelfId = +params['id']
      this.fetchShelfDetails();

    })


  }
  fetchShelfDetails(): void {
    this.inventoryService.getShelfDetails(this.shelfId).subscribe(
      (data) => {
        console.log('data received from backend', data);
        this.shelfDetails = data;
        console.log("Shelf details fetched", this.shelfDetails);
      },
      (error) => {
        console.error("Error fetching shelf details", error);
      }
    )
  }
}
