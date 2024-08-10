import { Injectable } from '@angular/core';
import {environment} from "../../../environments/environment";
import {HttpClient, HttpParams} from "@angular/common/http";
import {QuestionRequest} from "../../models/question/QuestionRequest";
import {Observable} from "rxjs";
import {QuestionResponse} from "../../models/question/QuestionResponse";
import {QuestionWrapper} from "../../models/question/QuestionWrapper";
import {Page} from "ngx-pagination";
import {QuestionPageResponse} from "../../models/question/QuestionPageResponse";

@Injectable({
  providedIn: 'root'
})
export class QuestionService {

  baseUrl = environment.app.baseUrl;

  constructor(private http: HttpClient) {}

  createQuestionForQuiz(quizId: number, request: QuestionRequest): Observable<any> {
    return this.http.post<any>(`${this.baseUrl}/api/questions/quiz/${quizId}`, request);
  }
  getAllQuestionsByCourseId(courseId: number): Observable<any> {
    return this.http.get<QuestionResponse[]>(`${this.baseUrl}/api/questions/course/${courseId}`);
  }

  updateQuestion(question: QuestionResponse): Observable<any> {
    const questionId = question.id;
    const options = question.options.map(option => ({ id: option.id, text: option.text, correct: option.correct }));
    const payload = { id: questionId, title: question.text, type: question.type, options: options };
    return this.http.put<any>(`${this.baseUrl}/api/questions/${questionId}`, payload);
  }

  deleteQuestion(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/api/questions/${id}`);
  }


  //for instractor
  getAllQuestionsByQuizId(quizId: number): Observable<any> {
    return this.http.get<QuestionResponse[]>(`${this.baseUrl}/api/questions/quiz/${quizId}`);
  }

  getAllQuestionsByQuizIdPagable(quizId: number, page: number, size: number): Observable<QuestionPageResponse>{
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());

    return this.http.get<QuestionPageResponse>(`${this.baseUrl}/api/questions/pagable/quiz/${quizId}`, { params: params });
  }

  //for candidate
  getQuestionsByQuizId(quizId: number): Observable<any> {
    return this.http.get<QuestionWrapper[]>(`${this.baseUrl}/api/questions/quiz/${quizId}`);
  }
  getAllQuestionsForCandidate(quizId: number): Observable<any> {
    return this.http.get<QuestionWrapper[]>(`${this.baseUrl}/api/questions/candidate/${quizId}`);
  }


  getCorrectAnswerByQuizId(quizId: number): Observable<any> {
    return this.http.get<any>(`${this.baseUrl}/api/questions/quiz/${quizId}/Correct`);
  }

  getScore(userResponseQuestions: any[]): Observable<number> {
    return this.http.post<number>(`${this.baseUrl}/api/questions/getScore`, userResponseQuestions);
  }
}
