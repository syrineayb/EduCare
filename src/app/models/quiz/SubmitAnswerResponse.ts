export interface QuestionResult {
  questionId: number;
  correctOptionIds: number[];
  submittedOptionIds: number[];
}

export interface SubmitAnswerResponse {
  score: number;
  percentage: number;
  questionResults: QuestionResult[];
}
