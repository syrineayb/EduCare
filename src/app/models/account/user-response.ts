// user-response.ts
export interface UserResponse {
  id: number;
  firstname: string;
  lastname: string;
  fullname: string;
  email: string;
  password: string;
  createdAt: string;
  lastLogin: string;
  enabled: boolean;
  active: boolean;
  roles: string; // Change to string array type
}
