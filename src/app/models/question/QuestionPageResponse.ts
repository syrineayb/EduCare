import {QuestionResponse} from "./QuestionResponse";

export interface QuestionPageResponse{
  content?: QuestionResponse[];
  totalPages?: number;
}
