export interface CourseRequest {
  title: string;
  description: string;
  topicId: number;
  imageFile?: File;
  startDate: string; // You may need to adjust the type based on your date format
  endDate: string; // You may need to adjust the type based on your date format
  free: boolean;
 // price?: number;
  timeCommitment: string;
}
