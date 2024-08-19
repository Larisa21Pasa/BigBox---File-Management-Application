import { TestBed } from '@angular/core/testing';

import { SidebarSizeService } from './sidebar-size.service';

describe('SidebarSizeService', () => {
  let service: SidebarSizeService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(SidebarSizeService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
