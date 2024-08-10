import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CandidateProfileComponent } from './profile.component';

describe('CandidateProfileComponent', () => {
  let component: CandidateProfileComponent;
  let fixture: ComponentFixture<CandidateProfileComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [CandidateProfileComponent]
    });
    fixture = TestBed.createComponent(CandidateProfileComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
