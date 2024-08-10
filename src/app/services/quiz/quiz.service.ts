import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";
import {environment} from "../../../environments/environment";
import {QuizResponse} from "../../models/quiz/QuizResponse";
import {QuizRequest} from "../../models/quiz/QuizRequest";
import {SubmitAnswerRequest} from "../../models/quiz/SubmitAnswerRequest";
import {userScore} from "../../models/quiz/userScore";
import {SubmitAnswerResponse} from "../../models/quiz/SubmitAnswerResponse";
import {QuizResultResponse} from "../../models/quiz/QuizResultResponse";

@Injectable({
  providedIn: 'root'
})
export class QuizService {

  baseUrl = environment.app.baseUrl;

  constructor(private http: HttpClient) {}

  createQuizForCourse(courseId: number, quiz: QuizRequest): Observable<QuizResponse> {
    return this.http.post<QuizResponse>(`${this.baseUrl}/api/quizzes/course/${courseId}`, quiz);
  }
  createQuizWithQuestions(courseId: number, quiz: QuizRequest): Observable<QuizResponse> {
    return this.http.post<QuizResponse>(`${this.baseUrl}/api/quizzes/course/${courseId}/add`, quiz);
  }

  getQuizById(quizId: number): Observable<QuizResponse> {
    return this.http.get<QuizResponse>(`${this.baseUrl}/api/quizzes/${quizId}`);
  }

  updateQuiz(quizId: number, quiz: QuizRequest): Observable<QuizResponse> {
    return this.http.put<QuizResponse>(`${this.baseUrl}/api/quizzes/${quizId}`, quiz);
  }

  deleteQuiz(quizId: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/api/quizzes/${quizId}`);
  }

  getAllQuizzes(): Observable<QuizResponse[]> {
    return this.http.get<QuizResponse[]>(`${this.baseUrl}/api/quizzes/getAll`);
  }
  getQuizzesByCourseId(courseId:number): Observable<QuizResponse[]> {
    return this.http.get<QuizResponse[]>(`${this.baseUrl}/api/quizzes/course/${courseId}`);
  }
  evaluateQuiz(quizId: number, answers: SubmitAnswerRequest[]): Observable<SubmitAnswerResponse> {
    return this.http.post<SubmitAnswerResponse>(`${this.baseUrl}/api/quizzes/${quizId}/evaluateQuiz`, answers);
  }
  getResultsByCandidateId(): Observable<QuizResultResponse[]> {
    return this.http.get<QuizResultResponse[]>(`${this.baseUrl}/api/quizzes/results`);
  }

}
