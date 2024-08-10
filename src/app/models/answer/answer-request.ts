// answer-request.ts
export interface AnswerRequest {
  messageContent: string;
  discussionId?: number; // Make sure discussionId is defined here

  parentAnswerId?: number;
}
