import { Component, OnInit } from '@angular/core';
import { CourseResponse } from "../../../../models/course/course-response";
import { QuizResponse } from "../../../../models/quiz/QuizResponse";
import { QuizRequest } from "../../../../models/quiz/QuizRequest";
import { QuizService } from "../../../../services/quiz/quiz.service";
import { CourseService } from "../../../../services/course/course.service";
import { ToastrService } from "ngx-toastr";
import { Router } from "@angular/router";
import { CoursePageResponse } from "../../../../models/course/course-page-response";
import {QuestionService} from "../../../../services/questions/question.service";
import {QuestionResponse} from "../../../../models/question/QuestionResponse";

@Component({
  selector: 'app-quiz-management',
  templateUrl: './quiz-management.component.html',
  styleUrls: ['./quiz-management.component.css']
})
export class QuizManagementComponent implements OnInit {
  courses: CourseResponse[] = [];
  quizzes: QuizResponse[] = [];
  newQuiz: QuizRequest = { title: '', duration: 0, courseId: 0, mark: 0 ,questionIds:[]};
  currentPage = 0;
  totalPages = 0;
  pageSize = 4;
  pages: number[] = [];
  selectedCourseId: number | null = null;
  selectedQuiz: QuizResponse | null = null;
  searchTerm: string = '';
  filteredCourses: CourseResponse[] = [];
  questions: QuestionResponse[] = [];

  constructor(
    private quizService: QuizService,
    private courseService: CourseService,
    private toastr: ToastrService,
    private router: Router,
    private questionService: QuestionService

  ) {
  }

  ngOnInit(): void {
    this.loadCourses();
  }

  loadQuestionsByCourseId(selectedCourseId: number): void {
    if (selectedCourseId === null) {
      console.error('Please select a course.');
      return;
    }
    this.questionService.getAllQuestionsByCourseId(selectedCourseId).subscribe(
      (questions: QuestionResponse[]) => {
        this.questions = questions;
      },
      (error: any) => {
        console.error('Error loading questions by course ID:', error);
        // Handle error
      }
    );
  }

  loadCourses(): void {
    this.courseService.getInstructorCoursesPaginated(this.currentPage, this.pageSize)
      .subscribe(
        (response: CoursePageResponse) => {
          this.courses = response.content || [];
          this.totalPages = response.totalPages || 0;
          this.pages = Array.from({ length: this.totalPages }, (_, i) => i + 1); // Update pages array
          this.filteredCourses = this.courses; // Set filteredCourses to all courses initially
        },
        (error: any) => {
          console.error('Error loading courses:', error);
          this.showErrorMessage('Failed to fetch courses. Please try again later.');
        }
      );
  }

  applyFilters(): void {
    if (this.searchTerm.trim() === '') {
      // No search term, load all courses
      this.loadCourses();
    } else {
      // Apply search term filter
      this.searchCoursesByTitle(this.searchTerm);
    }
  }

  selectQuiz(quiz: QuizResponse): void {
    this.selectedQuiz = quiz;
  }

  searchCoursesByTitle(keyword: string): void {
    this.courseService.searchInstructorCoursesByTitleContaining(keyword).subscribe(
      (courses: CourseResponse[]) => {
        this.filteredCourses = courses;
      },
      (error: any) => {
        console.error('Error searching courses by title:', error);
        this.showErrorMessage('Failed to search courses by title. Please try again later.');
      }
    );
  }

  resetFilters(): void {
    this.searchTerm = '';
    this.loadCourses();
  }

  selectCourse(courseId: number): void {
    this.selectedCourseId = courseId;
    this.getQuizzesByCourseId();
    this.loadQuestionsByCourseId(courseId);
  }

  getQuizzesByCourseId(): void {
    if (this.selectedCourseId === null) {
      console.error('Please select a course.');
      return;
    }

    this.quizService.getQuizzesByCourseId(this.selectedCourseId).subscribe(
      quizzes => {
        this.quizzes = quizzes;
      },
      error => console.error('Error fetching quizzes:', error)
    );
  }

  createQuiz(): void {
    if (!this.newQuiz.title || !this.newQuiz.duration) {
      this.showErrorMessage('Please enter quiz title and duration.');
      console.error('Please enter quiz title and duration.');
      return;
    }

    if (this.selectedCourseId === null) {
      this.showErrorMessage('Please select a course');
      console.error('Please select a course.');
      return;
    }

    this.quizService.createQuizForCourse(this.selectedCourseId, this.newQuiz).subscribe(
      quizResponse => {
        this.showSuccessMessage('Quiz created successfully.');
        console.log('Quiz created successfully:', quizResponse);
        this.quizzes.push(quizResponse);
        this.newQuiz = { title: '', duration: 0, courseId: 0,mark:0 };

        // Redirect to ListQuestionofQuizComponent after creating the quiz
        this.router.navigate(['/instructor/quiz-questions', quizResponse.id]).then(() => {
          window.location.reload();
        });
      },
      error => {
        this.showErrorMessage('Failed to create quiz. Please try again later.');
        console.error('Error creating quiz:', error);
      }
    );
  }
  toggleQuestionSelection(id: number): void {
    const index = this.newQuiz.questionIds?.indexOf(id);
    if (index === -1) {
      // Question ID not found, add it to the array
      this.newQuiz.questionIds?.push(id);
    } else {
      // Question ID found, remove it from the array
      this.newQuiz.questionIds?.splice(id, index);
    }
  }

  createQuizWithQuestion(): void {
    if (!this.newQuiz.title || !this.newQuiz.duration|| !this.newQuiz.questionIds) {
      this.showErrorMessage('Please enter quiz title and duration and question.');
      console.error('Please enter quiz title and duration.');
      return;
    }

    if (this.selectedCourseId === null) {
      this.showErrorMessage('Please select a course');
      console.error('Please select a course.');
      return;
    }

    this.quizService.createQuizWithQuestions(this.selectedCourseId, this.newQuiz).subscribe(
      quizResponse => {

        this.showSuccessMessage('Quiz created successfully.');
        console.log('Quiz created successfully:', quizResponse);
        this.quizzes.push(quizResponse);
        this.newQuiz = { title: '', duration: 0, courseId: 0,mark:0,questionIds:[] };
      },
      error => {
        this.showErrorMessage('Failed to create quiz. Please try again later.');
        console.error('Error creating quiz:', error);
      }
    );
  }


  updateQuiz(quiz: QuizResponse): void {
    if (quiz) {
      const quizId = quiz.id;
      this.quizService.updateQuiz(quizId, quiz).subscribe(
        updatedQuiz => {
          const index = this.quizzes.findIndex(q => q.id === quizId);
          if (index !== -1) {
            this.quizzes[index] = updatedQuiz;
          }
          this.selectedQuiz = null;
          this.showSuccessMessage('Quiz updated successfully.');
        },
        error => {
          console.error('Error updating quiz:', error);
          this.showErrorMessage('Failed to update quiz. Please try again later.');
        }
      );
    }
  }

  deleteQuiz(): void {
    if (this.selectedQuiz) {
      const quizId = this.selectedQuiz.id;
      this.quizService.deleteQuiz(quizId).subscribe(
        () => {
          this.quizzes = this.quizzes.filter(q => q.id !== quizId);
          this.selectedQuiz = null;
          this.showSuccessMessage('Quiz deleted successfully.');
        },
        error => {
          this.showErrorMessage('Failed to delete quiz. Please try again later.');
          console.error('Error deleting quiz:', error);

        }
      );
    }
  }

  gopagequestion(quizId: number) {
    this.router.navigate(['/instructor/quiz-questions', quizId]).then(() => {
      window.location.reload();
    });


  }


  changePage(page: number, event: Event): void {
    event.preventDefault();
    this.currentPage = page;
    this.loadCourses();
  }

  nextPage(event: Event): void {
    event.preventDefault();
    if (this.currentPage < this.totalPages - 1) {
      this.currentPage++;
      this.loadCourses();
    }
  }

  prevPage(event: Event): void {
    event.preventDefault(); // Prevent default anchor tag behavior
    if (this.currentPage > 0) {
      this.currentPage--;
      this.loadCourses();
    }
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
  isSelectedQuestion(id: number): boolean {
    return <boolean>this.newQuiz.questionIds?.includes(id);
  }

}
