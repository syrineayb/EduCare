import {Component, ElementRef, OnInit} from '@angular/core';
import { ToastrService } from 'ngx-toastr';
import { CoursePageResponse } from '../../../../models/course/course-page-response';
import { CourseService } from '../../../../services/course/course.service';
import { Router } from '@angular/router';
import { AuthenticationService } from '../../../../services/auth/authentication.service';

@Component({
  selector: 'app-course-list',
  templateUrl: './course-list.component.html',
  styleUrls: ['./course-list.component.css']
})
export class CourseListComponent implements OnInit {
  courses: CoursePageResponse = {};
  stars = Array(5).fill(0);
  currentPage = 0;
  totalPages = 0;
  isAuthenticated: boolean = false;
  enrolledCandidatesCount: number = 0;
  scrolledRight = false; // Variable to indicate if user has scrolled to the right
  scrolledLeft = false;
  constructor(
      private toastr: ToastrService,
      private courseService: CourseService,
      private router: Router,
      private authService: AuthenticationService,
      private elRef: ElementRef
  ) {}

  ngOnInit(): void {
    this.isAuthenticated = this.authService.isAuthenticated();
    this.fetchCourses();
  }

  enrollUser(courseId: number): void {
    const userIdString = this.authService.getUserId();
    const userId = userIdString ? +userIdString : 0;

    this.courseService.isUserEnrolledInCourse(userId, courseId)
      .subscribe(
        (enrolled) => {
          if (!enrolled) {
            this.courseService.enrollInCourse(userId, courseId)
              .subscribe(
                (response) => {
                  console.log(`Enrolled user ${userId} in course ${courseId}`);
                  this.showSuccessMessage('Enrolled successfully!');
                  this.fetchCourses(); // Call fetchCourses() after successful enrollment
                },
                (error) => {
                  console.error('Failed to enroll user:', error);
                  this.showErrorMessage('Failed to enroll user!');
                }
              );
          } else {
            console.error('User is already enrolled in the course');
            this.showErrorMessage('You are already enrolled in this course!');
          }
        },
        (error) => {
          console.error('Error checking enrollment status:', error);
          this.showErrorMessage('Error checking enrollment status!');
        }
      );
  }

  fetchCourses(): void {
    this.courseService.getLatestCoursesForCandidates(this.currentPage, 3)
      .subscribe({
        next: (response: CoursePageResponse) => {
          this.courses = response;
          this.totalPages = response.totalPages || 0;
          this.scrolledRight = this.currentPage < this.totalPages - 1;
          this.courses.content?.forEach(course => this.fetchEnrolledUsersCount(course?.id));
        },
        error: (error) => {
          console.error('Error fetching courses:', error);
          this.showErrorMessage('Failed to fetch courses!');
        }
      });
  }

  fetchEnrolledUsersCount(courseId?: number): void {
    if (courseId) {
      this.courseService.countUsersEnrolledInCourse(courseId)
          .subscribe(
              (count) => {
                const course = this.courses.content?.find(c => c?.id === courseId);
                if (course) {
                  this.enrolledCandidatesCount = count;
                }
              },
              (error) => {
                console.error(`Error fetching enrolled users count for course ${courseId}:`, error);
              }
          );
    }
  }
  scrollRight(): void {
    if (this.currentPage < this.totalPages - 1) {
      this.currentPage++;
      this.fetchCourses();
      const container = this.elRef.nativeElement.querySelector('.topic-container');
      if (container) {
        container.scrollLeft += 300; // Adjust scroll amount as needed
      }
      this.scrolledLeft = true; // Set scrolledLeft to true when scrolling to the right
    }
  }

  scrollLeft(): void {
    if (this.currentPage > 0) {
      this.currentPage--;
      this.fetchCourses();
      const container = this.elRef.nativeElement.querySelector('.course-container');
      if (container) {
        container.scrollLeft -= 300; // Adjust scroll amount as needed
      }

      // Toggle the visibility of the left scroll button
      if (this.currentPage === 0) {
        this.scrolledLeft = false; // Hide the left scroll button if on the first page
      } else {
        this.scrolledLeft = true; // Show the left scroll button if not on the first page
      }
    }
  }
  redirectToAllCourses() {
    console.log(this.isAuthenticated);
    this.router.navigate(['/all-courses-list']);
  }

  redirectToLoginPage() {
    console.log(this.isAuthenticated);
    this.router.navigate(['/login']);
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
