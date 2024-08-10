import { Injectable } from '@angular/core';
import { environment } from "../../../environments/environment";
import { HttpClient, HttpHeaders } from "@angular/common/http";
import { MeetingScheduleRequest } from "../../models/MeetingSchedule/MeetingSchedule-request";
import { Observable } from "rxjs";
import { MeetingScheduleResponse } from "../../models/MeetingSchedule/MeetingSchedule-response";
import { CourseService } from "../course/course.service";
import { LessonService } from "../lesson/lesson.service";
import { CourseResponse } from "../../models/course/course-response";
import { LessonResponse } from "../../models/lesson/lesson-response";
import { MeetingSchedulePageResponse } from "../../models/MeetingSchedule/MeetingSchedulePageResponse";

@Injectable({
  providedIn: 'root'
})
export class MeetingScheduleService {
  baseUrl = environment.app.baseUrl;

  constructor(
    private http: HttpClient,
    private courseService: CourseService,
    private lessonService: LessonService
  ) { }

  private getAuthHeaders(): HttpHeaders {
    const token = localStorage.getItem('accessToken');
    return new HttpHeaders({
      'Content-Type': 'application/json',
      Authorization: `Bearer ${token}`
    });
  }

  createMeetingSchedule(meetingScheduleRequest: MeetingScheduleRequest): Observable<MeetingScheduleResponse> {
    const headers = this.getAuthHeaders();
    return this.http.post<MeetingScheduleResponse>(`${this.baseUrl}/api/meeting-schedules`, meetingScheduleRequest, { headers });
  }

  getAllMeetingSchedules(): Observable<MeetingScheduleResponse[]> {
    return this.http.get<MeetingScheduleResponse[]>(`${this.baseUrl}/api/meeting-schedules`);
  }
  getAuthenticatedInstructorMeetingSchedules(): Observable<MeetingScheduleResponse[]> {
    const headers = this.getAuthHeaders();
    return this.http.get<MeetingScheduleResponse[]>(`${this.baseUrl}/api/meeting-schedules/instructor`, { headers });
  }
  getMeetingScheduleById(id: number): Observable<MeetingScheduleResponse> {
    const headers = this.getAuthHeaders();
    return this.http.get<MeetingScheduleResponse>(`${this.baseUrl}/api/meeting-schedules/${id}`, { headers });
  }

  updateMeetingSchedule(id: number, meetingScheduleRequest: MeetingScheduleRequest): Observable<MeetingScheduleResponse> {
    const headers = this.getAuthHeaders();
    return this.http.put<MeetingScheduleResponse>(`${this.baseUrl}/api/meeting-schedules/${id}`, meetingScheduleRequest, { headers });
  }

  deleteMeetingSchedule(id: number): Observable<void> {
    const headers = this.getAuthHeaders();
    return this.http.delete<void>(`${this.baseUrl}/api/meeting-schedules/${id}`, { headers });
  }

  getInstructorCourses(): Observable<CourseResponse[]> {
    return this.courseService.getInstructorCourses();
  }

  getAllLessonsByCourse(courseId: number): Observable<LessonResponse[]> {
    return this.lessonService.getAllLessonsByCoursee(courseId);
  }
  getAuthenticatedInstructorMeetingSchedulesPaginated(page: number, size: number): Observable<MeetingSchedulePageResponse> {
    const headers = this.getAuthHeaders();
    return this.http.get<MeetingSchedulePageResponse>(`${this.baseUrl}/api/meeting-schedules/instructor-paginated?page=${page}&size=${size}`, { headers });
  }
  searchMeetingSchedules(searchTerm: string, searchDate: string, page: number, size: number): Observable<MeetingSchedulePageResponse> {
    const headers = this.getAuthHeaders();
    let url = `${this.baseUrl}/api/meeting-schedules/search?`;
    if (searchTerm.trim() !== '') {
      url += `searchTerm=${searchTerm}&`;
    }
    if (searchDate.trim() !== '') {
      url += `searchDate=${searchDate}&`;
    }
    url += `page=${page}&size=${size}`;
    return this.http.get<MeetingSchedulePageResponse>(url, { headers });
  }
  getAuthenticatedCandidateMeetingSchedules(page: number, size: number): Observable<MeetingSchedulePageResponse> {
    const headers = this.getAuthHeaders();
    return this.http.get<MeetingSchedulePageResponse>(`${this.baseUrl}/api/meeting-schedules/candidate?page=${page}&size=${size}`, { headers });
  }
}
