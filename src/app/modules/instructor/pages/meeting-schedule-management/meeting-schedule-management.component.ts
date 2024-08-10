import { Component } from '@angular/core';
import { MeetingScheduleResponse } from "../../../../models/MeetingSchedule/MeetingSchedule-response";
import { MeetingScheduleService } from "../../../../services/MeetingSchedule/meeting-schedule.service";
import { ToastrService } from "ngx-toastr";
import { Router } from "@angular/router";
import { MeetingSchedulePageResponse } from "../../../../models/MeetingSchedule/MeetingSchedulePageResponse";
import { MeetingScheduleRequest } from "../../../../models/MeetingSchedule/MeetingSchedule-request";

@Component({
  selector: 'app-meeting-schedule-management',
  templateUrl: './meeting-schedule-management.component.html',
  styleUrls: ['./meeting-schedule-management.component.css']
})
export class MeetingScheduleManagementComponent {
  meetingSchedules: MeetingScheduleResponse[] = [];
  filteredMeetingSchedules: MeetingScheduleResponse[] = [];
  searchTerm: string = '';
  searchDate: string = '';
  currentPage = 0;
  totalPages = 0;
  pageSize = 4; // Number of courses per page
  pages: number[] = []; // Define pages array
  errorMsg: string[] = [];
  toastShown: boolean = false;

  constructor(
    private meetingScheduleService: MeetingScheduleService,
    private toastr: ToastrService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadMeetingSchedules();
  }

  loadMeetingSchedules(): void {
    this.meetingScheduleService.getAuthenticatedInstructorMeetingSchedulesPaginated(this.currentPage, this.pageSize)
      .subscribe(
        (response: MeetingSchedulePageResponse) => {
          this.meetingSchedules = response.content || [];
          this.totalPages = response.totalPages || 0;
          this.pages = Array.from({ length: this.totalPages }, (_, i) => i + 1); // Update pages array
          this.filteredMeetingSchedules = this.meetingSchedules; // Set filteredMeetingSchedules to all meeting schedules initially
        },
        (error: any) => {
          console.error('Error loading meeting schedules:', error);
          this.showErrorMessage('Failed to fetch meeting schedules. Please try again later.');
        }
      );
  }




    searchMeetingSchedulesByTitle(keyword: string): void {
        this.filteredMeetingSchedules = this.meetingSchedules.filter(meetingSchedule => {
            return meetingSchedule.meetingTitle.toLowerCase().includes(keyword.toLowerCase());
        });
    }

    searchMeetingSchedulesByDate(date: string): void {
        this.filteredMeetingSchedules = this.meetingSchedules.filter(meetingSchedule => {
            // Assuming meetingSchedule.dateTime is a string in ISO format (e.g., '2024-05-17T12:00:00')
            return meetingSchedule.dateTime.includes(date);
        });
    }

    applyFilters(): void {
        if (this.searchTerm.trim() === '' && this.searchDate.trim() === '') {
            // No search term or date, load all meeting schedules
            this.loadMeetingSchedules();
        } else if (this.searchTerm.trim() !== '' && this.searchDate.trim() !== '') {
            // Apply both title and date filters
            this.searchMeetingSchedulesByTitle(this.searchTerm);
            this.searchMeetingSchedulesByDate(this.searchDate);
        } else if (this.searchTerm.trim() !== '') {
            // Apply title filter only
            this.searchMeetingSchedulesByTitle(this.searchTerm);
        } else {
            // Apply date filter only
            this.searchMeetingSchedulesByDate(this.searchDate);
        }
    }


    resetFilters(): void {
        this.searchTerm = '';
        this.searchDate = ''; // Reset the search date
        this.loadMeetingSchedules();
    }

  showSuccessMessage(message: string): void {
    this.toastr.success(message, 'Success', {
      positionClass: 'toast-center-center',
      toastClass: 'custom-toast-success',
    });
  }

  showErrorMessage(message: string): void {
    this.toastr.error(message, 'Error', {
      positionClass: 'toast-center-center',
      toastClass: 'custom-toast-error',
    });
  }

  // Pagination methods
  changePage(page: number, event: Event): void {
    event.preventDefault(); // Prevent default anchor tag behavior
    this.currentPage = page;
    this.loadMeetingSchedules();
  }

  nextPage(event: Event): void {
    event.preventDefault(); // Prevent default anchor tag behavior
    if (this.currentPage < this.totalPages - 1) {
      this.currentPage++;
      this.loadMeetingSchedules();
    }
  }

  prevPage(event: Event): void {
    event.preventDefault(); // Prevent default anchor tag behavior
    if (this.currentPage > 0) {
      this.currentPage--;
      this.loadMeetingSchedules();
    }
  }

  navigateToCreateMeetingSchedule() {
    this.router.navigate(['/instructor/create-meeting-schedule']);
  }

  updateMeetingSchedule(meetingSchedule: MeetingScheduleResponse) {
    const updatedMeetingScheduleRequest: MeetingScheduleRequest = {
      // Assuming we have form fields to capture these values, otherwise modify as needed
      meetingTitle: meetingSchedule.meetingTitle,
      meetingDescription: meetingSchedule.meetingDescription,
      durationMinutes: meetingSchedule.durationMinutes,
      dateTime: meetingSchedule.dateTime,
      meetingUrl: meetingSchedule.meetingUrl
    };

    this.meetingScheduleService.updateMeetingSchedule(meetingSchedule.id, updatedMeetingScheduleRequest)
      .subscribe(
        (response: MeetingScheduleResponse) => {
          this.showSuccessMessage('Meeting schedule updated successfully');
          this.loadMeetingSchedules(); // Refresh the list
        },
        (error: any) => {
          console.error('Error updating meeting schedule:', error);
          this.showErrorMessage('Failed to update meeting schedule. Please try again later.');
        }
      );
  }

  deleteMeetingSchedule(id: number) {
    this.meetingScheduleService.deleteMeetingSchedule(id)
      .subscribe(
        () => {
          this.showSuccessMessage('Meeting schedule deleted successfully');
          this.loadMeetingSchedules(); // Refresh the list
        },
        (error: any) => {
          console.error('Error deleting meeting schedule:', error);
          this.showErrorMessage('Failed to delete meeting schedule. Please try again later.');
        }
      );
  }
  isMeetingInPast(meetingDate: string): boolean {
    return new Date(meetingDate) < new Date();
  }


}
