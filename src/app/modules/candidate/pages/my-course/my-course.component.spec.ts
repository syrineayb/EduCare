import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MyCourseComponent } from './my-course.component';

describe('MyCourseComponent', () => {
  let component: MyCourseComponent;
  let fixture: ComponentFixture<MyCourseComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [MyCourseComponent]
    });
    fixture = TestBed.createComponent(MyCourseComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
