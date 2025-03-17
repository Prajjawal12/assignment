import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SaveShelfComponent } from './save-shelf.component';

describe('SaveShelfComponent', () => {
  let component: SaveShelfComponent;
  let fixture: ComponentFixture<SaveShelfComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SaveShelfComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SaveShelfComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
