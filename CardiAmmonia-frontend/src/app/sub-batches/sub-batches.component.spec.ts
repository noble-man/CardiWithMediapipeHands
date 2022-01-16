import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SubBatchesComponent } from './sub-batches.component';

describe('SubBatchesComponent', () => {
  let component: SubBatchesComponent;
  let fixture: ComponentFixture<SubBatchesComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ SubBatchesComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(SubBatchesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
