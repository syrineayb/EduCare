import { Component, OnInit } from '@angular/core';
import { MeetingScheduleService } from '../../../../services/MeetingSchedule/meeting-schedule.service';
import { MeetingScheduleRequest } from '../../../../models/MeetingSchedule/MeetingSchedule-request';
import { CourseResponse } from '../../../../models/course/course-response';
import { LessonResponse } from '../../../../models/lesson/lesson-response';
import { FormBuilder, FormGroup, Validators } from "@angular/forms";
import { ToastrService } from 'ngx-toastr';
import {Router} from "@angular/router";
import {futureDateValidator} from "../../../app-common/validators/date-time-validator";

@Component({
    selector: 'app-meeting-schedule',
    templateUrl: './create-meeting-schedule.component.html',
    styleUrls: ['./create-meeting-schedule.component.css']
})
export class  CreateMeetingScheduleComponent implements OnInit {
    meetingTitle: string | undefined;
    durationMinutes: number | undefined;
    dateTime: string | undefined;
    meetingUrl: string | undefined;
    meetingDescription: string | undefined;
    selectedLessonId: number | null = null; // Initialize as null
    selectedCourseId: number | null = null; // Initialize as null
    courses: CourseResponse[] | undefined;
    lessons: LessonResponse[] | undefined;
    meetingForm!: FormGroup; // Mark as definitely assigned

    constructor(private meetingScheduleService: MeetingScheduleService,
                private formBuilder: FormBuilder,
                private toastr: ToastrService,
                private router: Router
    ) { }

    ngOnInit(): void {
        this.getInstructorCourses();
        // Initialize the form with validation rules
        this.meetingForm = this.formBuilder.group({
            meetingTitle:  ['', [Validators.required]],
            durationMinutes: ['', [Validators.required, Validators.min(1)]],
        //  dateTime: ['', [Validators.required, futureDateValidator()]], // Apply the custom validator
         //   meetingUrl:  ['', [Validators.required]],
            meetingDescription:  ['', [Validators.required]],
            selectedCourseId: ['', Validators.required]
        });
    }

    getInstructorCourses(): void {
        this.meetingScheduleService.getInstructorCourses()
            .subscribe(courses => this.courses = courses);
    }

    onCourseSelected(courseId: number | null): void {
        if (courseId !== null) {
            this.selectedCourseId = courseId;
            this.getAllLessonsByCourse(courseId);
        }
    }

    getAllLessonsByCourse(courseId: number): void {
        this.meetingScheduleService.getAllLessonsByCourse(courseId)
            .subscribe(lessons => this.lessons = lessons);
    }

    onSubmit(): void {
        // Check if the form is valid before submitting
        if (this.meetingForm.valid) {
            const meetingScheduleRequest: MeetingScheduleRequest = {
                meetingTitle: this.meetingForm.get('meetingTitle')?.value,
                durationMinutes: this.meetingForm.get('durationMinutes')?.value,
                dateTime: this.meetingForm.get('dateTime')?.value,
                meetingUrl: this.meetingForm.get('meetingUrl')?.value,
                meetingDescription: this.meetingForm.get('meetingDescription')?.value,
                courseId: this.meetingForm.get('selectedCourseId')?.value,
                lessonId: this.selectedLessonId
            };

            this.meetingScheduleService.createMeetingSchedule(meetingScheduleRequest)
                .subscribe(response => {
                    console.log('Meeting schedule created:', response);
                    this.showSuccessMessage('Meeting schedule created successfully.');
                    // Reset the form after successful submission
                    this.meetingForm.reset();
                    // Optionally, you can navigate to another page or show a success message here
                }, error => {
                    console.error('Error creating meeting schedule:', error);
                    this.showErrorMessage('Failed to create meeting schedule. Please try again.');
                    // Handle error, show error message, etc.
                });
        } else {
            // Form is invalid, mark all fields as touched to display error messages
            this.markFormGroupTouched(this.meetingForm);
            this.showErrorMessage('Form is incomplete. Please fill out all required fields.');
        }
    }

    // Helper function to mark all form controls as touched
    markFormGroupTouched(formGroup: FormGroup): void {
        Object.values(formGroup.controls).forEach(control => {
            control.markAsTouched();

            if (control instanceof FormGroup) {
                this.markFormGroupTouched(control);
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

  redirectToMeetingScheduleList() {
    this.router.navigate(['/instructor/meeting-schedule-management']);
  }

    launchMeeting() {
        console.log('taw nekhdmek');
    }
}
