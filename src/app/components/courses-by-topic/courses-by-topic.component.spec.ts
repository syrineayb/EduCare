import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CoursesByTopicComponent } from './courses-by-topic.component';

describe('CoursesByTopicComponent', () => {
  let component: CoursesByTopicComponent;
  let fixture: ComponentFixture<CoursesByTopicComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [CoursesByTopicComponent]
    });
    fixture = TestBed.createComponent(CoursesByTopicComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
