export interface ChangePasswordRequest {
  currentPassword?: string; // Optional currentPassword
  newPassword: string;
  confirmationPassword: string;
}
