import {InstructorResponse} from "./instructor-response";

export interface InstructorPageResponse {
  content?: InstructorResponse[];
  totalPages?: number;
}
