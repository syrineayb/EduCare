import {TopicResponse} from "../topic/topic-response";
import {ResourceResponse} from "./ResourceResponse";

export interface PageResponse{
  content?: ResourceResponse[];
  totalPages?: number;
  totalElements?:number;
}
