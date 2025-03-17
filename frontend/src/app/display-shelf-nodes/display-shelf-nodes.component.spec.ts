import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DisplayShelfNodesComponent } from './display-shelf-nodes.component';

describe('DisplayShelfNodesComponent', () => {
  let component: DisplayShelfNodesComponent;
  let fixture: ComponentFixture<DisplayShelfNodesComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DisplayShelfNodesComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(DisplayShelfNodesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
