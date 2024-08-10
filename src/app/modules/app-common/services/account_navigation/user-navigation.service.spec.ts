import { TestBed } from '@angular/core/testing';

import { UserNavigationService } from './user-navigation.service';

describe('UserNavigationService', () => {
  let service: UserNavigationService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(UserNavigationService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
