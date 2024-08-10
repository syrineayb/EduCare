import { TestBed } from '@angular/core/testing';
import { CanActivateFn } from '@angular/router';

import { candidateGuard } from './candidate.guard';

describe('candidateGuard', () => {
  const executeGuard: CanActivateFn = (...guardParameters) => 
      TestBed.runInInjectionContext(() => candidateGuard(...guardParameters));

  beforeEach(() => {
    TestBed.configureTestingModule({});
  });

  it('should be created', () => {
    expect(executeGuard).toBeTruthy();
  });
});
