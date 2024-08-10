import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MemberFooterComponent } from './member-footer.component';

describe('FooterComponent', () => {
  let component: MemberFooterComponent;
  let fixture: ComponentFixture<MemberFooterComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [MemberFooterComponent]
    });
    fixture = TestBed.createComponent(MemberFooterComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
