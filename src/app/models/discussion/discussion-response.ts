  import {MessageResponse} from "../message/message-response";
  import {AnswerResponse} from "../answer/answer-response";

  export interface DiscussionResponse {
    id: number;
    subject: string;
    message: MessageResponse;
    createdAt: Date;
    updatedAt: Date;
    lastMessageDate: Date;
    authorId: number;
    authorFullName: string;
    forumId: number;
    answers?: AnswerResponse[];
    showAnswers: boolean;
    updatedSubject?: string; // Add this line
    editMode?: boolean; // Add this line if it doesn't already exist

  }
