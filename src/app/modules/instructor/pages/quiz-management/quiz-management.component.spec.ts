import { ComponentFixture, TestBed } from '@angular/core/testing';

import { QuizManagementComponent } from './quiz-management.component';

describe('QuizManagementComponent', () => {
  let component: QuizManagementComponent;
  let fixture: ComponentFixture<QuizManagementComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [QuizManagementComponent]
    });
    fixture = TestBed.createComponent(QuizManagementComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
