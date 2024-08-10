export interface MeetingScheduleResponse {
  id: number;
  meetingTitle: string;
  durationMinutes: number; // Duration in minutes
  dateTime: string; // Date and time in ISO 8601 format
  meetingUrl: string;
  meetingDescription: string;
  instructorFullName: string;
  courseId: number;
  courseTitle: string;
  lessonId: number;
  lessonTitle: string;
  createdAt: string; // Date and time of creation in ISO 8601 format
  updatedAt: string; // Date and time of last update in ISO 8601 format
  endTime?: string; // End time in ISO 8601 format

}
