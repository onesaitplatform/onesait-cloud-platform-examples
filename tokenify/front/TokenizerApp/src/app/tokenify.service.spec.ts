import { TestBed } from '@angular/core/testing';

import { TokenifyService } from './tokenify.service';

describe('TokenifyService', () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  it('should be created', () => {
    const service: TokenifyService = TestBed.get(TokenifyService);
    expect(service).toBeTruthy();
  });
});
