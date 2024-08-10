import {DiscussionResponse} from "./discussion/discussion-response";

export interface DiscussionPageResponse {
  content?: DiscussionResponse[];
  totalPages?: number;
  totalElements?:number;
}
