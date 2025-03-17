import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SaveDeviceComponent } from './save-device.component';

describe('SaveDeviceComponent', () => {
  let component: SaveDeviceComponent;
  let fixture: ComponentFixture<SaveDeviceComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SaveDeviceComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SaveDeviceComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
