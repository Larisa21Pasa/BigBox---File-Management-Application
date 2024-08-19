import { ComponentFixture, TestBed } from '@angular/core/testing';

import { WelcomeTopbarComponent } from './welcome-topbar.component';

describe('WelcomeTopbarComponent', () => {
  let component: WelcomeTopbarComponent;
  let fixture: ComponentFixture<WelcomeTopbarComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [WelcomeTopbarComponent]
    });
    fixture = TestBed.createComponent(WelcomeTopbarComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
