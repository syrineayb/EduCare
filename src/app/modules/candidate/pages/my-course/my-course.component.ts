import { Component, OnInit } from '@angular/core';
import { ToastrService } from 'ngx-toastr';
import { CourseResponse } from "../../../../models/course/course-response";
import { CourseService } from "../../../../services/course/course.service";
import { ActivatedRoute, Router } from "@angular/router";
import { QuizService } from "../../../../services/quiz/quiz.service";
import { QuizResponse } from "../../../../models/quiz/QuizResponse";
import { LessonService } from "../../../../services/lesson/lesson.service";

@Component({
  selector: 'app-my-course',
  templateUrl: './my-course.component.html',
  styleUrls: ['./my-course.component.css']
})
export class MyCourseComponent implements OnInit {
  courses: CourseResponse[] = [];
  progressValues = new Map<number, number>();

  constructor(
    private toastr: ToastrService,
    private courseService: CourseService,
    private router: Router,
    private route: ActivatedRoute,
    private quizService: QuizService,
    private lessonService: LessonService,
  ) { }

  ngOnInit(): void {
    this.fetchCourses();
  }

  fetchCourses(): void {
    this.courseService.getMyCourses().subscribe({
      next: (courses: CourseResponse[]) => {
        this.courses = courses;
        // Fetch progress for each course
        this.courses.forEach(course => this.fetchProgress(course.id));
      },
      error: (error: any) => {
        console.error('Error fetching courses:', error);
        this.showErrorMessage('Failed to fetch courses!');
      }
    });
  }

  fetchProgress(courseId: number): void {
    this.lessonService.getUserCourseProgress(courseId).subscribe({
      next: response => {
        this.progressValues.set(courseId, response.progress);
      },
      error: error => {
        console.error('Error fetching progress:', error);
      }
    });
  }

  redirectToLessons(courseId: number): void {
    this.router.navigate(['/candidate/my-course/lessons', courseId]);
  }

  SeeQuizzes(courseId: number): void {
    this.quizService.getQuizzesByCourseId(courseId).subscribe({
      next: (quizzes: QuizResponse[]) => {
        if (quizzes.length > 0) {
          const quizId = quizzes[0].id;
          this.router.navigate(['/candidate/listQuizzes', quizId]);
        } else {
          this.showErrorMessage('No quiz found for the course.');
        }
      },
      error: error => {
        console.error('Error fetching quiz for the course:', error);
        this.showErrorMessage('Failed to fetch quiz for the course.');
      }
    });
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

  unenrollFromCourse(courseId: number): void {
    this.courseService.unenrollFromCourse(courseId).subscribe(
      () => {
        const index = this.courses.findIndex(course => course.id === courseId);
        if (index !== -1) {
          this.courses.splice(index, 1);
          this.showSuccessMessage('You are successfully unenrolled from this course');
        }
        console.log('Unenrolled successfully');
      },
      error => {
        this.showErrorMessage('Failed to unenroll from this course');
        console.error('Error while unenrolling:', error);
      }
    );
  }
}
