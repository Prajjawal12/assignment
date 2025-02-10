import { Component } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-home',
  imports: [],
  templateUrl: './home.component.html',
  styleUrl: './home.component.css',
})
export class HomeComponent {
  constructor(private router: Router) {}

  navigateToDevice(): void {
    this.router.navigate(['/device']);
  }

  navigateToInventory(): void {
    this.router.navigate(['/inventory']);
  }
}
