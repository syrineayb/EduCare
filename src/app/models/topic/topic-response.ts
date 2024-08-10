export  interface TopicResponse {
  id: number;
  title: string;
  imageFile?: string;
  createdAt: string; // You might need to adjust the date format based on your backend response
  updatedAt: string;
}
