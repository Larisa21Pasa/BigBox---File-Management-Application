import {ComponentFixture, TestBed} from '@angular/core/testing';

import {DeletedFilesComponent} from './deleted-files.component';

describe('DeletedFilesComponent', () => {
  let component: DeletedFilesComponent;
  let fixture: ComponentFixture<DeletedFilesComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [DeletedFilesComponent]
    });
    fixture = TestBed.createComponent(DeletedFilesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
