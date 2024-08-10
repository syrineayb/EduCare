import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthenticationService } from '../../services/auth/authentication.service';
import { RegisterRequest } from '../../models/authentication/register-request';
import { ToastrService } from "ngx-toastr";
import { TokenService } from "../../modules/app-common/services/token/token.service";
import {AccountService} from "../../services/account/account.service";

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.css']
})
export class RegisterComponent implements OnInit {
  registerRequest: RegisterRequest = {email: "", firstname: "", lastname: "", password: "", role: ""};
  registerForm!: FormGroup;
  errorMsg: string[] = [];
  toastShown: boolean = false;
  passwordVisible: boolean = false;

  constructor(
    private formBuilder: FormBuilder,
    private authService: AuthenticationService,
    private userService: AccountService,
    private toasterService: ToastrService,
    private router: Router,
    private tokenService: TokenService
  ) {
  }

  ngOnInit(): void {
    this.registerForm = this.formBuilder.group({
      firstname: ['', [Validators.required, Validators.minLength(2), Validators.maxLength(50)]],
      lastname: ['', [Validators.required, Validators.minLength(2), Validators.maxLength(50)]],
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(6), Validators.maxLength(16)]],
      role: ['', Validators.required]
    });
  }

  get f() {
    return this.registerForm.controls;
  }

  onSubmit() {
    if (this.registerForm.invalid) {
      if (!this.toastShown) {
        this.toasterService.error('Please fill in all the required fields with correct information.', 'Form Validation Error', {
          positionClass: 'toast-center-center',
          toastClass: 'custom-toast-error',
        });
        this.toastShown = true;
      }
      return;
    }

    // Check if email already exists
    const email = this.registerForm.value.email;
    this.userService.checkEmailExists(email).subscribe({
      next: (exists) => {
        if (exists) {
          // Email already exists, display error message
          this.toasterService.error('This email is already registered. Please use a different email.', 'Error', {
            positionClass: 'toast-center-center',
            toastClass: 'custom-toast-error',
          });
        } else {
          // Proceed with registration
          this.registerUser();
        }
      },
      error: (error) => {
        // Handle error in checking email existence
        console.error('Error checking email:', error);
        this.toasterService.error('An unexpected error occurred. Please try again later.', 'Server Error', {
          positionClass: 'toast-center-center',
          toastClass: 'custom-toast-error',
        });
      }
    });
  }

  registerUser() {
    // Set the role directly from the form value
    this.registerRequest.role = this.registerForm.value.role;
    this.registerRequest.email = this.registerForm.value.email;
    this.registerRequest.firstname = this.registerForm.value.firstname;
    this.registerRequest.lastname = this.registerForm.value.lastname;
    this.registerRequest.password = this.registerForm.value.password;

    this.authService.register(this.registerRequest).subscribe({
      next: (response) => {
        if (response.access_token) {
          // Handle successful registration
          this.tokenService.accessToken = response.access_token || '';
          console.log('Registration successful:', response);
          this.showRegistrationSuccess(); // Call the method to display success toast
          // this.router.navigateByUrl('/login');
        } else {
          console.error('Access token not found in response:', response);
        }
      },
      error: (error) => {
        console.error('Registration error:', error);
        this.errorMsg = [];

        if (error.status === 400 && error.error && error.error.validationErrors) {
          const validationErrors = error.error.validationErrors;
          validationErrors.forEach((validationError: string) => {
            switch (validationError) {
              case 'firstname':
                this.errorMsg.push('First name is required');
                break;
              case 'lastname':
                this.errorMsg.push('Last name is required');
                break;
              case 'email':
                this.errorMsg.push('Invalid email format');
                break;
              case 'password':
                this.errorMsg.push('Password is required and must be at least 6 characters long');
                break;
              case 'role':
                this.errorMsg.push('Please select a user type');
                break;
              default:
                this.errorMsg.push('An unexpected error occurred. Please try again later.');
                break;
            }
          });

          if (this.errorMsg.length > 0) {
            this.toasterService.error(this.errorMsg.join('<br/>'), 'Validation Error', {
              positionClass: 'toast-center-center',
              toastClass: 'custom-toast-error',
              enableHtml: true
            });
          }
        }
      }
    });
  }

  togglePasswordVisibility() {
    this.passwordVisible = !this.passwordVisible;
    const passwordInput = document.getElementById('password') as HTMLInputElement;
    if (passwordInput) {
      passwordInput.type = this.passwordVisible ? 'text' : 'password';
    }
  }

  showRegistrationSuccess() {
    this.toasterService.success('Registration successful!', 'Success', {
      positionClass: 'toast-center-center',
      toastClass: 'custom-toast-success',
    });

    setTimeout(() => {
      this.router.navigateByUrl('/login');
    }, 1000); // Redirect after 1 second (1000 milliseconds)
  }

}
