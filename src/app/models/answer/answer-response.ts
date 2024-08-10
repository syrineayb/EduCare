import {ReplyResponse} from "../reply/ReplyResponse";

export interface AnswerResponse {
  id: number;
  discussionId: number;
  //forumId: number;
  messageContent: string;
  messageText: string;
  authorId: number;
  authorFullName: string;
  createdAt: string;
  updatedAt: string;
  replies?: ReplyResponse[]; // Nested replies
  parentAnswerId?: number;
  showReplyInput?: boolean; // Define showReplyInput property

}
