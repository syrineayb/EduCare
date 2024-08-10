import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CreateMeetingScheduleComponent } from './create-meeting-schedule.component';

describe('ScheduleMeetingComponent', () => {
  let component: CreateMeetingScheduleComponent;
  let fixture: ComponentFixture<CreateMeetingScheduleComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [CreateMeetingScheduleComponent]
    });
    fixture = TestBed.createComponent(CreateMeetingScheduleComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
