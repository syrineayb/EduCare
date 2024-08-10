import {Component, ElementRef, HostListener, OnDestroy, OnInit, ViewChild} from '@angular/core';
import {CalendarOptions, DayCellMountArg} from '@fullcalendar/core';
import dayGridPlugin from '@fullcalendar/daygrid';
import interactionPlugin from '@fullcalendar/interaction';
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {CourseResponse} from "../../../../models/course/course-response";
import {LessonResponse} from "../../../../models/lesson/lesson-response";
import {MeetingScheduleService} from "../../../../services/MeetingSchedule/meeting-schedule.service";
import {ToastrService} from "ngx-toastr";
import {Router} from "@angular/router";
import {MeetingScheduleRequest} from "../../../../models/MeetingSchedule/MeetingSchedule-request";
import {MeetingScheduleResponse} from "../../../../models/MeetingSchedule/MeetingSchedule-response";
import timeGridPlugin from "@fullcalendar/timegrid";
import {ProfileResponse} from "../../../../models/profile/profile-response";
import {ProfileService} from "../../../../services/profile/profile.service";
import {futureDateValidator} from "../../../app-common/validators/date-time-validator"; // import plugin for dateClick

declare var $: any; // Declare jQuery
declare var JitsiMeetExternalAPI: any;

@Component({
    selector: 'app-schedule',
    templateUrl: './schedule.component.html',
    styleUrls: ['./schedule.component.css']
})
export class ScheduleComponent implements OnInit, OnDestroy {
    meetingForm!: FormGroup; // Mark as definitely assigned
 //   @ViewChild('deleteIconTemplate', { static: true }) deleteIconTemplate!: ElementRef;
    @ViewChild('updateIconTemplate', { static: true }) updateIconTemplate!: ElementRef;
    selectedStartTime: string = '';
    selectedEndTime: string = '';
    dateTime: string | undefined;
    meetingUrl: string | undefined;
    selectedCourseId: number | null = null; // Initialize as null
    courses: CourseResponse[] | undefined;
    lessons: LessonResponse[] | undefined;
    selectedMeetingId: number | null = null; // Track the selected meeting ID

    selectedDate: string = ''; // Declare a variable to store the selected date
    domain: string = 'meet.jit.si';
    roomName: string = '';
    user: any;
    api: any;
    options: any;
    jitsiWindow: Window | null = null;
    showIframe: boolean = false;
    userProfile: ProfileResponse | undefined;
    numberOfParticipants: number = 0;
    calendarOptions: CalendarOptions = {
        initialView: 'timeGridWeek',
        dayCellDidMount: this.handleDayCell.bind(this),
        plugins: [dayGridPlugin, interactionPlugin, timeGridPlugin],
        slotLabelFormat: {
            hour: 'numeric',
            minute: '2-digit',
            omitZeroMinute: false,
            meridiem: 'short'
        },
        select: this.handleSelect.bind(this),
        dateClick: this.handleDateClick.bind(this),
        eventDidMount: (info) => {
            info.el.setAttribute('tabindex', '0');
        },
        slotLabelContent: (slotInfo) => {
            const timeSlotDiv = document.createElement('div');
            const [hour, minute] = slotInfo.text.split(':');
            const hourSpan = document.createElement('span');
            hourSpan.textContent = hour;
            hourSpan.style.fontSize = '20px';
            hourSpan.style.display = 'inline-block';
            const colonSpan = document.createElement('span');
            colonSpan.textContent = ':';
            colonSpan.style.fontSize = '14px';
            colonSpan.style.display = 'inline-block';
            const minuteSpan = document.createElement('span');
            minuteSpan.textContent = minute;
            minuteSpan.style.fontSize = '14px';
            minuteSpan.style.display = 'inline-block';
            timeSlotDiv.appendChild(hourSpan);
            timeSlotDiv.appendChild(colonSpan);
            timeSlotDiv.appendChild(minuteSpan);
            timeSlotDiv.style.width = '100%';
            timeSlotDiv.style.height = '50px';
            timeSlotDiv.style.lineHeight = '50px';
            timeSlotDiv.style.textAlign = 'center';
            return { domNodes: [timeSlotDiv] };
        },
        dayCellClassNames: (arg) => {
            const today = new Date();
            if (arg.date.toDateString() === today.toDateString()) {
                return ['custom-highlight'];
            }
            return [];
        },
        dayHeaderContent: (arg) => {
            const dayHeaderDiv = document.createElement('div');
            dayHeaderDiv.textContent = arg.text; // Display the day text (e.g., "Sun", "Mon", etc.)
            dayHeaderDiv.style.fontWeight = 'italic';
            dayHeaderDiv.style.textAlign = 'center';
            dayHeaderDiv.style.background = '#ffffff'; // Example background color (white)
            dayHeaderDiv.style.color = '#0d4fd9'; // Example text color (grey)
            return { domNodes: [dayHeaderDiv] };
        }
    };
    handleDayCell(arg: DayCellMountArg): void {
        const today = new Date();
        const cellDate = arg.date;

        if (cellDate.toDateString() === today.toDateString()) {
            arg.el.style.backgroundColor = '#F8F2FE'; // Set your desired purple color
        }
    }

    constructor(private meetingScheduleService: MeetingScheduleService,
                private formBuilder: FormBuilder,
                private toastr: ToastrService,
                private router: Router,
                private profileService: ProfileService
    ) { }

    ngOnInit(): void {
        this.loadMeetings(); // Load existing meetings when component initializes
        this.getInstructorCourses();
        this.initMeetingForm();

        // Call handleEventMount for each event after the component initializes
        this.calendarOptions.eventDidMount = this.handleEventMount.bind(this);
        this.profileService.getCurrentUserProfile().subscribe(
            (profile: ProfileResponse) => {
                this.userProfile = profile;
            },
            (error) => {
                console.error('Error fetching current user profile:', error);
            }
        );
        console.log(this.numberOfParticipants);
    }
    handleSelect(info: any) {
        // Update selected start and end times
        this.selectedStartTime = info.startStr;
        this.selectedEndTime = info.endStr;

        // Calculate duration
        const startTime = new Date(this.selectedStartTime);
        const endTime = new Date(this.selectedEndTime);
        const durationInMinutes = (endTime.getTime() - startTime.getTime()) / (1000 * 60);

        // Update form with selected time and duration
        this.meetingForm.patchValue({
            dateTime: this.selectedStartTime, // Set start time
            durationMinutes: durationInMinutes // Set duration
        });
    }

    handleEventMount(info: any): void {
        const eventElement = info.el;
        const meetingId = info.event.id; // Retrieve the meeting ID from the event

        // Log the retrieved meeting ID
        console.log('Meeting ID:', meetingId);

        // Create event container
        const eventContainer = document.createElement('div');
        eventContainer.classList.add('event-container');
        eventContainer.style.display = 'flex'; // Apply flexbox
        eventContainer.style.justifyContent = 'center'; // Center horizontally
        eventContainer.style.alignItems = 'center'; // Center vertically

        // Create delete icon inside the event container
     /*   const deleteIcon = document.createElement('i');
        deleteIcon.classList.add('fa', 'fa-trash', 'delete-icon');
        deleteIcon.style.cursor = 'pointer';
        deleteIcon.style.color = 'red';
        deleteIcon.style.margin = '5px 5px 0 5px'; // Adjust margin as needed (top-right-bottom-left)

        // Append delete icon to event container
        eventContainer.appendChild(deleteIcon);

        // Append event container to event element
        eventElement.appendChild(eventContainer);

        // Attach click event listener to the delete icon
        deleteIcon.addEventListener('click', () => {
            if (meetingId) {
                if (confirm('Are you sure you want to delete this meeting?')) {
                    this.onDelete(meetingId);
                }
            }
        });

      */

        // Example: Attach double-click event listener to the event element
        eventElement.addEventListener('dblclick', () => {
            // Handle double-click event (for update)
            this.handleEventDoubleClick(info);
        });
    }
    // Method to handle double-click event (for update)
    handleEventDoubleClick(info: any): void {
        const meetingId = info.event.id; // Retrieve the meeting ID from the event

        console.log('Double-clicked Meeting ID:', meetingId);

        if (meetingId) {
            // Call the method to fetch meeting details when event is double-clicked
            this.updateMeeting(meetingId);
        }
    }
    updateMeeting(meetingId: number): void {
        this.meetingScheduleService.getMeetingScheduleById(meetingId).subscribe(
            (meeting: MeetingScheduleResponse) => {
                this.meetingForm.patchValue({
                    meetingTitle: meeting.meetingTitle,
                    durationMinutes: meeting.durationMinutes,
                    dateTime: meeting.dateTime,
                    meetingUrl: meeting.meetingUrl,
                    meetingDescription: meeting.meetingDescription,
                    selectedCourseId: meeting.courseId,
                    selectedLessonId: meeting.lessonId
                });
                this.selectedMeetingId = meetingId;
                $('#addScheduleModal').modal('show');
            },
            (error: any) => {
                console.error('Error fetching meeting details:', error);
                this.showErrorMessage('Failed to load meeting details. Please try again.');
            }
        );
    }

    private isDateTimeInPast(dateTime: string): boolean {
        const selectedDateTime = new Date(dateTime);
        const currentDateTime = new Date();
        return selectedDateTime <= currentDateTime;
    }

    handleDateClick(arg: { dateStr: string }) {
        const selectedDateTime = new Date(arg.dateStr); // Convert selected date string to Date object
        const currentDateTime = new Date(); // Get current date and time

        // Compare only the date component
        const currentDateStr = currentDateTime.toISOString().split('T')[0];
        const selectedDateStr = selectedDateTime.toISOString().split('T')[0];

        // Compare the date and time components
        if (selectedDateStr === currentDateStr && selectedDateTime <= currentDateTime) {
            // Selected time is past the current time on the same day
            this.showErrorMessage('You cannot schedule a meeting in the past. Please choose a future date.'); // Alert the user
        } else if (selectedDateTime < currentDateTime) {
            // Selected date is in the past
            this.showErrorMessage('You cannot schedule a meeting in the past. Please choose a future date.'); // Alert the user
        } else {
            // Reset selectedMeetingId to null to indicate a new meeting
            this.selectedMeetingId = null;
            $('#addScheduleModal').modal('show'); // Show the modal
            this.meetingForm.get('dateTime')?.setValue(arg.dateStr); // Set the selected date to dateTime form control
            this.selectedDate = arg.dateStr; // Update the selectedDate variable
        }
    }

    initMeetingForm(): void {
        this.meetingForm = this.formBuilder.group({
            meetingTitle: ['', [Validators.required]],
            durationMinutes: ['', [Validators.required, Validators.min(1)]],
            dateTime: ['', [Validators.required, futureDateValidator()]], // Add future date validator
            meetingUrl: ['', [Validators.required]],
            meetingDescription: ['', [Validators.required]],
            selectedCourseId: ['', Validators.required],
            selectedLessonId: ['']
        });
    }
    loadMeetings(): void {
        this.meetingScheduleService.getAuthenticatedInstructorMeetingSchedules().subscribe(
            (meetings: MeetingScheduleResponse[]) => {
                const events = meetings.map(meeting => {
                    const endTime = new Date(meeting.dateTime);
                    endTime.setMinutes(endTime.getMinutes() + meeting.durationMinutes); // Calculate end time
                    const isMeetingOver = endTime < new Date(); // Check if meeting end time is in the past

                    return {
                        id: String(meeting.id), // Convert id to string type
                        title: `${meeting.courseTitle} - ${meeting.meetingTitle}`, // Concatenate meeting title and course title
                        start: meeting.dateTime,
                        end: this.calculateEndTime(meeting.dateTime, meeting.durationMinutes), // Calculate end time based on start time and duration
                        backgroundColor: isMeetingOver ? '#FFA07A' : '#D6ADFF', // Conditionally set background color
                    };
                });
                this.calendarOptions.events = events;
            },
            (error: any) => {
                console.error('Error loading meetings:', error);
            }
        );
    }

    calculateEndTime(startTime: string, durationMinutes: number): string {
        const startDateTime = new Date(startTime);
        const endDateTime = new Date(startDateTime.getTime() + durationMinutes * 60000); // Add duration in milliseconds
        return endDateTime.toISOString(); // Convert to ISO string format
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

    markFormGroupTouched(formGroup: FormGroup): void {
        Object.values(formGroup.controls).forEach(control => {
            control.markAsTouched();

            if (control instanceof FormGroup) {
                this.markFormGroupTouched(control);
            }
        });
    }

    onSubmit(): void {
        if (this.meetingForm.valid) {
            const formValues = this.meetingForm.value;

            // Prepare the meeting data from form values
            const meetingScheduleRequest: MeetingScheduleRequest = {
                meetingTitle: formValues.meetingTitle,
                durationMinutes: formValues.durationMinutes,
                dateTime: formValues.dateTime,
                meetingUrl: formValues.meetingUrl,
                meetingDescription: formValues.meetingDescription,
                courseId: formValues.selectedCourseId,
                lessonId: formValues.selectedLessonId
            };

            // Check if it's an update or create operation based on this.selectedMeetingId
            if (this.selectedMeetingId) {
                // Update existing meeting
                this.meetingScheduleService.updateMeetingSchedule(this.selectedMeetingId, meetingScheduleRequest).subscribe(
                    () => {
                        this.showSuccessMessage('Meeting schedule updated successfully.');
                        this.loadMeetings(); // Reload meetings after update
                        $('#addScheduleModal').modal('hide'); // Hide the modal
                    },
                    (error: any) => {
                        console.error('Error updating meeting schedule:', error);
                        this.showErrorMessage('Failed to update meeting schedule. Please try again.');
                    }
                );
            } else {
                // Create new meeting
                this.meetingScheduleService.createMeetingSchedule(meetingScheduleRequest).subscribe(
                    () => {
                        this.showSuccessMessage('Meeting schedule created successfully.');
                        this.loadMeetings(); // Reload meetings after creation
                        $('#addScheduleModal').modal('hide'); // Hide the modal
                    },
                    (error: any) => {
                        console.error('Error creating meeting schedule:', error);
                        this.showErrorMessage('Failed to create meeting schedule. Please try again.');
                    }
                );
            }
        } else {
            // Form is invalid, mark fields as touched to show errors
            this.markFormGroupTouched(this.meetingForm);
            this.showErrorMessage('Form is incomplete. Please fill out all required fields.');
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

    redirectToMeetingScheduleList() {
        this.router.navigate(['/instructor/meeting-schedule-management']);
    }

    launchMeeting() {
        this.showIframe = true;
        this.setupJitsiMeet();
    }

    onDelete(meetingId: number): void {
        console.log('hahaha sarouna')
        if (meetingId) {
            console.log(''+meetingId)
            // Show confirmation dialog before deleting the meeting
            if (confirm('Are you sure you want to delete this meeting?')) {
                // Call the service method to delete the meeting
                this.meetingScheduleService.deleteMeetingSchedule(meetingId).subscribe(
                    () => {
                        // Meeting deleted successfully
                        this.showSuccessMessage('Meeting deleted successfully.');
                        $('#addScheduleModal').modal('hide'); // Hide the modal
                        this.loadMeetings(); // Reload meetings after deletion
                    },
                    (error: any) => {
                        console.error('Error deleting meeting:', error);
                        this.showErrorMessage('Failed to delete meeting. Please try again.');
                    }
                );
            }
        }
    }
    setupJitsiMeet(): void {
        this.options = {
            roomName: this.roomName,
            width: '100%',
            height: 500,
            configOverwrite: {
                prejoinPageEnabled: false
            },
            interfaceConfigOverwrite: {
                TILE_VIEW_MAX_COLUMNS: 8
            },
            userInfo: {
                displayName: this.userProfile ? `${this.userProfile.firstName} ${this.userProfile.lastName}` : '',
                avatar: this.userProfile ? this.userProfile.profileImage : ''
            }
        };

        this.jitsiWindow = window.open(
            `https://${this.domain}/${this.roomName}`,
            '_blank',
            'width=800,height=600'
        );

        if (this.jitsiWindow) {
            this.jitsiWindow.onload = () => {
                this.api = new JitsiMeetExternalAPI(this.domain, this.options);
                this.api.addEventListener('readyToClose', this.handleClose);
                this.api.addEventListener('participantLeft', this.handleParticipantLeft);
                this.api.addEventListener('participantJoined', this.handleParticipantJoined);
                this.api.addEventListener('videoConferenceJoined', this.handleVideoConferenceJoined);
                this.api.addEventListener('videoConferenceLeft', this.handleVideoConferenceLeft);
                this.api.addEventListener('audioMuteStatusChanged', this.handleMuteStatus);
                this.api.addEventListener('videoMuteStatusChanged', this.handleVideoStatus);


                this.getInitialParticipantsCount();

            };
        }
    }
    async getInitialParticipantsCount() {
        try {
            const participants = await this.api.getParticipantsInfo();
            this.numberOfParticipants = participants.length;
        } catch (error) {
            console.error('Error getting initial participants count:', error);
        }
    }
    @HostListener('window:beforeunload')
    ngOnDestroy() {
        if (this.api) {
            this.api.dispose();
        }
        if (this.jitsiWindow) {
            this.jitsiWindow.close();
        }
        this.router.navigate(['/instructor/']);
    }

    handleClose = () => {
        console.log("handle close");
    }

    handleParticipantLeft = () => {
        this.numberOfParticipants--;
        console.log('Number of participants:', this.numberOfParticipants);
    }

    handleParticipantJoined = () => {
        this.numberOfParticipants++;
        console.log('Number of participants:', this.numberOfParticipants);
    }

    handleVideoConferenceJoined = () => {
        this.numberOfParticipants = this.api.getNumberOfParticipants();
        console.log('Number of participants:', this.numberOfParticipants);
    }

    handleVideoConferenceLeft = () => {
        this.numberOfParticipants = 0;
        console.log('Number of participants:', this.numberOfParticipants);
    }

    handleMuteStatus = (audio: any) => {
        console.log('handleMuteStatus', audio);
    }

    handleVideoStatus = (video: any) => {
        console.log('handleVideoStatus', video);
    }

    getParticipants() {
        return new Promise((resolve, reject) => {
            setTimeout(() => {
                resolve(this.getParticipants());
            }, 500);
        });
    }
    resetForm(): void {
        this.meetingForm.reset(); // Reset the form
        this.selectedDate = ''; // Reset the selected date
    }
}
