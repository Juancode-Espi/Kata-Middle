import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ReleaseRequest } from '../models/release.model';

@Injectable({ providedIn: 'root' })
export class ReleaseService {
  private readonly apiUrl = 'http://localhost:8080/api/releases';
  private readonly integrationUrl = 'http://localhost:8080/api/integration';

  constructor(private http: HttpClient) {}

  analyzeStack(repo: string): Observable<any> {
    return this.http.get<any>(`${this.integrationUrl}/deepwiki/analyze/${repo}`);
  }

  create(release: ReleaseRequest): Observable<ReleaseRequest> {
    return this.http.post<ReleaseRequest>(this.apiUrl, release);
  }

  findAll(): Observable<ReleaseRequest[]> {
    return this.http.get<ReleaseRequest[]>(this.apiUrl);
  }
}
