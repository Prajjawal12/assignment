import { NgIf } from '@angular/common';
import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms'
import { InventoryService } from '../inventory.service';
import { response } from 'express';

@Component({
  selector: 'app-inventory',
  imports: [NgIf, FormsModule],
  templateUrl: './inventory.component.html',
  styleUrl: './inventory.component.css'
})
export class InventoryComponent {
  onSubmit() {
    throw new Error('Method not implemented.');
  }


  constructor(private inventoryService: InventoryService) { }
  showForm = false;
  formType: 'saveShelf' | 'getShelf' | 'saveShelfPosition' | 'getShelfPosition' | 'addShelfToShelfPosition' | 'addShelfPositionToDevice' = 'saveShelf';
  deviceId: number = NaN;
  sheflv0Id: number = NaN;
  shelfPositionv0Id: number = NaN;
  shelfv0 = { id: NaN, name: '', shelfType: '' }
  shelfPositionv0 = { id: NaN, name: '' }


  showSaveShelfForm() {
    this.showForm = true;
    this.formType = 'saveShelf'
  }
  showGetShelfForm() {
    this.showForm = true;
    this.formType = 'getShelf'
  }
  showSaveShelfPositionForm() {
    this.showForm = true;
    this.formType = 'saveShelfPosition'
  }
  showGetShelfPositionForm() {
    this.showForm = true;
    this.formType = 'getShelfPosition'
  }
  showAddShelfToShelfPositionForm() {
    this.showForm = true;
    this.formType = 'addShelfToShelfPosition'
  }
  showAddShelfPositionToDevice() {
    this.showForm = true;
    this.formType = 'addShelfPositionToDevice'
  }
  hideform() {
    this.showForm = false;
    this.deviceId = NaN;
    this.sheflv0Id = NaN;
    this.shelfPositionv0Id = NaN;
    this.shelfv0 = { id: NaN, name: '', shelfType: '' }
    this.shelfPositionv0 = { id: NaN, name: '' }
  }



}


