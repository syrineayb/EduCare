export interface SubmitAnswerRequest {
  questionId: number;
  selectedOptionIds: number[]; // Changed to an array of numbers to support multiple selections
}
