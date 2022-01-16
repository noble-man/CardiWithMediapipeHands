import { TestBed } from '@angular/core/testing';

import { PointAndClickService } from './point-and-click.service';

describe('PointAndClickService', () => {
  let service: PointAndClickService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(PointAndClickService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
