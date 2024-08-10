import {UserResponse} from "./user-response";

export interface AccountPageResponse {
    content?: UserResponse[];
    totalPages?: number;
}
