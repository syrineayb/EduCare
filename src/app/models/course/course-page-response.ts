import {CourseResponse} from "./course-response";

export interface CoursePageResponse {
  content?: CourseResponse[];
  totalPages?: number;
}
