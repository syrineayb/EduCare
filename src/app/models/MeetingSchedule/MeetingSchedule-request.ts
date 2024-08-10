export interface MeetingScheduleRequest {
  meetingTitle?: string;
  durationMinutes?: number;
  dateTime?: string;
  meetingUrl?: string;
  meetingDescription?: string;
  instructorId?: number;
  courseId?: number | null;
  lessonId?: number | null;
}
