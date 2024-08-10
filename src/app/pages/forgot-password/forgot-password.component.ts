import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { AccountService } from "../../services/account/account.service";
import { ChangePasswordRequest } from "../../models/account/change-password";
import { ToastrService } from "ngx-toastr";
import { FormBuilder, FormGroup, ValidationErrors, Validators } from "@angular/forms";

@Component({
  selector: 'app-forgot-password',
  templateUrl: './forgot-password.component.html',
  styleUrls: ['./forgot-password.component.css']
})
export class ForgotPasswordComponent implements OnInit {
  email: string = '';
  verificationCode: string = '';
  passwordForm!: FormGroup;

  changePasswordRequest: ChangePasswordRequest = {
    currentPassword: '', // This field could be used if needed
    newPassword: '',
    confirmationPassword: ''
  };
  changePasswordFormSubmitted: boolean = false;
  emailVerified: boolean = false;

  constructor(
    private accountService: AccountService,
    private toastr: ToastrService,
    private router: Router,
    private formBuilder: FormBuilder
  ) { }

  ngOnInit(): void {
    this.passwordForm = this.formBuilder.group({
      currentPassword: [''],
      newPassword: ['', [Validators.required, Validators.minLength(6), Validators.maxLength(16)]],
      confirmationPassword: ['', Validators.required]
    }, {
      validators: this.passwordMatchValidator // Custom validator for password match
    });
  }

  // Getter for easy access to form fields in the template
  get f() {
    return this.passwordForm.controls;
  }

  // Custom validator to check if new password and confirmation password match
  passwordMatchValidator(group: FormGroup): ValidationErrors | null {
    const newPasswordControl = group.get('newPassword');
    const confirmationPasswordControl = group.get('confirmationPassword');

    // Ensure confirmationPasswordControl is not null
    if (!newPasswordControl || !confirmationPasswordControl) {
      return null; // Exit early if controls are not found
    }

    const errors = confirmationPasswordControl.errors || {};

    // Check if passwords match and add/remove errors accordingly
    if (newPasswordControl.value !== confirmationPasswordControl.value) {
      errors['passwordMismatch'] = true;
    } else {
      delete errors['passwordMismatch'];
    }

    confirmationPasswordControl.setErrors(Object.keys(errors).length ? errors : null);
    return null;
  }


  onSubmit(): void {
    // Check if email exists
    this.accountService.checkEmailExistence(this.email).subscribe(
      exists => {
        if (exists) {
          // Email exists, proceed to show success message or further action
          this.emailVerified = true; // Set email verification flag to true
          this.showSuccessMessage('Verification code sent successfully.');
        } else {
          // Email does not exist, show error message
          this.showErrorMessage('Email does not exist in our system.');
        }
      },
      error => {
        // Error handling
        this.showErrorMessage('Error sending verification code. Please try again later.');
      }
    );
  }

  verifyCode(): void {
    this.accountService.verifyCode(this.email, this.verificationCode).subscribe({
      next: () => {
        // Verification code is correct, show change password form
        this.changePasswordFormSubmitted = true;
        this.emailVerified = true; // Optionally set emailVerified to true if needed
        this.showSuccessMessage('Verification code verified.');
      },
      error: (err) => {
        // Error verifying code
        this.showErrorMessage('Invalid verification code. Please try again.');
        console.error('Error verifying code:', err); // Log the error for debugging
      }
    });
  }


  onChangePassword(): void {
    // Validate new password and confirmation
    if (this.passwordForm.invalid) {
      // Mark all fields as touched to display validation errors
      this.passwordForm.markAllAsTouched();
      // Display error message
      this.showErrorMessage('Please fill in all required fields.');
      return;
    }

    const { currentPassword, newPassword, confirmationPassword } = this.passwordForm.value;

    // Check if new password is equal to current password
    if (newPassword === currentPassword) {
      this.showErrorMessage('New password cannot be the same as the current password.');
      return;
    }

    this.changePasswordRequest.newPassword = newPassword;
    this.changePasswordRequest.confirmationPassword = confirmationPassword;

    // Make API call to change password
    this.accountService.forgotpassword(this.email, this.changePasswordRequest).subscribe({
      next: () => {
        // Password changed successfully
        this.showSuccessMessage('Your password has been changed successfully.');
        this.changePasswordFormSubmitted = false;
        this.changePasswordRequest = {
          currentPassword: '',
          newPassword: '',
          confirmationPassword: ''
        };
        // Delay for 2 seconds and then navigate to login page
        setTimeout(() => {
          this.router.navigate(['/login']); // Replace '/login' with your actual login route
        }, 2000);
      },
      error: (err) => {
        // Error changing password
        this.showErrorMessage('Failed to change password. Please try again later.');
        console.error('Error changing password:', err); // Log the error for debugging
      }
    });
  }

  private showSuccessMessage(message: string): void {
    this.toastr.success(message, 'Success', {
      positionClass: 'toast-center-center',
      toastClass: 'custom-toast-success',
    });
  }

  private showErrorMessage(message: string): void {
    this.toastr.error(message, 'Error', {
      positionClass: 'toast-center-center',
      toastClass: 'custom-toast-error',
    });
  }
}
