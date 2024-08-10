// message.model.ts
export interface MessageRequest {
  messageText?: string;
  subject?: string;
  recordFile?: File;
  imageFile?: File;
  videoFile?: File;
  pdfFile?: File;
}
