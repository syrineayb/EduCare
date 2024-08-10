import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { CourseService } from "../../../../services/course/course.service";
import { TopicService } from "../../../../services/topic/topic.service";
import { TopicResponse } from "../../../../models/topic/topic-response";
import { ToastrService } from "ngx-toastr";
import {Router} from "@angular/router";

@Component({
    selector: 'app-create-course',
    templateUrl: './create-course.component.html',
    styleUrls: ['./create-course.component.css']
})
export class CreateCourseComponent implements OnInit {
    createCourseForm!: FormGroup;
    selectedFile: File | null = null;
    topics: TopicResponse[] = [];

    imageUrl: string | ArrayBuffer | undefined;

    constructor(
        private formBuilder: FormBuilder,
        private courseService: CourseService,
        private topicService: TopicService,
        private toastr: ToastrService,
        private router: Router
    ) { }

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

    ngOnInit(): void {
        this.createCourseForm = this.formBuilder.group({
            title: ['', [Validators.required, Validators.minLength(2), Validators.maxLength(100)]],
            description: ['', [Validators.required, Validators.minLength(2)]],
            topicId: [null, Validators.required],
            startDate: [null, Validators.required],
            endDate: [null, Validators.required],
           // free: [true, Validators.required],
          //  price: [null],
            timeCommitment: ['', Validators.required]
        });
        this.loadTopics();
    }

    onSubmit(): void {
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
                   this.resetFileInput();                },
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
    onFileSelected(event: any): void {
        this.selectedFile = event.target.files[0];
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

    redirectToCourseList(): void {
        this.router.navigate(['/instructor/course-management']); // Redirect to the route for the list of courses
    }
}
