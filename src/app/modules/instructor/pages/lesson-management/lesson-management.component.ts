// lesson-management.component.ts
import { Component, OnInit } from '@angular/core';

import {LessonService} from "../../../../services/lesson/lesson.service";
import {LessonPageResponse} from "../../../../models/lesson/lesson-page-response";
import {ActivatedRoute, Router} from "@angular/router";
@Component({
  selector: 'app-lesson-management',
  templateUrl: './lesson-management.component.html',
  styleUrls: ['./lesson-management.component.css']
})
export class LessonManagementComponent implements OnInit {
  courseId: number | null = null; // Initialize courseId as null
  lessons: LessonPageResponse | undefined;
  currentPage: number = 1;
  pageSize: number = 10; // Adjust the page size as needed

  constructor(
      private router: Router,
      private route: ActivatedRoute,
      private lessonService: LessonService // Inject your LessonService
  ) { }

  ngOnInit(): void {
    this.route.paramMap.subscribe(params => {
      const courseIdParam = params?.get('id');
      if (courseIdParam !== null && courseIdParam !== undefined) {
        this.courseId = +courseIdParam;
        this.loadLessons();
      } else {
        // Handle the case where the parameter is missing or null
      }
    });
  }

  loadLessons(): void {
    // Check if courseId is null before making the service call
    if (this.courseId !== null) {
      // Fetch lessons for the current course
      this.lessonService.getAllLessonsByCourses(this.courseId, this.currentPage, this.pageSize)
          .subscribe(response => {
            this.lessons = response;
          });
    } else {
      // Handle the case where courseId is null
      console.error('Course ID is null');
    }
  }

  onPageChange(page: number): void {
    this.currentPage = page;
    this.loadLessons();
  }

  // Method to navigate to the add lesson page
  navigateToAddLesson(courseId: number): void {
    // Navigate to the add lesson page with the course ID
    this.router.navigate(['/add-lesson', courseId]);
  }

  // Method to navigate to the edit lesson page
  navigateToEditLesson(lessonId: number): void {
    // Navigate to the edit lesson page with the lesson ID
    this.router.navigate(['/edit-lesson', lessonId]);
  }

  // Method to delete a lesson
  deleteLesson(lessonId: number): void {
    // Call the delete lesson method from the lesson service
    // Handle success and error cases accordingly
  }

  // Other methods for lesson management
}
