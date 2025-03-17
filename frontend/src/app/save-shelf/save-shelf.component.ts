import { Component, OnDestroy, OnInit } from '@angular/core';
import { Shelf } from '../models/shelf.model';
import { Subscription } from 'rxjs';
import { InventoryService } from '../inventory.service';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';

@Component({
  selector: 'app-save-shelf',
  imports: [ReactiveFormsModule],
  templateUrl: './save-shelf.component.html',
  styleUrl: './save-shelf.component.css'
})
export class SaveShelfComponent implements OnDestroy, OnInit {
  shelfForm: FormGroup;
  shelfExists = false;
  private subscriptions: Subscription[] = []

  constructor(private inventoryService: InventoryService, private fb: FormBuilder) {
    this.shelfForm = this.fb.group({
      id: ['', Validators.required],
      name: ['', Validators.required],
      shelfType: ['', Validators.required],
      associatedShelfPositions: ['', Validators.required]
    })
  }
  ngOnInit(): void {
    console.log(this.shelfForm);
  }

  onSubmit(): void {
    if (this.shelfForm.valid) {
      const shelf: Shelf = this.shelfForm.value;
      const id = shelf.id;
      this.subscriptions.push(this.inventoryService.getShelfById(id).subscribe({
        next: (existingShelf) => {
          this.shelfExists = true;
          if (confirm('Shelf ID already exists . Do you want to modify?')) {
            this.saveShelf(shelf, true);
          }
        },
        error: (error) => {
          this.shelfExists = false;
          this.saveShelf(shelf, false);
        }
      }))
    }
  }





  private saveShelf(shelf: Shelf, confirmModification: boolean): void {
    this.subscriptions.push(this.inventoryService.saveShelf(shelf, confirmModification).subscribe({
      next: (savedShelf) => {
        alert('Shelf saved succesfully.')
        this.shelfForm.reset();
      },
      error: (error) => {
        alert('Error saving shelf: ' + error)
      }
    }))
  }
  ngOnDestroy(): void {
    this
      .subscriptions.forEach(sub => sub.unsubscribe());
  }

}
