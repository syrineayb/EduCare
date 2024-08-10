import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MeetingScheduleManagementComponent } from './meeting-schedule-management.component';

describe('MeetingScheduleManagementComponent', () => {
  let component: MeetingScheduleManagementComponent;
  let fixture: ComponentFixture<MeetingScheduleManagementComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [MeetingScheduleManagementComponent]
    });
    fixture = TestBed.createComponent(MeetingScheduleManagementComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
