import {OptionResponse} from "../option/optionResponce";
import {OptionRequest} from "../option/optionRequest";

export interface QuestionResponse {
  id: number;
  text: string;
  type:string;
  quizId:number;
  quizTitle: string;
  duration:number;
  mark:number;
  courseId:number;
  options: OptionResponse[];
}
