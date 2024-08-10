import {MeetingScheduleResponse} from "./MeetingSchedule-response";

export interface MeetingSchedulePageResponse {
  content?: MeetingScheduleResponse[];
  totalPages?: number;
}
