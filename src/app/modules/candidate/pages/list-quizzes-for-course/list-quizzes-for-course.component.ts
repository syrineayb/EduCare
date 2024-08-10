import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { QuizResponse } from "../../../../models/quiz/QuizResponse";
import { QuizService } from "../../../../services/quiz/quiz.service";
import { ToastrService } from "ngx-toastr";
import { QuizResultResponse } from "../../../../models/quiz/QuizResultResponse";

@Component({
  selector: 'app-list-quizzes-for-course',
  templateUrl: './list-quizzes-for-course.component.html',
  styleUrls: ['./list-quizzes-for-course.component.css']
})
export class ListQuizzesForCourseComponent implements OnInit {
  quizzes: QuizResponse[] = [];
  courseId!: number;
  progressValues = new Map<number, number>();

  constructor(
    private route: ActivatedRoute,
    private toastr: ToastrService,
    private router: Router,
    private quizService: QuizService
  ) {}

  ngOnInit(): void {
    this.route.params.subscribe(params => {
      this.courseId = +params['courseId'];
      this.loadQuizzesForCourse(this.courseId);
    });
  }

  fetchProgress(quizId: number): void {
    this.quizService.getResultsByCandidateId().subscribe({
      next: results => {
        const progress = results.find(result => result.quizId === quizId)?.percentage || 0;
        this.progressValues.set(quizId, progress);
      },
      error: error => {
        console.error('Error fetching progress:', error);
      }
    });
  }

  loadQuizzesForCourse(courseId: number): void {
    this.quizService.getQuizzesByCourseId(courseId).subscribe(
      (quizzes: QuizResponse[]) => {
        this.quizzes = quizzes;
        this.quizzes.forEach(quiz => this.fetchProgress(quiz.id));
      },
      (error) => {
        console.error('Error loading quizzes:', error);
        this.showErrorMessage('Error loading quizzes');
      }
    );
  }

  startQuiz(quizId: number): void {
    this.router.navigate(['/candidate/list-questionof-quiz', quizId]);
  }

  private showSuccessMessage(message: string): void {
    this.toastr.success(message, 'Success', {
      positionClass: 'toast-center-center',
      toastClass: 'custom-toast-success',
    });
  }

  private showErrorMessage(message: string): void {
    this.toastr.error(message, 'Error', {
      positionClass: 'toast-center-center',
      toastClass: 'custom-toast-error',
    });
  }
}
