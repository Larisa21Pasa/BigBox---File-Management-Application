import {ComponentFixture, TestBed} from '@angular/core/testing';

import {FsEntryComponent} from './fs-entry.component';

describe('FilesComponent', () => {
  let component: FsEntryComponent;
  let fixture: ComponentFixture<FsEntryComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [FsEntryComponent]
    });
    fixture = TestBed.createComponent(FsEntryComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
