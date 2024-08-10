import {OptionRequest} from "../option/optionRequest";

export interface QuestionRequest {
  title: string;
  type:string;
  options: OptionRequest[]; // Ensure this is an array of OptionRequest
}
