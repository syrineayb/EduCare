import { Component, OnInit } from '@angular/core';
import {CalendarOptions, EventClickArg, DateSelectArg, DayCellMountArg} from '@fullcalendar/core';
import dayGridPlugin from '@fullcalendar/daygrid';
import interactionPlugin from '@fullcalendar/interaction';
import timeGridPlugin from '@fullcalendar/timegrid';
import { ToastrService } from 'ngx-toastr'; // Import ToastrService
import { MeetingScheduleResponse } from '../../../../models/MeetingSchedule/MeetingSchedule-response';
import { MeetingScheduleService } from '../../../../services/MeetingSchedule/meeting-schedule.service';
import { MeetingSchedulePageResponse } from '../../../../models/MeetingSchedule/MeetingSchedulePageResponse';

@Component({
    selector: 'app-my-scheduled-meetings',
    templateUrl: './my-scheduled-meetings.component.html',
    styleUrls: ['./my-scheduled-meetings.component.css']
})
export class MyScheduledMeetingsComponent implements OnInit {
    meetingSchedules: MeetingScheduleResponse[] = [];
    calendarOptions: CalendarOptions;

    constructor(
        private meetingScheduleService: MeetingScheduleService,
        private toastr: ToastrService // Inject ToastrService
    ) {
        // Initialize calendar options and bind handleSelect
        this.calendarOptions = {
            initialView: 'timeGridWeek',
            dayCellDidMount: this.handleDayCell.bind(this),
            plugins: [dayGridPlugin, interactionPlugin, timeGridPlugin],
            slotLabelFormat: {
                hour: 'numeric',
                minute: '2-digit',
                omitZeroMinute: false,
                meridiem: 'short'
            },
            select: this.handleSelect.bind(this), // Ensure correct binding here
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
            },
            events: [],

            buttonText: {
                today: 'Today', // Change text for today button
            },

            eventClick: (info: EventClickArg) => {
                const meetingUrl = info.event.extendedProps['meetingUrl'];
                const eventStartTime = info.event.startStr; // Get the start time of the clicked event

                // Find the corresponding meeting based on event start time
                const matchingMeeting = this.meetingSchedules.find(meeting => {
                    const startDateTime = new Date(meeting.dateTime);
                    const endDateTime = new Date(startDateTime.getTime() + (meeting.durationMinutes * 60000));

                    // Check if the event start time falls within the meeting start and end time
                    return new Date(eventStartTime) >= startDateTime && new Date(eventStartTime) <= endDateTime;
                });

                // If matching meeting found, check if the meeting has already ended
                if (matchingMeeting) {
                    const startDateTime = new Date(matchingMeeting.dateTime);
                    const endDateTime = new Date(startDateTime.getTime() + (matchingMeeting.durationMinutes * 60000));

                    // Check if current date and time is after the end date and time of the meeting
                    if (new Date() > endDateTime) {
                        this.showErrorMessage('This meeting has already ended.');
                    } else {
                        // Open the meeting URL in a new tab
                        if (meetingUrl) {
                            window.open(meetingUrl, '_blank');
                        }
                    }
                } else {
                    console.error('Meeting not found for the clicked event start time:', eventStartTime);
                }
            }
        };
    }
    handleDayCell(arg: DayCellMountArg): void {
        const today = new Date();
        const cellDate = arg.date;

        if (cellDate.toDateString() === today.toDateString()) {
            arg.el.style.backgroundColor = '#F8F2FE'; // Set your desired purple color
        }
    }

    ngOnInit(): void {
        this.getMeetingSchedules();
    }

    getMeetingSchedules(): void {
        this.meetingScheduleService.getAuthenticatedCandidateMeetingSchedules(0, 100).subscribe(
            (response: MeetingSchedulePageResponse) => {
                this.meetingSchedules = response.content || [];

                // Calculate end date and time for each meeting
                this.meetingSchedules.forEach(meeting => {
                    const startDateTime = new Date(meeting.dateTime);
                    const endDateTime = new Date(startDateTime.getTime() + (meeting.durationMinutes * 60000));
                    meeting.endTime = endDateTime.toISOString();
                });

                // Update the calendar options with the new events
                this.updateCalendarOptions();
            },
            (error) => {
                console.error('Error fetching meeting schedules', error);
            }
        );
    }

    updateCalendarOptions(): void {
        this.calendarOptions.events = this.meetingSchedules.map(meeting => {
            const startDateTime = new Date(meeting.dateTime);
            const endDateTime = new Date(startDateTime.getTime() + (meeting.durationMinutes * 60000));
            const currentDate = new Date();

            // Determine background color based on meeting date
            let backgroundColor = '#D6ADFF'; // Default color
            if (endDateTime < currentDate) {
                backgroundColor = '#F0A6A6'; // Past meetings color
            } else if (startDateTime <= currentDate && endDateTime >= currentDate) {
                backgroundColor = '#A6F0C6'; // Ongoing meetings color
            }

            return {
                title: meeting.meetingTitle || 'No title available',
                start: meeting.dateTime,
                end: meeting.endTime,
                description: meeting.meetingDescription,
                backgroundColor: backgroundColor, // Set background color based on condition
                extendedProps: {
                    courseTitle: meeting.courseTitle,
                    lessonTitle: meeting.lessonTitle,
                    instructorFullName: meeting.instructorFullName,
                    meetingUrl: meeting.meetingUrl
                }
            };
        });
    }

    handleSelect = (arg: DateSelectArg): void => {
        const selectedDate = new Date(arg.startStr);
        const currentDate = new Date();

        // Calculate end date and time for each meeting based on duration
        const invalidMeeting = this.meetingSchedules.find(meeting => {
            const meetingStart = new Date(meeting.dateTime);
            const meetingEnd = new Date(meetingStart.getTime() + (meeting.durationMinutes * 60000)); // Calculate end time
            return meetingStart < currentDate || meetingEnd < currentDate;
        });

        if (invalidMeeting) {
            this.showErrorMessage('Error: You cannot select a meeting that has already passed.');
            return;
        }

        // Proceed with handling the selection (if needed)
        console.log('Selected date:', selectedDate);
    };

    private showErrorMessage(message: string): void {
        this.toastr.error(message, 'Error', {
            positionClass: 'toast-center-center',
            toastClass: 'custom-toast-error',
        });
    }
}
