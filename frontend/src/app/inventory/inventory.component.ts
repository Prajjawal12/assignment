import { NgIf } from '@angular/common';
import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms'
import { InventoryService } from '../inventory.service';
import { ToastrService } from 'ngx-toastr';

@Component({
  selector: 'app-inventory',
  imports: [NgIf, FormsModule],
  templateUrl: './inventory.component.html',
  styleUrl: './inventory.component.css',



})
export class InventoryComponent {
  showSuccess(response: string) {
    this.toastr.success(response);
  }
  showError(error: string) {
    this.toastr.error(error)
  }

  constructor(private inventoryService: InventoryService, private toastr: ToastrService) { }
  showForm = false;
  formType: 'saveShelf' | 'getShelf' | 'saveShelfPosition' | 'getShelfPosition' | 'addShelfToShelfPosition' | 'addShelfPositionToDevice' = 'saveShelf';
  deviceId: number = NaN;
  sheflv0Id: number = NaN;
  shelfPositionv0Id: number = NaN;
  shelfv0 = { id: NaN, name: '', shelfType: '', shelfPositionId: NaN }
  shelfPositionv0 = { id: NaN, name: '', deviceId: NaN }


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
    this.shelfv0 = { id: NaN, name: '', shelfType: '', shelfPositionId: NaN }
    this.shelfPositionv0 = { id: NaN, name: '', deviceId: NaN }
  }
  onSubmit() {
    if (this.formType === 'saveShelf') {
      this.inventoryService.saveShelf(this.shelfv0).subscribe(

        (response) => {
          console.log('Shelf saved: ', response)
          this.showSuccess(JSON.stringify(response))
          this.hideform()
        }, (error) => {
          console.error('Error saving device: ', error)
          this.showError(JSON.stringify(error))
        })
    } else if (this.formType === 'saveShelfPosition') {
      this.inventoryService.saveShelfPosition(this.shelfPositionv0).subscribe(
        (response) => {
          console.log('Saved Shelf Position ', response)
          this.showSuccess(JSON.stringify(response))
          this.hideform()

        }, (error) => {
          console.error('Error saving Shelf Position ',)
          this.showError(JSON.stringify(error))
        }
      )
    }
    else if (this.formType === 'getShelf') {
      this.inventoryService.getShelf(this.sheflv0Id).subscribe(
        (response) => {
          console.log('Shelf retrieved', response)
          this.showSuccess(JSON.stringify(response))
          this.hideform()

        },
        (error) => {
          console.log('Error fetching shelf ', error)
          this.showError(JSON.stringify(error));


        }
      )
    } else if (this.formType === 'getShelfPosition') {
      this.inventoryService.getShelfPosition(this.shelfPositionv0Id).subscribe(
        (response) => {
          console.log('ShelfPosition retrieved: ', response)
          this.showSuccess(JSON.stringify(response))
          this.hideform();
        }, (error) => {
          console.error('Error fetching Shelf Position', error)
          this.showError(JSON.stringify(error))
        }
      )
    } else if (this.formType === 'addShelfPositionToDevice') {
      this.inventoryService.addShelfPositionToDevice(this.deviceId, this.shelfPositionv0Id).subscribe(
        (response) => {
          const responseMessage: string = `Added ShelfPosition with shelfPositionId: ${this.shelfPositionv0Id} to deviceId: ${this.deviceId}`;
          console.log(responseMessage)
          this.showSuccess(responseMessage)
          this.hideform()
        }, (error) => {
          console.log('Error Adding sheld position to device', error)
          this.showError(JSON.stringify(error))
        }
      )
    } else if (this.formType === 'addShelfToShelfPosition') {
      this.inventoryService.addShelfToShelfPosition(this.sheflv0Id, this.shelfPositionv0Id).subscribe(
        (response) => {
          const responseMessage: string = `Added Shelf with shelfId : ${this.sheflv0Id} to Shelf Position with shelfPositionId : ${this.shelfPositionv0Id}`
          console.log(responseMessage)
          this.showSuccess(responseMessage)
          this.hideform()
        }, (error) => {
          console.log('Error adding shelf to shelf position', error);
          this.showError(JSON.stringify(error))

        }
      )
    }
  }
}





