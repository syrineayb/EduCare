import {QuestionResponse} from "../question/QuestionResponse";

export interface QuizResponse{
  id: number;
  title: string;
  duration: number;
  mark:number;
  courseTitle: string;
}
