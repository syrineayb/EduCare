import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { CoursePageResponse } from "../../models/course/course-page-response";
import { CourseService } from '../../services/course/course.service';
import {CourseResponse} from "../../models/course/course-response";
import {ToastrService} from "ngx-toastr";
import {AuthenticationService} from "../../services/auth/authentication.service";

@Component({
  selector: 'app-courses-by-topic',
  templateUrl: './courses-by-topic.component.html',
  styleUrls: ['./courses-by-topic.component.css']
})
export class CoursesByTopicComponent implements OnInit {
  topicId: number = 0;
  courses: CourseResponse []=[];
  isAuthenticated: boolean = false;
  filteredCourses: CourseResponse[] = [];
  searchTerm: string = '';

  currentPage = 0;
  totalPages = 0;
  pageSize = 6; // Number of courses per page
  pages: number[] = [];
  enrolledUserCounts: { [key: number]: number } = {};

  constructor(
    private toastr: ToastrService, // Injectez ToastrService dans le constructeur
    private route: ActivatedRoute,
    private authService: AuthenticationService,
  private courseService: CourseService,
  ) { }

  ngOnInit(): void {
    this.fetchAllCourses();

    this.isAuthenticated = true; // Assuming authentication is successful

    this.route.params.subscribe(params => {
      this.topicId = +params['topicId'];
      this.fetchAllCourses();
    });
  }

  fetchAllCourses(): void {
    this.courseService.getCoursesByTopicPagable(this.topicId,this.currentPage, this.pageSize)
      .subscribe(
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
