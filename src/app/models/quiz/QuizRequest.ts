export interface QuizRequest {
  title:string;
  duration: number;
  courseId?:number;
  mark:number;
  questionIds?: number[]; // Optional list of question IDs

}
