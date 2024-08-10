import { Injectable } from '@angular/core';
import {HttpClient, HttpHeaders, HttpParams} from '@angular/common/http';
import { Observable } from 'rxjs';
import { catchError } from 'rxjs/operators';

import { environment } from '../../../environments/environment';
import {TopicPageResponse} from "../../models/topic/topic-page-response";
import {InstructorPageResponse} from "../../models/instructor/instructor-page-response";

@Injectable({
  providedIn: 'root'
})
export class InstructorService {
  private baseUrl = environment.app.baseUrl;

  constructor(private http: HttpClient) {}
  findAllInstructors(page = 0, size = 3): Observable<InstructorPageResponse> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());

    // No need for authorization headers

    return this.http.get<InstructorPageResponse>(`${this.baseUrl}/api/profiles/instructors`, { params });
  }
}
