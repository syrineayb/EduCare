import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SecurityProfileComponent } from './security-profile.component';

describe('SecurityProfileComponent', () => {
  let component: SecurityProfileComponent;
  let fixture: ComponentFixture<SecurityProfileComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [SecurityProfileComponent]
    });
    fixture = TestBed.createComponent(SecurityProfileComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
