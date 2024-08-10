import {LessonResponse} from "../lesson/lesson-response";

export interface CourseResponse {
  id: number;
  title: string;
  description: string;
  createdAt: string; // You may need to adjust the type based on your date format
  updatedAt: string; // You may need to adjust the type based on your date format
  topicId: number;
  topicTitle: string;
  imageFile: string;
  startDate: string; // You may need to adjust the type based on your date format
  endDate: string; // You may need to adjust the type based on your date format
  free: boolean;
 // price: number;
  timeCommitment: string;
  publisherName: string;
  lessons: LessonResponse[]; // Assuming LessonResponse is another interface

}
