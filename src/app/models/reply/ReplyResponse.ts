  export interface ReplyResponse {
    id: number;
    discussionId: number;
    //forumId: number;
    messageContent: string;
    authorId: number;
    authorFullName: string;
    parentAuthorId: number;
    createdAt: string;
    updatedAt: string;
    replies?: ReplyResponse[]; // Nested replies
    parentAnswerId?: number;
    showReplyInput?: boolean; // Define showReplyInput property

  }
