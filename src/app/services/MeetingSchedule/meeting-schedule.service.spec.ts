import { TestBed } from '@angular/core/testing';

import { MeetingScheduleService } from './meeting-schedule.service';

describe('MeetingScheduleService', () => {
  let service: MeetingScheduleService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(MeetingScheduleService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
