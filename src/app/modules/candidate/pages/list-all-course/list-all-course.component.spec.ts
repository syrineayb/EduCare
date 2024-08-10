import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ListAllCourseComponent } from './list-all-course.component';

describe('ListAllCourseComponent', () => {
  let component: ListAllCourseComponent;
  let fixture: ComponentFixture<ListAllCourseComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ListAllCourseComponent]
    });
    fixture = TestBed.createComponent(ListAllCourseComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
