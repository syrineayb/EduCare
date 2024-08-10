  import { Component, OnInit } from '@angular/core';
  import { ToastrService } from 'ngx-toastr';
  import { CourseResponse } from '../../../../models/course/course-response';
  import { CoursePageResponse } from '../../../../models/course/course-page-response';
  import { CourseService } from "../../../../services/course/course.service";
  import {TopicService} from "../../../../services/topic/topic.service";
  import {Router} from "@angular/router";
  import {LessonService} from "../../../../services/lesson/lesson.service";
  import {AbstractControl, FormBuilder, FormGroup, Validators} from "@angular/forms";
  import {TopicResponse} from "../../../../models/topic/topic-response";
  import {futureDateValidator} from "../../../app-common/validators/date-time-validator";

  @Component({
    selector: 'app-course-management',
    templateUrl: './course-management.component.html',
    styleUrls: ['./course-management.component.css']
  })
  export class CourseManagementComponent implements OnInit {
    courses: CourseResponse[] = [];
    filteredCourses: CourseResponse[] = [];
    searchTerm: string = '';
    currentPage = 0;
    totalPages = 0;
    pageSize = 4; // Number of courses per page
    pages: number[] = []; // Define pages array
    errorMsg: string[] = [];
    toastShown: boolean = false;
    //selectedFile: File | null = null;
   // price: number | null | undefined;
    isHovered: boolean = false;
    createCourseForm!: FormGroup;
    selectedFile: File | null = null;
    topics: TopicResponse[] = [];

    imageUrl: string | ArrayBuffer | undefined;


    constructor(
      private courseService: CourseService,
      private toastr: ToastrService,
      private lessonService: LessonService,
      private router: Router,
      private topicService: TopicService,
      private formBuilder: FormBuilder // Inject FormBuilder
    ) {}


    ngOnInit(): void {
      this.createCourseForm = this.formBuilder.group({
        title: ['', [Validators.required, Validators.maxLength(200)]],
        description: ['', Validators.required],
        topicId: ['', Validators.required],
        timeCommitment: ['', Validators.required],
     //   imageFile: ['', Validators.required],
        startDate: ['', [Validators.required, futureDateValidator()]],
        endDate: ['', [Validators.required, futureDateValidator()]],
      }, { validator: this.endDateAfterStartDateValidator });
    this.loadTopics();
      this.loadCourses();
    }

    endDateAfterStartDateValidator(control: AbstractControl): { [key: string]: boolean } | null {
      const startDate = control.get('startDate')?.value;
      const endDate = control.get('endDate')?.value;
      if (startDate && endDate && endDate <= startDate) {
        return { 'endDateBeforeStartDate': true };
      }
      return null;
    }



    loadTopics(): void {
      this.topicService.getAllTopicsWithIdAndTitle().subscribe(
          topics => {
            this.topics = topics;
          },
          error => {
            console.error('Error loading topics:', error);
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

    createCourse(): void {
      if (this.createCourseForm.valid && this.selectedFile) {
        const formData = new FormData();
        formData.append('title', this.createCourseForm.get('title')!.value);
        formData.append('description', this.createCourseForm.get('description')!.value);
        formData.append('topicId', this.createCourseForm.get('topicId')!.value);
        formData.append('imageFile', this.selectedFile);
        formData.append('startDate', this.createCourseForm.get('startDate')!.value);
        formData.append('endDate', this.createCourseForm.get('endDate')!.value);
        //   formData.append('free', this.createCourseForm.get('free')!.value);
        /*    const priceValue = this.createCourseForm.get('price')?.value;
            formData.append('price', priceValue !== null ? priceValue : '');

         */
        formData.append('timeCommitment', this.createCourseForm.get('timeCommitment')!.value);

        this.courseService.createCourse(formData).subscribe(
            response => {
              this.showSuccessMessage('Course created successfully');
              this.createCourseForm.reset(); // Reset form after successful submission
              // Reset file input
              this.resetFileInput();
              this.loadCourses();
              },
            error => {
              this.showErrorMessage('Error creating course. Please try again.');
              console.error('Error creating course:', error);
            }
        );
      } else {
        this.showErrorMessage('Please fill in all required fields.');
      }
    }
    resetFileInput(): void {
      // Reset the value of the file input
      const fileInput = document.getElementById('imageFile') as HTMLInputElement;
      if (fileInput) {
        fileInput.value = ''; // Reset file input value
      }
    }
    get f() {
      return this.createCourseForm.controls;
    }
    resetForm(): void {
      this.createCourseForm.reset(); // Reset form fields
      this.selectedFile = null; // Clear selected file
      const fileInput = document.getElementById('imageFile') as HTMLInputElement;
      if (fileInput) {
        fileInput.value = ''; // Reset file input value
      }
    }
    onFileSelected(event: any): void {
      this.selectedFile = event.target.files[0];
    }


























    updateCourse(course: CourseResponse): void {
      console.log('Updating course:', course.title);
      console.log('Selected file:', this.selectedFile);

      const formData = new FormData();

      // Append only the fields that are not null or undefined
      if (course.title) formData.append('title', course.title);
      if (course.description) formData.append('description', course.description);
      if (course.startDate) formData.append('startDate', course.startDate.toString());
      if (course.endDate) formData.append('endDate', course.endDate.toString());
      if (course.timeCommitment) formData.append('timeCommitment', course.timeCommitment.toString());
      if (this.selectedFile) formData.append('imageFile', this.selectedFile);
      // Append price only if it's a valid number

      // Append price only if it's a valid number
    /*  if (typeof course.price === 'number' && !isNaN(course.price)) {
        formData.append('price', course.price.toString());
      }

     */
      // Make the HTTP request to update the course
      this.courseService.updateCourse(course.id, formData).subscribe({
        next: (res: any) => {
          this.showSuccessMessage('Course updated successfully.');
          console.log('Course updated successfully', res);
          this.loadCourses(); // Optionally, reload the list of courses
        },
        error: (error: any) => {
          console.error('Error updating course:', error);
          this.showErrorMessage('Failed to update course. Please try again later.');
        }
      });
    }

    deleteCourse(courseId: number): void {
      this.courseService.deleteCourse(courseId).subscribe(
        () => {
          console.log('Course deleted successfully');
          this.showSuccessMessage('Course deleted successfully.');
          this.loadCourses();
        },
        error => {
          console.error('Error deleting course:', error);
          this.showErrorMessage('Failed to delete course. Please try again later.');
        }
      );
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

   /* applyFilters(): void {
      if (this.searchTerm.trim() === '') {
        // No search term, show all topics
        this.filteredCourses = this.courses;
      } else {
        // Apply search term filter
        this.filteredCourses = this.courses.filter(course =>
          course.title.toLowerCase().includes(this.searchTerm.toLowerCase())
        );
      }
    }
    */
    applyFilters(): void {
      if (this.searchTerm.trim() === '') {
        // No search term, load all courses
        this.loadCourses();
      } else {
        // Apply search term filter
        this.searchCoursesByTitle(this.searchTerm);
      }
    }


    resetFilters(): void {
      this.searchTerm = '';
      this.loadCourses();
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

    // Pagination methods
    changePage(page: number, event: Event): void {
      event.preventDefault(); // Prevent default anchor tag behavior
      this.currentPage = page;
      this.loadCourses();
    }

    nextPage(event: Event): void {
      event.preventDefault(); // Prevent default anchor tag behavior
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

    onEditFileSelected(event: any): void {
      const file: File = event.target.files[0];
      this.selectedFile = file;
    }

    toggleHover(hovered: boolean): void {
      this.isHovered = hovered;
    }

    navigateToCreateCourse() {
      // Navigate to the route where users can create a new course
      this.router.navigate(['/instructor/create-course']);
    }
  // In your Course Management component or wherever the navigation occurs
    navigateToAddLesson(courseId: number): void {
      this.router.navigate(['/instructor/lesson-management/add', courseId]);
    }


    /*addVideoConference(courseId: number) {
      this.router.navigate(['/instructor/video-conference', courseId]);
    }

     */
  }
