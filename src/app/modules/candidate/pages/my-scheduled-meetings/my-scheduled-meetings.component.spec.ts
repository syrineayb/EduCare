import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MyScheduledMeetingsComponent } from './my-scheduled-meetings.component';

describe('MyScheduledMeetingsComponent', () => {
  let component: MyScheduledMeetingsComponent;
  let fixture: ComponentFixture<MyScheduledMeetingsComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [MyScheduledMeetingsComponent]
    });
    fixture = TestBed.createComponent(MyScheduledMeetingsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
