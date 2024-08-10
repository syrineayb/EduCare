import {ForumResponse} from "./forum-response";

export interface ForumPageResponse {
  content?: ForumResponse[];
  totalPages?: number;
}
