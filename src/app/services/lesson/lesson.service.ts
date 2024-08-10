import { Injectable } from '@angular/core';
import {HttpClient, HttpHeaders, HttpParams} from '@angular/common/http';
import { Observable } from 'rxjs';
import {LessonPageResponse} from "../../models/lesson/lesson-page-response";
import {LessonRequest} from "../../models/lesson/lesson-request";
import {LessonResponse} from "../../models/lesson/lesson-response";
import {environment} from "../../../environments/environment";
import {UserLessonProgressResponse} from "../../models/progress/UserLessonProgressResponse";
import {UserCourseProgressResponse} from "../../models/progress/UserCourseProgressResponse";

@Injectable({
  providedIn: 'root'
})
export class LessonService {
  baseUrl = environment.app.baseUrl;

  constructor(private http: HttpClient) { }
  getUserCourseProgress(courseId: number): Observable<UserCourseProgressResponse>{
    return this.http.get<UserCourseProgressResponse>(`${this.baseUrl}/api/lessons/course/${courseId}/progress`);
  }
  countLessonsByCourseId(courseId: number): Observable<number> {
    return this.http.get<number>(`${this.baseUrl}/api/lessons/lessons/count/${courseId}`);
  }
  createLesson(courseId: number, lessonRequest: LessonRequest): Observable<LessonResponse> {
    const token = localStorage.getItem('accessToken');
    const headers = new HttpHeaders({
      'Content-Type': 'application/json',
      Authorization: `Bearer ${token}`
    });
    return this.http.post<LessonResponse>(`${this.baseUrl}/api/lessons/${courseId}`, lessonRequest, { headers });
  }

  updateLesson(lessonId: number, lessonRequest: LessonRequest): Observable<LessonResponse> {
    return this.http.put<LessonResponse>(`${this.baseUrl}/api/lessons/${lessonId}`, lessonRequest);
  }

  deleteLesson(lessonId: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/api/lessons/${lessonId}`);
  }

  getLessonById(lessonId: number): Observable<LessonResponse> {
    return this.http.get<LessonResponse>(`${this.baseUrl}/api/lessons/${lessonId}`);
  }

  /*getAllLessonsByCourse(courseId: number, page: number, size: number): Observable<LessonPageResponse> { // Use LessonPageResponse
    return this.http.get<LessonPageResponse>(`${this.baseUrl}/api/lessons/course/${courseId}?page=${page}&size=${size}`);
  }

   */

  //2


  getAllLessonsByCourse(courseId: number): Observable<LessonResponse[]> {
    return this.http.get<LessonResponse[]>(`${this.baseUrl}/api/lessons/course/${courseId}`);
  }

 getAllLessonsByCourses(courseId: number, page: number = 1, size: number=4 ): Observable<LessonPageResponse> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());

    return this.http.get<LessonPageResponse>(`${this.baseUrl}/api/lessons/course/${courseId}/paginated`, { params });
  }


  getAllLessonsByCoursee(courseId: number): Observable<LessonResponse[]> {
    return this.http.get<LessonResponse[]>(`${this.baseUrl}/api/lessons/course/${courseId}`);
  }
  findLessonByTitleContaining(keyword: string): Observable<LessonResponse[]> {
    const params = new HttpParams()
      .set('keyword', keyword);

    return this.http.get<LessonResponse[]>(`${this.baseUrl}/api/lessons/search`, { params });
  }

  getAllLessons(): Observable<LessonResponse[]> {
    return this.http.get<LessonResponse[]>(`${this.baseUrl}/api/lessons`);
  }


  uploadImageFile(formData: FormData): Observable<void> {
    const token = localStorage.getItem('accessToken');
    const headers = new HttpHeaders({
      Authorization: `Bearer ${token}`
    });
    return this.http.post<void>(`${this.baseUrl}/api/lesson-files/uploadImage`, formData, { headers });
  }


  uploadVideoFile(formData: FormData): Observable<void> {
    const token = localStorage.getItem('accessToken');
    const headers = new HttpHeaders({
      Authorization: `Bearer ${token}`
    });

    return this.http.post<void>(`${this.baseUrl}/api/lesson-files/uploadVideo`, formData, { headers });
  }

  uploadPdfFile(formData: FormData): Observable<void> {
    const token = localStorage.getItem('accessToken');
    const headers = new HttpHeaders({
      Authorization: `Bearer ${token}`
    });

    return this.http.post<void>(`${this.baseUrl}/api/lesson-files/uploadPdf`, formData, { headers });
  }

}
