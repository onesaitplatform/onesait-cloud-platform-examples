import { TestBed } from '@angular/core/testing';
import { FileManagerService } from './file-manager.service';

describe('FileManagerService', () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  it('should be created', () => {
    const service: FileManagerService = TestBed.get(FileManagerService);
    expect(service).toBeTruthy();
  });
});
