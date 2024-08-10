import { Injectable } from '@angular/core';
import {environment} from "../../../environments/environment";
import {HttpClient, HttpParams} from "@angular/common/http";
import {Observable} from "rxjs";
import {ResourceRequest} from "../../models/resource/ResourceRequest";
import {ResourceResponse} from "../../models/resource/ResourceResponse";
import {UserLessonProgressResponse} from "../../models/progress/UserLessonProgressResponse";
import {PageResponse} from "../../models/resource/PageResponse";
import {CountResourceResponse} from "../../models/resource/CountResourceResponse";

@Injectable({
  providedIn: 'root'
})
export class ResourceService {
  baseUrl = environment.app.baseUrl;

  constructor(private http: HttpClient) {}
  countVideosByLessonId(lessonId: number): Observable<CountResourceResponse> {
    return this.http.get<CountResourceResponse>(`${this.baseUrl}/api/resourcess/count/videos/${lessonId}`);
  }
  countImagesByLessonId(lessonId: number): Observable<CountResourceResponse> {
    return this.http.get<CountResourceResponse>(`${this.baseUrl}/api/resourcess/count/images/${lessonId}`);
  }
  countPDFsByLessonId(lessonId: number): Observable<CountResourceResponse> {
    return this.http.get<CountResourceResponse>(`${this.baseUrl}/api/resourcess/count/pdfs/${lessonId}`);
  }




  getResourcesByLessonId(lessonId: number, page: number, size: number): Observable<PageResponse >{
    const params = new HttpParams()
      .set('lessonId', lessonId.toString())
      .set('page', page.toString())
      .set('size', size.toString());

    return this.http.get<PageResponse >(`${this.baseUrl}/api/resourcess/lesson/resources`, { params });
  }

  findAllResources(page: number, size: number): Observable<PageResponse> {
    return this.http.get<PageResponse>(`${this.baseUrl}/api/resources/findAll?page=${page}&amp;size=${size}`);
  }
  getResourceProgress(lessonId: number): Observable<UserLessonProgressResponse>{
    return this.http.get<UserLessonProgressResponse>(`${this.baseUrl}/api/resourcess/lesson/${lessonId}/progress`);
  }

  markResourceAsCompleted(resourceId: number): Observable<void> {
    return this.http.put<void>(`${this.baseUrl}/api/resourcess/markAsCompleted?resourceId=${resourceId}`, {});
  }

  getResourceById(resourceId: number): Observable<ResourceResponse> {
    return this.http.get<ResourceResponse>(`${this.baseUrl}/api/resourcess/${resourceId}`);
  }

  createResource(formData: FormData, lessonId: number): Observable<ResourceResponse> {
    return this.http.post<ResourceResponse>(`${this.baseUrl}/api/resourcess/create/${lessonId}`, formData);
  }

  updateResource(resourceId: number, formData:FormData): Observable<ResourceResponse> {
    return this.http.put<ResourceResponse>(`${this.baseUrl}/api/resourcess/update/${resourceId}`, formData);
  }

  deleteResource(resourceId: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/api/resourcess/delete/${resourceId}`);
  }

  getAllResourcesByLessonId(lessonId: number): Observable<ResourceResponse[]> {
    return this.http.get<ResourceResponse[]>(`${this.baseUrl}/api/resourcess/lesson/${lessonId}`);
  }


}
