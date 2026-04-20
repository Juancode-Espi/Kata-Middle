import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { ReleaseService } from './release.service';
import { ReleaseRequest } from '../models/release.model';

describe('ReleaseService', () => {
  let service: ReleaseService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [ReleaseService]
    });
    service = TestBed.inject(ReleaseService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should analyze stack', () => {
    const dummyResponse = { stack: 'Java, Spring' };
    const repo = 'test-repo';

    service.analyzeStack(repo).subscribe(res => {
      expect(res).toEqual(dummyResponse);
    });

    const req = httpMock.expectOne(`http://localhost:8080/api/integration/deepwiki/analyze/${repo}`);
    expect(req.request.method).toBe('GET');
    req.flush(dummyResponse);
  });

  it('should create a release', () => {
    const dummyRelease: ReleaseRequest = { equipo: 'TeamA', tipo: 'rs', descripcion: 'Test' };

    service.create(dummyRelease).subscribe(res => {
      expect(res).toEqual(dummyRelease);
    });

    const req = httpMock.expectOne('http://localhost:8080/api/releases');
    expect(req.request.method).toBe('POST');
    req.flush(dummyRelease);
  });

  it('should find all releases', () => {
    const dummyReleases: ReleaseRequest[] = [{ equipo: 'TeamA', tipo: 'rs', descripcion: 'Test' }];

    service.findAll().subscribe(res => {
      expect(res.length).toBe(1);
      expect(res).toEqual(dummyReleases);
    });

    const req = httpMock.expectOne('http://localhost:8080/api/releases');
    expect(req.request.method).toBe('GET');
    req.flush(dummyReleases);
  });
});
