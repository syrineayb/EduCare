import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { AccountService } from "../../../../services/account/account.service";
import { ChangePasswordRequest } from "../../../../models/account/change-password";
import { ToastrService } from 'ngx-toastr';
import { Router } from "@angular/router";
import { TokenService } from "../../services/token/token.service";
import { UserNavigationService } from "../../services/account_navigation/user-navigation.service";

@Component({
  selector: 'app-security-profile',
  templateUrl: './security-profile.component.html',
  styleUrls: ['./security-profile.component.css']
})
export class SecurityProfileComponent implements OnInit {
  passwordForm!: FormGroup;
  toastShown: boolean = false;
  changePasswordRequest: ChangePasswordRequest = { confirmationPassword: "", currentPassword: "", newPassword: "" };
  profileRouterLink: any;
  securityRouterLink: string = ''; // Holds the router link for the security based on the user role
  role: string = '';
  userId: string | null = localStorage.getItem('userId'); // Retrieve user ID from local storage

  constructor(
    private accountService: AccountService,
    private toastr: ToastrService,
    private router: Router,
    private formBuilder: FormBuilder,
    private toasterService: ToastrService,
    private tokenService: TokenService,
    private userNavigationService: UserNavigationService
  ) { }

  ngOnInit(): void {
    this.determineRole();
    this.setRouterLinks(); // Set router links based on the user role
    this.passwordForm = this.formBuilder.group({
      currentPassword: ['', Validators.required],
      newPassword: ['', [Validators.required, Validators.minLength(6), Validators.maxLength(16)]],
      confirmPassword: ['', Validators.required]
    });
  }

  get f() {
    return this.passwordForm.controls;
  }

  changePassword(): void {
    if (this.passwordForm.invalid) {
      this.showFormValidationError();
      return;
    }

    const { currentPassword, newPassword, confirmPassword } = this.passwordForm.value;

    // Check if the new password matches the confirm password
    if (newPassword !== confirmPassword) {
      this.showErrorMessage('New password and confirm password do not match.');
      return;
    }

    this.changePasswordRequest.currentPassword = currentPassword;
    this.changePasswordRequest.newPassword = newPassword;
    this.changePasswordRequest.confirmationPassword = confirmPassword;

    this.accountService.changePassword(this.changePasswordRequest).subscribe(
      () => {
        console.log('Password changed successfully');
        this.showSuccessMessage('Your password has been changed successfully.');
      },
      error => {
        console.error('Error changing password:', error);
        if (error.error === 'Wrong password') {
          // Set custom error for incorrect current password
          this.passwordForm.get('currentPassword')?.setErrors({ 'incorrectPassword': true });
          this.showErrorMessage('Current password is incorrect.');
        } else if (error.error === 'Passwords do not match') {
          // Handle case when passwords do not match
          this.showErrorMessage('Passwords do not match.');
        } else {
          // Handle other error messages
          this.showErrorMessage('An error occurred while changing your password. Please try again.');
        }
      }
    );
  }


  deleteAccount(): void {
    if (confirm('Are you sure you want to delete your account?')) {
      // Find the confirmation dialog element
      const confirmDialog = document.querySelector('.confirm-dialog');
      if (confirmDialog) {
        // Apply custom CSS class to the confirmation dialog
        confirmDialog.parentElement?.classList.add('confirm-dialog');
      }

      this.accountService.deleteAccount().subscribe(
        () => {
          // Account deleted successfully
          this.showSuccessMessage('Your account has been deleted.');
          // Redirect to the home page after a delay
          setTimeout(() => {
            this.router.navigateByUrl('/');
          }, 2000); // Adjust the delay as needed
        },
        error => {
          // Error occurred while deleting account
          console.error('Error deleting account:', error);
          this.showErrorMessage('An error occurred while deleting your account. Please try again.');
        }
      );
    }
  }

  deactivateAccount(): void {
    if (confirm('Are you sure you want to deactivate your account?')) {
      this.accountService.deactivateCurrentUserAccount().subscribe(
        () => {
          this.showSuccessMessage('Account deactivated successfully');
          setTimeout(() => {
            this.router.navigateByUrl('/');
          }, 2000); // Adjust the delay as needed
        },
        error => {
          console.error('Error deactivating account:', error);
          this.showErrorMessage('An error occurred while deactivating your account. Please try again.');
        }
      );
    }
  }


  determineRole() {
    this.role = this.userNavigationService.determineRole(this.tokenService) || '';
  }

  // Set router links based on the user's role
  setRouterLinks(): void {
    const { profileRouterLink, securityRouterLink } = this.userNavigationService.setRouterLinks(this.role);
    this.profileRouterLink = profileRouterLink;
    this.securityRouterLink = securityRouterLink;
  }

  // Check if the profile link is active
  isProfileActive(): boolean {
    return this.router.isActive(this.profileRouterLink, true);
  }

  // Check if the security link is active
  isSecurityActive(): boolean {
    return this.router.isActive(this.securityRouterLink, true);
  }

  private showFormValidationError(): void {
    if (!this.toastShown) {
      this.toasterService.error('Please fill in all the required fields with correct information.', 'Form Validation Error', {
        positionClass: 'toast-center-center',
        toastClass: 'custom-toast-error',
      });
      this.toastShown = true;
      setTimeout(() => {
        this.toastShown = false;
      }, 3000);
    }
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
