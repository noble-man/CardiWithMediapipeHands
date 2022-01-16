import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ProductionDataComponent } from './production-data.component';

describe('ProductionDataComponent', () => {
  let component: ProductionDataComponent;
  let fixture: ComponentFixture<ProductionDataComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ProductionDataComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ProductionDataComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
