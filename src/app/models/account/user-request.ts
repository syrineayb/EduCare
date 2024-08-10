// user-request.ts
export interface UserRequest {
  id?: number;
  firstname: string;
  lastname: string;
  fullname?: string;
  email: string;
  password: string;
  genre?: any; // Assuming Genre is a custom type
  role?: string; // Change to string type
}
