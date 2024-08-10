import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AddLessonComponent } from './add-lesson.component';

describe('AddLessonComponent', () => {
  let component: AddLessonComponent;
  let fixture: ComponentFixture<AddLessonComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [AddLessonComponent]
    });
    fixture = TestBed.createComponent(AddLessonComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
