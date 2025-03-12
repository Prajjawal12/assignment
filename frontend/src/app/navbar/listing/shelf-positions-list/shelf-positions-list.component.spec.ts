import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ShelfPositionsListComponent } from './shelf-positions-list.component';

describe('ShelfPositionsListComponent', () => {
  let component: ShelfPositionsListComponent;
  let fixture: ComponentFixture<ShelfPositionsListComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ShelfPositionsListComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ShelfPositionsListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
