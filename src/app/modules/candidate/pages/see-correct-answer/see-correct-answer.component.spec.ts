import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SeeCorrectAnswerComponent } from './see-correct-answer.component';

describe('SeeCorrectAnswerComponent', () => {
  let component: SeeCorrectAnswerComponent;
  let fixture: ComponentFixture<SeeCorrectAnswerComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [SeeCorrectAnswerComponent]
    });
    fixture = TestBed.createComponent(SeeCorrectAnswerComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
