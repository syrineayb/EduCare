import {MessageResponse} from "./message-response";

export interface MessagePageResponse {
  content?: MessageResponse[];
  totalPages?: number;
  totalElements?:number;

}
