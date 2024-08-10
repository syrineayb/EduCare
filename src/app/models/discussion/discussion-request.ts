import {MessageRequest} from "../message/message-request";

export interface DiscussionRequest {
  subject: string;
  message: MessageRequest;
  //forumId: number;
}
