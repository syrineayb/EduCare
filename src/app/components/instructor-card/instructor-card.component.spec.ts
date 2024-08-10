import { ComponentFixture, TestBed } from '@angular/core/testing';

import { InstructorCardComponent } from './instructor-card.component';

describe('InstructorListComponent', () => {
  let component: InstructorCardComponent;
  let fixture: ComponentFixture<InstructorCardComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [InstructorCardComponent]
    });
    fixture = TestBed.createComponent(InstructorCardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
