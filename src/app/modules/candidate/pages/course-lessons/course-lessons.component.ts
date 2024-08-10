import {Component, OnInit} from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router'; // Import Router
import { LessonPageResponse } from "../../../../models/lesson/lesson-page-response";
import { LessonService } from "../../../../services/lesson/lesson.service";
import { LessonResponse } from "../../../../models/lesson/lesson-response";
import {ResourceService} from "../../../../services/resource/resource.service";

@Component({
  selector: 'app-course-lessons',
  templateUrl: './course-lessons.component.html',
  styleUrls: ['./course-lessons.component.css']
})
export class CourseLessonsComponent implements OnInit{
  lessons: LessonResponse[] = [];
  courseId: number | undefined;

  videoCounts = new Map<number, number>(); // Map to store video counts for each lesson
  imageCounts = new Map<number, number>(); // Map to store image counts for each lesson
  pdfCounts = new Map<number, number>(); // Map to store PDF counts for each lesson
  progressValues = new Map<number, number>();

  constructor(
    private lessonService: LessonService,
    private route: ActivatedRoute,
    private resourceService :ResourceService,

    private router: Router // Inject Router
  ) { }

  ngOnInit(): void {
    this.route.params.subscribe(params => {
      this.courseId = params['courseId'];
      if (this.courseId !== undefined) {
        this.fetchLessons();
      }
    });
  }

  fetchLessons(): void {
    if (this.courseId !== undefined) {
      this.lessonService.getAllLessonsByCourse(this.courseId).subscribe({
        next: (lessons: LessonResponse[]) => {
          this.lessons = lessons ?? [];
          this.lessons.forEach(lesson => {
            this.fetchResourceCounts(lesson.id);
            this.fetchProgress(lesson.id);
          });
        },
        error: (error: any) => {
          console.error('Error fetching lessons:', error);
          // Handle error
        }
      });
    }
  }


  fetchProgress(lessonId: number): void {
    this.resourceService.getResourceProgress(lessonId).subscribe({
      next: response => {
        this.progressValues.set(lessonId, response.progress);
      },
      error: error =>{
        console.error('Error fetching progress:', error);
      }
    });
  }

  fetchResourceCounts(lessonId: number): void {
    this.resourceService.countVideosByLessonId(lessonId).subscribe({
      next: response => {
        this.videoCounts.set(lessonId, response.count);// Store video count for the lesson
      },
      error: error => {
        console.error('Error fetching video count:', error);
      }
    });

    this.resourceService.countImagesByLessonId(lessonId).subscribe({
      next: response => {
        this.imageCounts.set(lessonId, response.count); // Store image count for the lesson
      },
      error: error => {
        console.error('Error fetching image count:', error);
      }
    });

    this.resourceService.countPDFsByLessonId(lessonId).subscribe({
      next: response => {
        this.pdfCounts.set(lessonId, response.count);// Store PDF count for the lesson
      },
      error: error => {
        console.error('Error fetching PDF count:', error);
      }
    });
  }

  redirectToLessonDetails(lessonId: number): void {
    // Navigate to lesson details page passing lessonId as a parameter
    this.router.navigate(['/candidate/my-course/lessons', this.courseId, 'resources', lessonId]);
  }
  redirectToForum(lessonId: number): void {
    // Navigate to the forum page passing lessonId as a parameter
    this.router.navigate(['/candidate/my-course/lessons/forum', lessonId]);
  }
}
