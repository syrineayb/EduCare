import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TopicManagementComponent } from './topic-management.component';

describe('ListTopicComponent', () => {
  let component: TopicManagementComponent;
  let fixture: ComponentFixture<TopicManagementComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [TopicManagementComponent]
    });
    fixture = TestBed.createComponent(TopicManagementComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
