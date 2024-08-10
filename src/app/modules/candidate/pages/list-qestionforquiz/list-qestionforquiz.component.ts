import { Component, OnInit, OnDestroy } from '@angular/core';
import { ToastrService } from 'ngx-toastr';
import { QuestionResponse } from '../../../../models/question/QuestionResponse';
import { ActivatedRoute, Router } from '@angular/router';
import { QuestionService } from '../../../../services/questions/question.service';
import { QuizService } from '../../../../services/quiz/quiz.service';
import { SubmitAnswerRequest } from '../../../../models/quiz/SubmitAnswerRequest';
import { userScore } from '../../../../models/quiz/userScore';
import { interval, Subscription } from 'rxjs';
import { SubmitAnswerResponse } from '../../../../models/quiz/SubmitAnswerResponse';

@Component({
  selector: 'app-list-qestionforquiz',
  templateUrl: './list-qestionforquiz.component.html',
  styleUrls: ['./list-qestionforquiz.component.css']
})
export class ListQestionforquizComponent implements OnInit, OnDestroy {
  currentQuizId: number | null = null;
  questions: QuestionResponse[] = [];
  quizId: number | null = null;
  courseId: number | null = null;
  userResponses: SubmitAnswerRequest[] = [];
  userScore: SubmitAnswerResponse | null = null;
  showCorrectAnswers: boolean = false;

  quiz: { duration: number; mark: number } | null = null;
  remainingTime: number | null = null;
  remainingTimeString: string = '';
  private timerSubscription: Subscription | null = null;

  constructor(
    private toastr: ToastrService,
    private route: ActivatedRoute,
    private router: Router,
    private questionService: QuestionService,
    private quizService: QuizService
  ) {}

  ngOnInit(): void {
    const quizIdParam = this.route.snapshot.paramMap.get('quizId');
    const courseIdParam = this.route.snapshot.paramMap.get('courseId');

    if (quizIdParam !== null) {
      const quizId = +quizIdParam;
      this.currentQuizId = quizId;
      this.quizId = quizId;
      this.fetchQuestions(quizId);
      this.fetchQuizDetails(quizId);
    } else {
      console.error('Quiz ID parameter is null.');
    }

    if (courseIdParam !== null) {
      this.courseId = +courseIdParam;
    } else {
      console.error('Course ID parameter is null.');
    }
  }
  toggleCorrectAnswers(): void {
    this.showCorrectAnswers = !this.showCorrectAnswers;
  }
  replayQuiz(): void {
    window.location.reload();
  }

  ngOnDestroy(): void {
    if (this.timerSubscription) {
      this.timerSubscription.unsubscribe();
    }
  }

  fetchQuizDetails(quizId: number): void {
    this.quizService.getQuizById(quizId).subscribe({
      next: (quiz) => {
        this.quiz = { duration: quiz.duration, mark: quiz.mark };
        this.remainingTime = this.quiz.duration * 60;
        this.startTimer();
      },
      error: (error) => {
        console.error('Error fetching quiz details:', error);
        this.showErrorMessage('Failed to fetch quiz details!');
      }
    });
  }

  startTimer(): void {
    if (this.quiz?.duration !== null && this.quiz?.duration !== undefined) {
      this.remainingTime = this.quiz.duration * 60;
      this.timerSubscription = interval(1000).subscribe(() => {
        if (this.remainingTime && this.remainingTime > 0) {
          this.remainingTime--;
          this.updateRemainingTime();
        } else {
          this.submitQuiz();
          this.timerSubscription?.unsubscribe();
        }
      });
    }
  }

  updateRemainingTime(): void {
    if (this.remainingTime !== null) {
      const minutes = Math.floor(this.remainingTime / 60);
      const seconds = this.remainingTime % 60;
      this.remainingTimeString = `${minutes}:${seconds < 10 ? '0' : ''}${seconds}`;
    }
  }

  submitQuiz(): void {
    if (this.quizId !== null) {
      if (this.questions.length === 0) {
        this.showErrorMessage('No questions found in the quiz.');
        return;
      }

      console.log('Submitting the following user responses:', this.userResponses);

      this.quizService.evaluateQuiz(this.quizId, this.userResponses).subscribe(
        response => {
          this.userScore = response;
          console.log('Quiz submitted successfully!', response);
          this.showSuccessMessage('Quiz submitted successfully!');
        },
        error => {
          console.error('Error submitting quiz:', error);
          this.showErrorMessage('Failed to submit quiz!');
        }
      );
    } else {
      console.error('Quiz ID is null.');
      this.showErrorMessage('Quiz ID is null!');
    }
  }

  fetchQuestions(quizId: number): void {
    this.questionService.getAllQuestionsForCandidate(quizId).subscribe({
      next: (response: QuestionResponse[]) => {
        this.questions = response;
        console.log('Questions fetched successfully!');
      },
      error: (error) => {
        console.error('Error fetching questions:', error);
        this.questions = [];
        this.showErrorMessage('Failed to fetch questions!');
      }
    });
  }

  handleSelection(questionId: number, selectedOptionId: number): void {
    const existingResponseIndex = this.userResponses.findIndex(response => response.questionId === questionId);

    if (existingResponseIndex !== -1) {
      const selectedOptionIndex = this.userResponses[existingResponseIndex].selectedOptionIds.indexOf(selectedOptionId);
      if (selectedOptionIndex !== -1) {
        this.userResponses[existingResponseIndex].selectedOptionIds.splice(selectedOptionIndex, 1);
      } else {
        this.userResponses[existingResponseIndex].selectedOptionIds.push(selectedOptionId);
      }
    } else {
      this.userResponses.push({ questionId, selectedOptionIds: [selectedOptionId] });
    }
  }

  seeCorrectAnswers(): void {
    this.showCorrectAnswers = true;
  }

  getQuestionText(questionId: number | undefined): string {
    const question = this.questions.find(q => q.id === questionId);
    return question ? question.text : '';
  }

  getOptionIds(questionId: number | undefined): number[] {
    const question = this.questions.find(q => q.id === questionId);
    return question ? question.options.map(option => option.id) : [];
  }

  getOptionText(optionId: number): string {
    for (const question of this.questions) {
      const option = question.options.find(o => o.id === optionId);
      if (option) {
        return option.text;
      }
    }
    return '';
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
