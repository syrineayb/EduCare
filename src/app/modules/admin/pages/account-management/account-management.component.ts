import {Component, ElementRef, OnInit, ViewChild} from '@angular/core';
import { ToastrService } from 'ngx-toastr';
import { AuthenticationService } from '../../../../services/auth/authentication.service';
import { UserResponse } from '../../../../models/account/user-response';
import { UserRequest } from '../../../../models/account/user-request';
import { AccountService } from '../../../../services/account/account.service';
import { TokenService } from '../../../app-common/services/token/token.service';
import { AccountPageResponse } from "../../../../models/account/AccountPageResponse";
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import {RegisterRequest} from "../../../../models/authentication/register-request";
import * as moment from 'moment';
@Component({
  selector: 'app-account-management',
  templateUrl: './account-management.component.html',
  styleUrls: ['./account-management.component.css']
})
export class AccountManagementComponent implements OnInit {
  users: UserResponse[] = [];
  filteredUsers: UserResponse[] = [];
    newUser: UserRequest = {
        fullname: undefined,
        email: '',
        password: '',
        firstname: '',
        lastname: '',
        role: ''
    };
    searchTerm: string = '';
    selectedRole: string = '';
    currentPage = 0;
    totalPages = 0;
    pageSize = 4; // Number of users per page
    pages: number[] = []; // Define pages array
    newUserForm!: FormGroup;
    errorMsg: string[] = [];
    toastShown: boolean = false;
    accountRequest: UserRequest = {fullname: "", email: "", firstname: "", lastname: "", password: "", role: ""};
    passwordVisible: boolean = false;
    @ViewChild('passwordInput') passwordInput!: ElementRef;

  constructor(
    private formBuilder: FormBuilder, // Inject FormBuilder
    private userService: AccountService,
    private authService: AuthenticationService,
    private toastr: ToastrService,
    private tokenService: TokenService
  ) {}

  ngOnInit(): void {
    this.initializeForm(); // Initialize the form when the component initializes
    this.loadUsers();
  }

  initializeForm(): void {
    this.newUserForm = this.formBuilder.group({
      firstname: ['', [Validators.required, Validators.minLength(2), Validators.maxLength(50)]],
      lastname: ['', [Validators.required, Validators.minLength(2), Validators.maxLength(50)]],
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(6), Validators.maxLength(16)]],
      role: ['', Validators.required]
    });
  }


  get f() {
    return this.newUserForm.controls;
  }


  createUser(): void {
    if (this.newUserForm.invalid) {
      Object.keys(this.newUserForm.controls).forEach(key => {
        this.newUserForm.controls[key].markAsTouched();
      });
      this.toastr.error('Please fill in all the required fields with correct information.', 'Form Validation Error', {
        positionClass: 'toast-center-center',
        toastClass: 'custom-toast-error',
      });
      return;
    }

    this.accountRequest.role = this.newUserForm.value.role;
    this.accountRequest.email = this.newUserForm.value.email;
    this.accountRequest.firstname = this.newUserForm.value.firstname;
    this.accountRequest.lastname = this.newUserForm.value.lastname;
    this.accountRequest.password = this.newUserForm.value.password;

    this.userService.checkEmailExists(this.newUserForm.value.email).subscribe(
      (emailExists: boolean) => {
        if (emailExists) {
          this.toastr.error('This email is already registered. Please use a different email.', 'Error', {
            positionClass: 'toast-center-center',
            toastClass: 'custom-toast-error',
          });
        } else {
          this.userService.createUser(this.newUserForm.value).subscribe(
            (response: any) => {
              console.log('User created successfully:', response);
              this.resetForm(); // Reset the form here
              this.loadUsers(); // Reload users after adding a new user
              this.showSuccessMessage('User added successfully.');
            },
            (error: any) => {
              console.error('Error creating user:', error);
              this.handleError(error);
            }
          );
        }
      },
      (error: any) => {
        console.error('Error checking email:', error);
        this.toastr.error('An unexpected error occurred. Please try again later.', 'Error', {
          positionClass: 'toast-center-center',
          toastClass: 'custom-toast-error',
        });
      }
    );
  }


    handleError(error: any): void {
        if (error.status === 400 && error.error && error.error.validationErrors) {
            // Handle validation errors
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
                this.toastr.error(this.errorMsg.join('<br/>'), 'Validation Error', {
                    positionClass: 'toast-center-center',
                    toastClass: 'custom-toast-error',
                    enableHtml: true
                });
            }
        }
    }



    loadUsers(): void {
    this.userService.getAllUsers(this.currentPage, this.pageSize)
      .subscribe(
        (response: AccountPageResponse) => {
          this.users = response.content || [];
          this.totalPages = response.totalPages || 0;
          this.pages = Array.from({ length: this.totalPages }, (_, i) => i + 1); // Update pages array
          this.filteredUsers = this.users; // Set filteredUsers to all users initially
        },
        (error: any) => {
          console.error('Error loading users:', error);
          this.showErrorMessage('Failed to fetch users. Please try again later.');
        }
      );
  }

    updateUser(userId: number, userRequest: UserRequest): void {
        if (userRequest.fullname) {
            const nameParts = userRequest.fullname.split(' ');
            userRequest.firstname = nameParts[0] || '';
            userRequest.lastname = nameParts[1] || '';
        }

        this.userService.updateUser(userId, userRequest).subscribe(
            updatedUser => {
                console.log('User updated successfully:', updatedUser);
                this.loadUsers();
                this.showSuccessMessage('User updated successfully.');
            },
            error => {
                console.error('Error updating user:', error);
                this.showErrorMessage('Failed to update user. Please try again later.');
            }
        );
    }


  deleteUser(userId: number): void {
    this.userService.deleteUser(userId).subscribe(
      () => {
        console.log('User deleted successfully');
        this.showSuccessMessage('User deleted successfully.');
        this.loadUsers();
      },
      error => {
        console.error('Error deleting user:', error);
        this.showErrorMessage('Failed to delete user. Please try again later.');
      }
    );
  }
  applyFilters(): void {
    if (this.searchTerm.includes('@')) {
      // Search by email
      this.userService.searchUsersByEmail(this.searchTerm).subscribe(
        (users: UserResponse[]) => {
          this.filteredUsers = users;
        },
        (error: any) => {
          console.error('Error searching users by email:', error);
          this.showErrorMessage('Failed to search users by email. Please try again later.');
        }
      );
    } else {
      // Check if the search term contains a space, indicating a full name search
      if (this.searchTerm.includes(' ')) {
        // Split the search term into first name and last name
        const [firstName, lastName] = this.searchTerm.split(' ');

        // Search by first name and last name
        this.userService.getUsersByFirstname(firstName).subscribe(
          (users: UserResponse[]) => {
            // Filter the result by last name
            this.filteredUsers = users.filter(user => user.fullname.toLowerCase().includes(lastName.toLowerCase()));
          },
          (error: any) => {
            console.error('Error searching users by first name:', error);
            this.showErrorMessage('Failed to search users by first name. Please try again later.');
          }
        );
      } else {
        // Search by role name
        if (this.selectedRole !== '') {
          this.userService.getUsersByRoleName(this.selectedRole).subscribe(
            (users: UserResponse[]) => {
              this.filteredUsers = users.filter(user =>
                user.fullname.toLowerCase().includes(this.searchTerm.toLowerCase()));
            },
            (error: any) => {
              console.error('Error filtering users by role:', error);
              this.showErrorMessage('Failed to filter users by role. Please try again later.');
            }
          );
        } else {
          // Search by name
          const filteredByName = this.users.filter(user =>
            user.fullname.toLowerCase().includes(this.searchTerm.toLowerCase())
          );
          this.filteredUsers = filteredByName;
        }
      }
    }
  }

  toggleActiveStatus(user: any): void {
    user.active = !user.active; // Toggle the active status of the user
    // Call the appropriate service method to update the user's active status in the backend
    if (user.active) {
      this.userService.activateUser(user.id).subscribe();
      console.log(user.fullname+" 's account is active: "+user.active);
    } else {
      this.userService.deactivateUser(user.id).subscribe();
      console.log(user.fullname+" 's account is deactivate: "+user.active);

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

    resetForm(): void {
        this.newUser = { // Reset the newUser object to its initial state
            fullname: '',
            email: '',
            firstname: '',
            lastname: '',
            password: '',
            role: '',
        };
        this.newUserForm.reset(); // Reset the form control values to empty
    }

  getUserStatus(): boolean {
    return this.authService.isAuthenticated();
  }

  isAdminUser(): boolean {
    return this.tokenService.isAdmin();
  }

  isAdmin(): boolean {
    return this.tokenService.isAdmin();
  }

  // Pagination methods
  changePage(page: number, event: Event): void {
    event.preventDefault(); // Prevent default anchor tag behavior
    this.currentPage = page;
    this.loadUsers();
  }

  nextPage(event: Event): void {
    event.preventDefault(); // Prevent default anchor tag behavior
    if (this.currentPage < this.totalPages - 1) {
      this.currentPage++;
      this.loadUsers();
    }
  }

  prevPage(event: Event): void {
    event.preventDefault(); // Prevent default anchor tag behavior
    if (this.currentPage > 0) {
      this.currentPage--;
      this.loadUsers();
    }
  }
  goToPage(page: number): void {
    this.currentPage = page - 1; // Convert page to 0-based index
    this.loadUsers(); // Reload users for the new page
  }

  canDeleteUser(createdAt: string, lastLogin: string): boolean {
    // Check if the user has never logged in
    if (!lastLogin) {
      return true;
    }

    // Check if the user has been registered for more than 3 months
    const currentDate = new Date();
    const registerDate = new Date(createdAt);
    const threeMonthsAgo = new Date(currentDate.getFullYear(), currentDate.getMonth() - 7, currentDate.getDate());
    return registerDate <= threeMonthsAgo;
  }
  clearSearch(): void {
    this.searchTerm = '';
    this.applyFilters();
  }
  togglePasswordVisibility() {
    this.passwordVisible = !this.passwordVisible;
    const passwordInput = document.getElementById('password') as HTMLInputElement;
    if (passwordInput) {
      passwordInput.type = this.passwordVisible ? 'text' : 'password';
    }
  }

}
