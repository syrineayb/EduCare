import { Injectable } from '@angular/core';
import {environment} from "../../../environments/environment";
import {HttpClient, HttpHeaders, HttpParams} from "@angular/common/http";
import {Observable} from "rxjs";
import {PageResponse} from "../../models/resource/PageResponse";
import {ForumResponse} from "../../models/forum/forum-response";
import {ForumPageResponse} from "../../models/forum/forum-page-response";
import {DiscussionRequest} from "../../models/discussion/discussion-request";
import {DiscussionResponse} from "../../models/discussion/discussion-response";
import {MessageRequest} from "../../models/message/message-request";
import {MessageResponse} from "../../models/message/message-response";
import {AnswerResponse} from "../../models/answer/answer-response";
import {AnswerRequest} from "../../models/answer/answer-request";
import {DiscussionPageResponse} from "../../models/discussion-page-response";
import {MessagePageResponse} from "../../models/message/message-page-response";
import {ResourceResponse} from "../../models/resource/ResourceResponse";
import {ReplyResponse} from "../../models/reply/ReplyResponse";

@Injectable({
  providedIn: 'root'
})
export class ForumService {
  baseUrl = environment.app.baseUrl;

  constructor(private http: HttpClient) {}

  // Forum APIs
  getAllForums(page: number = 0, size: number = 10): Observable<ForumPageResponse> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());

    return this.http.get<ForumPageResponse>(`${this.baseUrl}/api/forums`, { params });
  }

  getForumByLessonId(lessonId: number): Observable<ForumResponse> {
    const url = `${this.baseUrl}/api/forums/lesson/${lessonId}`;
    return this.http.get<ForumResponse>(url);
  }

  // Discussion APIs

  getAllDiscussionsByForumId(forumId: number, page: number , size: number): Observable<MessagePageResponse> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());
    const url = `${this.baseUrl}/api/messages/forum/${forumId}`;
    return this.http.get<MessagePageResponse>(url, { params });
  }
  getAllDiscussionsByLessonId(lessonId: number, page: number = 0, size: number = 10): Observable<DiscussionPageResponse> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());
    const url = `${this.baseUrl}/api/messages/lesson/${lessonId}`;
    return this.http.get<DiscussionPageResponse>(url, { params });
  }

  getInstructorLessonDiscussions(page: number=0 , size: number=4): Observable<MessagePageResponse> {
      const params = new HttpParams()
          .set('page', page.toString())
          .set('size', size.toString());
    return this.http.get<MessagePageResponse>(`${this.baseUrl}/api/messages/instructor-lessons-messages`, { params });
  }
  searchDiscussionsBySubject(keyword: string, size: number, page: number, forumId: number) : Observable<MessagePageResponse> {
    const params = new HttpParams()
        .set('keyword', keyword)
        .set('size', size.toString())
        .set('page', page.toString());
    const url = `${this.baseUrl}/api/messages/search/${forumId}`;
    return this.http.get<MessagePageResponse>(url, { params });
  }
  searchDiscussionsByLesson(keyword: string): Observable<DiscussionResponse[]> {
    const params = new HttpParams().set('keyword', keyword);
    const url = `${this.baseUrl}/api/messages/lesson/search`;
    return this.http.get<DiscussionResponse[]>(url, { params });
  }

 /* updateDiscussion(discussionId: number, discussionRequest: DiscussionRequest): Observable<DiscussionResponse> {
    const url = `${this.baseUrl}/api/messages/${discussionId}/update`;
    return this.http.put<DiscussionResponse>(url, discussionRequest);
  }

  */
  deleteDiscussion(discussionId: number): Observable<void> {
    const url = `${this.baseUrl}/api/messages/${discussionId}/delete`;
    return this.http.delete<void>(url);
  }


    // Message APIs

  createMessage(formData: FormData, forumId: number): Observable<MessageResponse> {
    return this.http.post<MessageResponse>(`${this.baseUrl}/api/messages/forum/${forumId}/create`, formData);
  }
  updateMessage(id: number, formData: FormData): Observable<MessageResponse> {
    return this.http.put<MessageResponse>(`${this.baseUrl}/api/messages/${id}/update`, formData);
  }




  downloadFile(filename: string): Observable<Blob> {
    const url = `${this.baseUrl}/api/messages/downloadFile/${filename}`;
    return this.http.get(url, { responseType: 'blob' });
  }


    // Answer APIs


// Method to create an answer

  createAnswer(discussionId: number, answerRequest: AnswerRequest): Observable<AnswerResponse> {
    const token = localStorage.getItem('accessToken');
    const headers = new HttpHeaders({
      'Content-Type': 'application/json', // Add content type header
      Authorization: `Bearer ${token}`
    });
    const url = `${this.baseUrl}/api/answers/create/${discussionId}`; // Adjust the endpoint URL

    return this.http.post<AnswerResponse>(url, answerRequest, { headers });
  }
  getAnswerById(answerId: number): Observable<AnswerResponse> {
    const url = `${this.baseUrl}/api/answers/${answerId}`;
    return this.http.get<AnswerResponse>(url);
  }

  updateAnswer(answerId: number, answerRequest: AnswerRequest): Observable<AnswerResponse> {
    const url = `${this.baseUrl}/api/answers/update/${answerId}`;
    return this.http.put<AnswerResponse>(url, answerRequest);
  }

  deleteAnswer(answerId: number): Observable<void> {
    const url = `${this.baseUrl}/api/answers/delete/${answerId}`;
    return this.http.delete<void>(url);
  }

  getAnswersByDiscussion(discussionId: number): Observable<AnswerResponse[]> {
    const url = `${this.baseUrl}/api/answers/discussion/${discussionId}`;
    return this.http.get<AnswerResponse[]>(url);
  }

  getAnswersByUser(userId: number): Observable<AnswerResponse[]> {
    const url = `${this.baseUrl}/api/answers/user/${userId}`;
    return this.http.get<AnswerResponse[]>(url);
  }

  getAnswersCountByDiscussion(discussionId: number): Observable<number> {
    const url = `${this.baseUrl}/api/answers/discussion/${discussionId}/count`;
    return this.http.get<number>(url);
  }

  searchAnswers(keyword: string): Observable<AnswerResponse[]> {
    const params = new HttpParams().set('keyword', keyword);
    const url = `${this.baseUrl}/api/answers/search`;
    return this.http.get<AnswerResponse[]>(url, { params });
  }

  likeAnswer(answerId: number, userId: number): Observable<void> {
    const url = `${this.baseUrl}/api/answers/${answerId}/like`;
    return this.http.post<void>(url, { userId });
  }

  getAnswersCountByMessageId(messageId: number): Observable<number> {
    const url = `${this.baseUrl}/api/answers/message/${messageId}/count`;
    return this.http.get<number>(url);
  }



/* Reply  To Answers methods*/
createReplyToAnswer(parentAnswerId: number, answerRequest: AnswerRequest): Observable<ReplyResponse> {
  const token = localStorage.getItem('accessToken');
  const headers = new HttpHeaders({
    'Content-Type': 'application/json',
    Authorization: `Bearer ${token}`
  });
  const url = `${this.baseUrl}/api/answers/${parentAnswerId}/reply`; // Endpoint ajusté pour la création de la réponse à réponse

  return this.http.post<ReplyResponse>(url, answerRequest, { headers });
}

}
  /*
  getForumById(id: number): Observable<ForumResponse> {
    const url = `${this.apiUrl}/${id}`;
    return this.http.get<ForumResponse>(url);
  }

  createForum(lessonId: number, forumRequest: ForumRequest): Observable<ForumResponse> {
    const url = `${this.apiUrl}/${lessonId}`;
    return this.http.post<ForumResponse>(url, forumRequest);
  }
  */

