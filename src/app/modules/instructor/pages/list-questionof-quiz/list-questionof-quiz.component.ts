import { Component, OnInit } from '@angular/core';
import { QuestionService } from "../../../../services/questions/question.service";
import { ToastrService } from "ngx-toastr";
import { ActivatedRoute, Router } from "@angular/router";
import { QuestionResponse } from "../../../../models/question/QuestionResponse";
import { QuestionRequest } from "../../../../models/question/QuestionRequest";
import { OptionRequest } from "../../../../models/option/optionRequest";
import {Page} from "ngx-pagination";
import {QuestionPageResponse} from "../../../../models/question/QuestionPageResponse";

@Component({
  selector: 'app-list-questionof-quiz',
  templateUrl: './list-questionof-quiz.component.html',
  styleUrls: ['./list-questionof-quiz.component.css']
})
export class ListQuestionofQuizComponent implements OnInit {
  questions: QuestionResponse[] = [];
  currentQuizId!: number;
  currentPage = 0;
  totalPages = 0;
  pageSize = 4; // Number of courses per page
  pages: number[] = []; // Define pages array
  newQuestionText: QuestionRequest = {
    title: '',
    type: '',
    options: []
  };

  newQuestionOptions: OptionRequest[] = [];

  constructor(
    private toastr: ToastrService,
    private router: Router,
    private route: ActivatedRoute,
    private questionService: QuestionService
  ) {
    const quizIdParam = this.route.snapshot.paramMap.get('quizId');
    if (quizIdParam !== null) {
      const quizId = +quizIdParam;
      this.currentQuizId = quizId;
      this.loadQuestions(quizId);
    } else {
      console.error('Quiz ID parameter is null.');
    }
  }

  ngOnInit(): void {
    this.loadQuestions(this.currentQuizId);
  }
  loadQuestions(quizId: number): void {
    this.questionService.getAllQuestionsByQuizIdPagable(quizId, this.currentPage, this.pageSize).subscribe(
      (page: QuestionPageResponse) => {
        this.questions = page.content || []; // Extract content property
        this.totalPages = page.totalPages || 0;
        this.pages = Array.from({ length: this.totalPages }, (_, i) => i + 1); // Update pages array

        console.log(this.questions); // Log the content of the page
      },
      (error) => {
        console.error('Error loading questions:', error);
      }
    );
  }


  addOption(): void {
    this.newQuestionOptions.push({ id: 0, text: '', correct: false });
  }

  addOptionToQuestion(question: QuestionResponse): void {
    question.options.push({ id: 0, text: '', correct: false });
  }

  removeOption(question: QuestionResponse, index: number): void {
    question.options.splice(index, 1);
  }

  validateOptions(options: OptionRequest[], type: string): boolean {
    if (type === 'YES_NO' && options.length !== 2) {
      this.showErrorMessage('Yes/No questions must have exactly 2 options.');
      return false;
    }
    if (type === 'MULTIPLE_CHOICE' && options.length < 3) {
      this.showErrorMessage('Multiple Choice questions must have at least 3 options.');
      return false;
    }
    if (type === 'SINGLE_CHOICE' && (options.length < 3 || options.filter(opt => opt.correct).length < 1)) {
      this.showErrorMessage('SINGLE_CHOICE questions must have at least 3 options and at least one correct option.');
      return false;
    }
    if (type === 'MULTIPLE_CHOICE' && options.filter(opt => opt.correct).length !== 2) {
      this.showErrorMessage('Multiple Choice questions must have exactly 2 correct options.');
      return false;
    }
    if (type === 'YES_NO' && options.filter(opt => opt.correct).length !== 1) {
      this.showErrorMessage('Yes/No questions must have exactly 1 correct option.');
      return false;
    }
    return true;
  }

  addQuestion(): void {
    if (!this.newQuestionText.title || !this.newQuestionText.type) {
      this.showErrorMessage('Please enter all details');
      return;
    }

    if (!this.validateOptions(this.newQuestionOptions, this.newQuestionText.type)) {
      return;
    }

    if (this.currentQuizId !== null) {
      this.newQuestionText.options = this.newQuestionOptions;
      console.log('Data sent to backend:', this.newQuestionText);

      this.questionService.createQuestionForQuiz(this.currentQuizId, this.newQuestionText).subscribe(
        () => {
          console.log('Data sent to backended:', this.newQuestionText);

          this.loadQuestions(this.currentQuizId);
          this.clearInputFields();
          this.showSuccessMessage('Question added successfully');
        },
        (error) => {
          console.error('Error adding question:', error);
          this.showErrorMessage('Failed to add question. Please try again later.');
        }
      );
    } else {
      console.error('Quiz ID is null.');
      this.showErrorMessage('Quiz ID is null.');
    }
  }

  clearInputFields(): void {
    this.newQuestionText = {
      title: '',
      type: '',
      options: []
    };
    this.newQuestionOptions = [];
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

  goToQuizList(): void {
    this.router.navigate(['/instructor/quiz-management']);
  }

  updateQuestion(question: QuestionResponse): void {
    if (!this.validateOptions(question.options, question.type)) {
      return;
    }

    if (question && this.currentQuizId !== null) {
      this.questionService.updateQuestion(question).subscribe(
        () => {
          this.showSuccessMessage('Question updated successfully');
          this.loadQuestions(this.currentQuizId);
        },
        (error) => {
          console.error('Error updating question:', error);
          this.showErrorMessage('Failed to update question. Please try again later.');
        }
      );
    }
  }

  deleteQuestion(questionId: number): void {
    if (confirm('Are you sure you want to delete this question?')) {
      this.questionService.deleteQuestion(questionId).subscribe(
        () => {
          this.questions = this.questions.filter(q => q.id !== questionId);
          this.showSuccessMessage('Question deleted successfully');
        },
        (error) => {
          console.error('Error deleting question:', error);
          this.showErrorMessage('Failed to delete question. This question is already used by candidates who have played the quiz.');
        }
      );
    }
  }


  removeOptions(optionsArray: OptionRequest[], index: number): void {
    optionsArray.splice(index, 1);
  }

  onQuestionTypeChange(question?: QuestionResponse): void {
    if (question) {
      if (question.type === 'YES_NO') {
        while (question.options.length > 2) {
          question.options.pop();
        }
      }
    } else {
      if (this.newQuestionText.type === 'YES_NO') {
        while (this.newQuestionOptions.length > 2) {
          this.newQuestionOptions.pop();
        }
      }
    }
  }

  changePage(page: number, event: Event): void {
    event.preventDefault(); // Prevent default anchor tag behavior
    this.currentPage = page;
    this.loadQuestions(this.currentQuizId);
  }

  nextPage(event: Event): void {
    event.preventDefault(); // Prevent default anchor tag behavior
    if (this.currentPage < this.totalPages - 1) {
      this.currentPage++;
      this.loadQuestions(this.currentQuizId);
    }
  }
  prevPage(event: Event): void {
    event.preventDefault(); // Prevent default anchor tag behavior
    if (this.currentPage > 0) {
      this.currentPage--;
      this.loadQuestions(this.currentQuizId);
    }
  }
}
