import { Component, OnInit } from '@angular/core';
import { AuthenticationService } from '../../../../services/auth/authentication.service';
import { CourseService } from '../../../../services/course/course.service';
import { Chart } from 'angular-highcharts';
import {CourseEnrollmentService} from "../../../../services/CourseEnrollment/course-enrollment.service";
import {CourseEnrollmentResponse} from "../../../../models/CourseEnrollmentResponse/CourseEnrollmentResponse";

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent implements OnInit {
  countnbrCourse = 0;
  countnbrStudents = 0;
  username: string | null = '';
  courseChart: Chart = new Chart({ /* chart configuration */ });
  enrolledCandidatesCount=0;
  courseId=0;
  enrollment:CourseEnrollmentResponse [] = [];
  searchTerm: string = '';
  filteredEnrollments: any[]=[] // Array to store filtered enrollments
  currentPage: number = 0; // Current page number
  pageSize: number = 4; // Number of items per page
  totalPages: number = 0; // Total number of pages
  pages: number[] = [];

  constructor(
    private courseService: CourseService,
    private authService: AuthenticationService,
    private  courseEnrollmentService:CourseEnrollmentService,

  ) { }

  ngOnInit(): void {
    this.username = this.authService.getUsername();
    this.getCountOfInstructorCourses();
    this.filteredEnrollments = this.enrollment;

    this.getCountOfAllEnrolledCandidatesForAllCourses();
    this.getEnrolledCandidatesPerCourse();
    this.getAllCourseEnrollments(); // Call the method here
    this.totalPages = Math.ceil(this.enrollment.length / this.pageSize);
    this.pages = Array(this.totalPages).fill(0).map((x, i) => i + 1);
  }

  changePage(page: number, event: MouseEvent): void {
    event.preventDefault();
    this.currentPage = page;
    this.updateFilteredEnrollments();
  }

  prevPage(event: MouseEvent): void {
    event.preventDefault();
    if (this.currentPage > 0) {
      this.currentPage--;
      this.updateFilteredEnrollments();
    }
  }

  nextPage(event: MouseEvent): void {
    event.preventDefault();
    if (this.currentPage < this.totalPages - 1) {
      this.currentPage++;
      this.updateFilteredEnrollments();
    }
  }

  updateFilteredEnrollments(): void {
    const startIndex = this.currentPage * this.pageSize;
    this.filteredEnrollments = this.enrollment.slice(startIndex, startIndex + this.pageSize);
  }



  onPageChange(page: number): void {
    // Calculate start index and end index of the current page
    const startIndex = (page - 1) * this.pageSize;
    const endIndex = startIndex + this.pageSize;

    // Update filteredEnrollments with enrollments for the current page
    this.filteredEnrollments = this.enrollment.slice(startIndex, endIndex);

    // Update current page
    this.currentPage = page;
  }
  getPages(): number[] {
    const pageCount = Math.ceil(this.enrollment.length / this.pageSize);
    return Array.from({ length: pageCount }, (_, i) => i + 1);
  }

  search() {
    // Filter enrollments by course name and student name based on the combined search term
    this.filteredEnrollments = this.enrollment.filter(enrollment =>
      (enrollment.courseName && enrollment.courseName.toLowerCase().includes(this.searchTerm.toLowerCase())) ||
      (enrollment.candidateName && enrollment.candidateName.toLowerCase().includes(this.searchTerm.toLowerCase())) ||
      (enrollment.candidateLastname && enrollment.candidateLastname.toLowerCase().includes(this.searchTerm.toLowerCase()))
    );
  }


  getCountOfInstructorCourses(): void {
    this.courseService.getCountOfInstructorCourses().subscribe(
      count => this.countnbrCourse = count,
      error => console.log(error)
    );
  }

  getCountOfAllEnrolledCandidatesForAllCourses(): void {
    this.courseService.getCountOfAllEnrolledCandidatesForAllCourses().subscribe(
      count => this.countnbrStudents = count,
      error => console.log(error)
    );
  }
  getCountOfEnrolledCandidatesForCourse(): void {
    this.courseService.getCountOfEnrolledCandidatesForCourse(this.courseId).subscribe(
      count => this.countnbrStudents = count,
      error => console.log(error)
    );
  }
  getAllCourseEnrollments(): void {
    this.courseEnrollmentService.getAllCourseEnrollments().subscribe(
      enrollments => {
        this.enrollment = enrollments;
        this.totalPages = Math.ceil(enrollments.length / this.pageSize);
        this.updateFilteredEnrollments();

      },
      error => console.log(error)
    );
  }

  getEnrolledCandidatesPerCourse(): void {
    this.courseService.getInstructorCourses().subscribe(
      courses => {
        const seriesData = courses.map(course => ({
          name: course.title,
          y: 0 // Default value, will be updated once data is fetched
        }));

        // Iterate over each course to get enrolled candidates count
        courses.forEach((course, index) => {
          this.courseService.getCountOfEnrolledCandidatesForCourse(course.id).subscribe(
            count => {
              seriesData[index].y = count; // Update the y value with the fetched count
              this.updateChart(seriesData); // Update the chart with new data
            },
            error => console.log(error)
          );
        });
      },
      error => console.log(error)
    );
  }



  updateChart(seriesData: any[]): void {
    this.courseChart = new Chart({
      chart: {
        type: 'column',
        height: 400
      },
      title: {
        text: 'Enrolled Candidates per Course'
      },
      xAxis: {
        categories: seriesData.map(data => data.name),
        title: {
          text: 'Courses'
        }
      },
      yAxis: {
        min: 0,
        title: {
          text: 'Number of Enrolled Candidates'
        }
      },
      plotOptions: {
        column: {
          pointPadding: 0.2,
          borderWidth: 0,
        },
      },
      series: [{
        type: 'column',
        name: 'Enrolled Candidates',
        data: seriesData.map(data => data.y)
      }],
      credits: { enabled: false }
    });
  }



}
