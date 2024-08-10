import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ListQuizzesForCourseComponent } from './list-quizzes-for-course.component';

describe('ListQuizzesForCourseComponent', () => {
  let component: ListQuizzesForCourseComponent;
  let fixture: ComponentFixture<ListQuizzesForCourseComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ListQuizzesForCourseComponent]
    });
    fixture = TestBed.createComponent(ListQuizzesForCourseComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
