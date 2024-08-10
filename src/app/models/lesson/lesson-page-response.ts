import {LessonResponse} from "./lesson-response";

export interface LessonPageResponse {
  content?: LessonResponse[];
  totalPages?: number;
}
