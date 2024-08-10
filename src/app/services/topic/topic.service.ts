import { Injectable } from '@angular/core';
import {HttpClient, HttpEvent, HttpHeaders, HttpParams, HttpRequest} from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { TopicResponse } from '../../models/topic/topic-response';
import { TopicRequest } from '../../models/topic/topic-request';
import { TopicPageResponse } from '../../models/topic/topic-page-response';
import { HttpClientModule } from '@angular/common/http';
import {CoursePageResponse} from "../../models/course/course-page-response";

@Injectable({
  providedIn: 'root'
})
export class TopicService {
  baseUrl = environment.app.baseUrl;

  constructor(private http: HttpClient) {
  }


  findTopicsByTitle(keyword: string): Observable<TopicResponse[]> {
    const params = new HttpParams()
      .set('keyword', keyword)
     // .set('page', page.toString())
     // .set('size', size.toString());

    return this.http.get<TopicResponse[]>(`${this.baseUrl}/api/topics/search-by-title`, {params});
  }

  findTopicsByTitleContaining(keyword: string): Observable<TopicResponse[]> {
    const params = new HttpParams().set('keyword', keyword);
    return this.http.get<TopicResponse[]>(`${this.baseUrl}/api/topics/search-by-title`, { params });
  }

  findTopicsByTitleContainingPaginated(keyword: string, page: number, size: number): Observable<TopicPageResponse> {
    const params = new HttpParams()
      .set('keyword', keyword)
      .set('page', page.toString())
      .set('size', size.toString());

    return this.http.get<TopicPageResponse>(`${this.baseUrl}/api/topics/search`, { params });
  }


  getTopicsInBatch(page: number, size: number): Observable<any> {
    return this.http.get<any>(`${this.baseUrl}/api/topics?page=${page}&size=${size}`);
  }
  getLatestTopicsForCandidates(page: number = 0, size: number = 10): Observable<TopicPageResponse> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());

    return this.http.get<TopicPageResponse>(`${this.baseUrl}/api/topics/latest-topics`, { params });
  }


  getTopicById(topicId: number): Observable<TopicResponse> {
    return this.http.get<TopicResponse>(`${this.baseUrl}/api/topics/${topicId}`);
  }

  createTopic(formData: FormData) {
    const token = localStorage.getItem('accessToken');
    const headers = new HttpHeaders({
      Authorization: `Bearer ${token}`
    });

    // Send POST request to create topic
    return this.http.post<any>('http://localhost:8080/api/topics', formData);
  }

  updateTopic(topicId: number, formData: FormData): Observable<TopicResponse> {
    const token = localStorage.getItem('accessToken');
    const headers = new HttpHeaders({
      Authorization: `Bearer ${token}`
    });
    return this.http.put<TopicResponse>(`${this.baseUrl}/api/topics/${topicId}`, formData);
  }
  deleteTopic(topicId: number): Observable<string> {
    const token = localStorage.getItem('accessToken');
    const headers = new HttpHeaders({
      'Content-Type': 'application/json',
      Authorization: `Bearer ${token}`
    });
    return this.http.delete<string>(`${this.baseUrl}/api/topics/${topicId}`, { headers });
  }

  getAllTopicTitles(): Observable<string[]> {
    return this.http.get<string[]>(`${this.baseUrl}/api/topics/titles`);
  }
/*
  getAllTopics(): Observable<TopicResponse[]> {
    return this.http.get<TopicResponse[]>(`${this.baseUrl}/api/topics`);
  }


 */

  getAllTopicsWithIdAndTitle(): Observable<TopicResponse[]> {
    return this.http.get<TopicResponse[]>(`${this.baseUrl}/api/topics/all-with-id-and-title`);
  }
}
