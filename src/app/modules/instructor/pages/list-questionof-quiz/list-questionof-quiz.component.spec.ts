import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ListQuestionofQuizComponent } from './list-questionof-quiz.component';

describe('ListQuestionofQuizComponent', () => {
  let component: ListQuestionofQuizComponent;
  let fixture: ComponentFixture<ListQuestionofQuizComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ListQuestionofQuizComponent]
    });
    fixture = TestBed.createComponent(ListQuestionofQuizComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
