import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ListQestionforquizComponent } from './list-qestionforquiz.component';

describe('ListQestionforquizComponent', () => {
  let component: ListQestionforquizComponent;
  let fixture: ComponentFixture<ListQestionforquizComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ListQestionforquizComponent]
    });
    fixture = TestBed.createComponent(ListQestionforquizComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
