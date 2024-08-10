import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { CourseResponse } from '../../models/course/course-response';
import { CourseRequest } from '../../models/course/course-request';
import { CoursePageResponse } from '../../models/course/course-page-response';
import { environment } from "../../../environments/environment";
import { TopicPageResponse } from "../../models/topic/topic-page-response";
import {CandidateCourseResponse} from "../../models/course/CandidateCourseResponse";

@Injectable({
  providedIn: 'root'
})
export class CourseService {
  baseUrl = environment.app.baseUrl;

  constructor(private http: HttpClient) {}

/*  enrollInCourse(userId: number, courseId: number): Observable<any> {

    const body = { userId, courseId }; // Create the request body

    return this.http.post<any>(`${this.baseUrl}/api/courses/courses/enroll`, body);
  }

 */
  isUserEnrolledInCourse(userId: number, courseId: number): Observable<boolean> {
    const url = `${this.baseUrl}/api/courses/${courseId}/users/${userId}/enrollment`;
    return this.http.get<boolean>(url);
  }
  enrollInCourse(userId: number, courseId: number): Observable<CandidateCourseResponse> {
    const url = `${this.baseUrl}/api/courses/courses/enroll?userId=${userId}&courseId=${courseId}`;
    return this.http.post<CandidateCourseResponse>(url, null);
  }
  unenrollFromCourse(courseId: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/api/courses/${courseId}/unenroll`);
  }


  getMyCourses(): Observable<CourseResponse[]> {
    return this.http.get<CourseResponse[]>(`${this.baseUrl}/api/courses/my-courses`);
  }
  findAllCourses(page = 0, size = 3): Observable<CoursePageResponse> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());

    return this.http.get<CoursePageResponse>(`${this.baseUrl}/api/courses/findAll`, { params });
  }

  // Retrieve all courses
  getAllCourses(): Observable<CourseResponse[]> {
    return this.http.get<CourseResponse[]>(`${this.baseUrl}/api/courses`);
  }

  getLatestCoursesForCandidates(page: number = 0, size: number = 10): Observable<CoursePageResponse> {
    const params = new HttpParams()
        .set('page', page.toString())
        .set('size', size.toString());

    return this.http.get<CoursePageResponse>(`${this.baseUrl}/api/courses/latest-courses`, { params });
  }


  // Retrieve instructor courses
  //1
  getInstructorCourses(): Observable<CourseResponse[]> {
    return this.http.get<CourseResponse[]>(`${this.baseUrl}/api/courses/instructor-courses`);
  }

  // Retrieve paginated instructor courses


  getInstructorCoursesPaginated(page: number = 0, size: number = 4): Observable<CoursePageResponse> {
    const params = new HttpParams()
        .set('page', page.toString())
        .set('size', size.toString());

    return this.http.get<CoursePageResponse>(`${this.baseUrl}/api/courses/instructor-courses/paginated`, { params });
  }

  // Retrieve a course by ID
  findById(courseId: number): Observable<CourseResponse> {
    return this.http.get<CourseResponse>(`${this.baseUrl}/api/courses/${courseId}`);
  }

  // Retrieve a course by ID
  createCourse(formData: FormData) {
        const token = localStorage.getItem('accessToken');
        const headers = new HttpHeaders({
            Authorization: `Bearer ${token}`
        });
        // Send POST request to create topic
        return this.http.post<any>('http://localhost:8080/api/courses', formData);
    }
  // Update an existing course


  updateCourse(courseId: number, formData: FormData): Observable<any> {
    const token = localStorage.getItem('accessToken');
    const headers = new HttpHeaders({
      Authorization: `Bearer ${token}`
    });
    return this.http.put(`${this.baseUrl}/api/courses/${courseId}`, formData, { headers });
  }


  // Delete a course by ID
  deleteCourse(courseId: number): Observable<void> {
    const token = localStorage.getItem('accessToken');
    const headers = new HttpHeaders({
      Authorization: `Bearer ${token}`
    });
    return this.http.delete<void>(`${this.baseUrl}/api/courses/${courseId}`, { headers });
  }

  // Search all courses by title containing a keyword with pagination
  searchAllCoursesByTitleContaining(keyword: string, page: number, size: number): Observable<CoursePageResponse> {
    const params = new HttpParams()
      .set('keyword', keyword)
      .set('page', page.toString())
      .set('size', size.toString());

    return this.http.get<CoursePageResponse>(`${this.baseUrl}/api/courses/search`, { params });
  }
  findCoursesInBatch(page: number, size: number): Observable<any> {
      return this.http.get<any>(`${this.baseUrl}/api/courses/in-batch?page=${page}&size=${size}`);
  }
  // Search instructor courses by title containing a keyword
  searchInstructorCoursesByTitleContaining(keyword: string): Observable<CourseResponse[]> {
    const params = new HttpParams()
        .set('keyword', keyword);

    return this.http.get<CourseResponse[]>(`${this.baseUrl}/api/courses/instructor-courses/search`, { params });
  }

  // Retrieve courses by topic with pagination


  getCoursesByTopic(topicId: number): Observable<CourseResponse[]> {

    return this.http.get<CourseResponse[]>(`${this.baseUrl}/api/courses/by-topic/${topicId}`);
  }
  getCoursesByTopicPagable(topicId: number,page :number, size :number): Observable<CoursePageResponse> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());

    return this.http.get<CoursePageResponse>(`${this.baseUrl}/api/courses/pagable/topic/${topicId}`, { params });
  }

  countUsersEnrolledInCourse(courseId: number): Observable<number> {
    return this.http.get<number>(`${this.baseUrl}/api/courses/${courseId}/enrolled-users/count`);
  }
  getCountOfInstructorCourses(): Observable<number> {
    return this.http.get<number>(`${this.baseUrl}/api/courses/instructor-courses/count`);
  }
  // Method to get the count of enrolled candidates for a specific course taught by the authenticated instructor
  getCountOfEnrolledCandidatesForCourse(courseId: number): Observable<number> {
    return this.http.get<number>(`${this.baseUrl}/api/courses/${courseId}/enrolled-candidates/count`);
  }

  // Method to get the count of all enrolled candidates for all courses taught by the authenticated instructor
  getCountOfAllEnrolledCandidatesForAllCourses(): Observable<number> {
    return this.http.get<number>(`${this.baseUrl}/api/courses/enrolled-candidates/count`);
  }

  // Method to fetch course title by ID
  getCourseTitle(courseId: number): Observable<string> {
    const token = localStorage.getItem('accessToken');
    const headers = new HttpHeaders({
      Authorization: `Bearer ${token}`
    });
    return this.http.get(`${this.baseUrl}/api/courses/${courseId}/title`, { headers, responseType: 'text' });
  }


}
