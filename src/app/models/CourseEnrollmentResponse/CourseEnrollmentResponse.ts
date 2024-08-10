export interface CourseEnrollmentResponse {
  id: number;
  publisherId: number;
  candidateId: number;
  candidateImage: string;
  courseId: number;
  courseName: string;
  candidateName:string;
  candidateLastname:string;
  progress:number;
  quizProgress : number;
}
