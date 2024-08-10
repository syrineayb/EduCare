import {AnswerResponse} from "../answer/answer-response";

export interface MessageResponse {
  id: number;
  messageText: string;
  subject: string;
  authorId:number;
  recordUrl?: string;
  imageUrl?: string;
  videoUrl?: string;
  pdfUrl?: string;
  createdAt: Date;
  updatedAt: Date;
  authorFullName: string;
 // answers: AnswerResponse[]; // New property for answers
    editMode?: boolean; // Add this line if it doesn't already exist


    showAnswers: boolean;
    answers?: AnswerResponse[];
    forumId: number;
    lastMessageDate: Date;

}
