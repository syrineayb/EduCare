import {DiscussionResponse} from "./discussion-response";

export interface DiscussionPageResponse {
  content?: DiscussionResponse[];
  totalPages?: number;
  totalElements?:number;
}
