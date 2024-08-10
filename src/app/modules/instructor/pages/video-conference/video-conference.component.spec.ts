import { ComponentFixture, TestBed } from '@angular/core/testing';

import { VideoConferenceComponent } from './video-conference.component';

describe('VideoConferenceComponent', () => {
  let component: VideoConferenceComponent;
  let fixture: ComponentFixture<VideoConferenceComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [VideoConferenceComponent]
    });
    fixture = TestBed.createComponent(VideoConferenceComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
