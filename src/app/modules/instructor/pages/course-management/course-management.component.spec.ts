import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CourseManagementComponent } from './course-management.component';

describe('CourseManagementComponent', () => {
  let component: CourseManagementComponent;
  let fixture: ComponentFixture<CourseManagementComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [CourseManagementComponent]
    });
    fixture = TestBed.createComponent(CourseManagementComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
