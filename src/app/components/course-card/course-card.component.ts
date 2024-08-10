import {Component, OnInit} from "@angular/core";
import {CoursePageResponse} from "../../models/course/course-page-response";
import {CourseService} from "../../services/course/course.service";
import {Router} from "@angular/router";
import {AuthenticationService} from "../../services/auth/authentication.service";


@Component({
  selector: 'app-course-list',
  templateUrl: './course-card.component.html',
  styleUrls: ['./course-card.component.css']
})
export class CourseCardComponent implements OnInit {
  courses: CoursePageResponse = {};
  stars = Array(5).fill(0);
  isAuthenticated: boolean = false; // Flag to track user's authentication status

  enrolledUserCounts: { [key: number]: number } = {};
  constructor(
      private courseService: CourseService,
      private router: Router,
      private authService: AuthenticationService,
  ) {} // Inject the CourseService

  ngOnInit(): void {
    this.isAuthenticated = this.authService.isAuthenticated();

    this.fetchCourses();

  }


  fetchCourses(): void {
    this.courseService.findAllCourses().subscribe({
      next: (response: CoursePageResponse) => {
        this.courses = response;
        // Fetch the enrolled user count for each course
        this.courses?.content?.forEach((course) => {
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
      error: (error) => {
        console.error('Error fetching courses:', error);
      }
    });
  }

  redirectToAllCourses() {
    console.log(this.isAuthenticated);
    this.router.navigate(['/all-courses-list']); // Replace '/all-courses' with the actual route to your all courses page


  }

  redirectToLoginPage() {
    console.log(this.isAuthenticated);

    this.router.navigate(['/login']); // Replace '/all-courses' with the actual route to your all courses page

  }
}
