import { Component, OnInit } from '@angular/core';
import { CourseResponse } from '../../../../models/course/course-response';
import { CourseService } from '../../../../services/course/course.service';
import { ToastrService } from 'ngx-toastr';
import {Router} from "@angular/router";
import {AuthenticationService} from "../../../../services/auth/authentication.service";
import {CoursePageResponse} from "../../../../models/course/course-page-response";

@Component({
  selector: 'app-list-all-course',
  templateUrl: './list-all-course.component.html',
  styleUrls: ['./list-all-course.component.css']
})
export class ListAllCourseComponent implements OnInit {
  courses: CourseResponse[] = [];
  coursesPage: CoursePageResponse = {};

  filteredCourses: CourseResponse[] = [];
  searchTerm: string = '';

  currentPage = 0;
  totalPages = 0;
  pageSize = 6; // Number of courses per page
  pages: number[] = [];
  isAuthenticated: boolean = false; // Flag to track user's authentication status

  enrolledUserCounts: { [key: number]: number } = {};

  enrolledCandidatesCounts: number =0; // Map course IDs to enrolled counts


  constructor(
    private toastr: ToastrService, // Injectez ToastrService dans le constructeur
    private courseService: CourseService,
    private router: Router,
    private authService: AuthenticationService

  ) {}

  ngOnInit(): void {
    this.fetchAllCourses();
  }

  fetchEnrolledUsersCount(courseId: number): void {
    this.courseService.countUsersEnrolledInCourse(courseId).subscribe(
        (count) => {
          this.enrolledCandidatesCounts= count;

          console.log(this.enrolledCandidatesCounts);
        },
        (error) => {
          console.error(`Error fetching enrolled users count for course ${courseId}:`, error);
        }
    );
  }
  fetchAllCourses(): void {
    this.courseService.findAllCourses(this.currentPage, this.pageSize).subscribe(
        (response: any) => {

          this.courses = response.content || [];
          this.totalPages = response.totalPages || 0;
          this.pages = Array.from({ length: this.totalPages }, (_, i) => i + 1);
          this.filteredCourses = this.courses; // Initially, display all courses

          this.courses?.forEach((course) => {
            this.courseService.countUsersEnrolledInCourse(course.id).subscribe(
              (count) => {
                this.enrolledUserCounts[course.id] = count;
              },
              (error) => {
                console.error('Error fetching enrolled user count:', error);
              }
            );
          });


          this.courses.forEach((course) => this.fetchEnrolledUsersCount(course.id));

        },
        (error: any) => {
          console.error('Error loading courses:', error);
          // Handle error loading courses
        }
      );
  }

  clearSearch(): void {
    this.searchTerm = '';
  }
  applyFilters(): void {
    if (this.searchTerm.trim() === '') {
      // No search term, show all courses
      this.filteredCourses = this.courses;
    } else {
      // Apply search term filter
      this.filteredCourses = this.courses.filter(course =>
        course.title.toLowerCase().includes(this.searchTerm.toLowerCase())
      );
    }
  }
  enrollUser(courseId: number): void {
    // Fetch the authenticated user ID
    const userIdString = this.authService.getUserId();
    const userId = userIdString ? +userIdString : 0; // Convert to number or use 0 as a default value

    this.courseService.isUserEnrolledInCourse(userId, courseId)
      .subscribe(
        (enrolled) => {
          if (!enrolled) {
            // Proceed with enrollment if not already enrolled
            this.courseService.enrollInCourse(userId, courseId)
              .subscribe(
                (response) => {
                  console.log(`Enrolled user ${userId} in course ${courseId}`);
                  this.showSuccessMessage('Enrolled successfully!');
                  //this.fetchEnrolledUsersCount(courseId);
                  this.fetchAllCourses();
                  // Handle success, if needed
                },
                (error) => {
                  console.error('Failed to enroll user:', error);
                  this.showErrorMessage('Failed to enroll user!');
                  // Handle error, if needed
                }
              );
          } else {
            // Display error message indicating user is already enrolled
            console.error('you are already enrolled in this course');
            this.showErrorMessage('You are already enrolled in this course!');
            // Show error message to the user
          }
        },
        (error) => {
          console.error('Error checking enrollment status:', error);
          this.showErrorMessage('Error checking enrollment status!');
          // Handle error, if needed
        }
      );
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

  changePage(page: number, event: Event): void {
    event.preventDefault(); // Prevent default anchor tag behavior
    this.currentPage = page;
    this.fetchAllCourses();
  }

  nextPage(event: Event): void {
    event.preventDefault(); // Prevent default anchor tag behavior
    if (this.currentPage < this.totalPages - 1) {
      this.currentPage++;
      this.fetchAllCourses();
    }
  }

  prevPage(event: Event): void {
    event.preventDefault(); // Prevent default anchor tag behavior
    if (this.currentPage > 0) {
      this.currentPage--;
      this.fetchAllCourses();
    }
  }
}
