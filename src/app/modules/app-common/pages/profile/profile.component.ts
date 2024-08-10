import { Component, Input, OnInit } from '@angular/core';
import { Router } from "@angular/router";
import { TokenService } from "../../services/token/token.service";
import { HttpEventType, HttpResponse } from "@angular/common/http";
import {ProfileResponse} from "../../../../models/profile/profile-response";
import {ProfileService} from "../../../../services/profile/profile.service";
import {UserNavigationService} from "../../services/account_navigation/user-navigation.service";
import {ToastrService} from "ngx-toastr";
import {regions} from "../../components/store/country-data-store";

@Component({
  selector: 'app-profile',
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.css'],
})
export class ProfileComponent implements OnInit {
  @Input() username: string = '';
  @Input() location: string = '';
  @Input() biography: string = '';
  @Input() userDetails: any;
  role: string ='';
  userProfile: ProfileResponse | undefined;
  selectedFile: File | null = null;
  progress = 0;
  message = '';
  userId: string | null = localStorage.getItem('userId'); // Retrieve user ID from local storage
  countries: any = regions; // Utiliser les régions de la Tunisie
  selectedCountry: string | undefined;
  showGenderSpan: boolean = true;
  showCountrySpan: boolean = true;
  profileRouterLink: any;
  securityRouterLink: string = ''; // Holds the router link for the security based on the user role

  constructor(
      private router: Router,
      private profileService: ProfileService,
      private tokenService: TokenService,
      private userNavigationService: UserNavigationService,
      private toastr: ToastrService,

  ) { }


  ngOnInit(): void {
    this.role = this.userNavigationService.determineRole(this.tokenService) || ''; // Provide a default value of ''
    const { profileRouterLink, securityRouterLink } = this.userNavigationService.setRouterLinks(this.role);
    this.profileRouterLink = profileRouterLink;
    this.securityRouterLink = securityRouterLink;
    this.loadProfile();
  }



  loadProfile() {
    this.profileService.getCurrentUserProfile().subscribe({
      next: (res: ProfileResponse) => {
        this.userProfile = res;
        console.log('Profile displaying successfully:', res);
      },
      error: (err: any) => {
        console.error('Error fetching user profile:', err);
      }
    });
  }
  isProfileActive(): boolean {
    return this.userNavigationService.isLinkActive(this.router, this.profileRouterLink);
  }
// Check if the security link is active
  isSecurityActive(): boolean {
    return this.userNavigationService.isLinkActive(this.router, this.securityRouterLink);
  }
  /*getUserRole() {
    this.accountService.getUsersByRoleName('ROLE_ADMIN').subscribe({
      next: (users: any[]) => {
        if (users && users.length > 0) {
          this.role = 'admin';
          return;
        }
      },
      error: (err: any) => {
        console.error('Error fetching user role:', err);
      }
    });

    this.accountService.getUsersByRoleName('ROLE_INSTRUCTOR').subscribe({
      next: (users: any[]) => {
        if (users && users.length > 0) {
          this.role = 'instructor';
          return;
        }
      },
      error: (err: any) => {
        console.error('Error fetching user role:', err);
      }
    });

    this.accountService.getUsersByRoleName('ROLE_CANDIDATE').subscribe({
      next: (users: any[]) => {
        if (users && users.length > 0) {
          this.role = 'candidate';
          return;
        }
      },
      error: (err: any) => {
        console.error('Error fetching user role:', err);
      }
    });
  }


   */
  updateProfile(): void {
    if (this.userProfile) {
      this.profileService.updateUserProfile(this.userProfile).subscribe({
        next: (res: ProfileResponse) => {
          this.userProfile = res;
          console.log('Profile updated successfully:', res);
          this.showSuccessMessage('Profile updated successfully');
          // Update the selected country after profile update
          this.selectedCountry = this.userProfile.country;
        },
        error: (err: any) => {
          console.error('Error updating profile:', err);
          this.showErrorMessage('Error updating profile');

        }
      });
    } else {
      console.error('User profile is undefined');
    }
  }
  onFileSelected(event: any): void {
    this.selectedFile = event.target.files[0];
  }

  handleFileInput(event: any): void {
    console.log('File selected:', event);
    const fileInput = event.target as HTMLInputElement;
    if (fileInput.files && fileInput.files.length > 0) {
      this.selectedFile = fileInput.files[0];
    }
  }

  uploadProfileImage(): void {

    if (this.selectedFile) {
      if (this.userId) {
        const userIdNumber = parseInt(this.userId, 10); // Convert string to number
        if (!isNaN(userIdNumber)) {


          this.profileService.uploadProfileImage(userIdNumber, this.selectedFile).subscribe({
            next: (event: any) => {
              if (event.type === HttpEventType.UploadProgress) {
                this.progress = Math.round(100 * event.loaded / event.total);
                this.showSuccessMessage('Profile image updated successfully');
                this.loadProfile();

              } else if (event instanceof HttpResponse && event.body) {
                this.message = event.body.message; // Ensure event.body exists before accessing properties
                // Or navigate to another route if needed
              }
            },
            error: (error: any) => {
              console.error('Error uploading file:', error);
              this.showErrorMessage('Error updating profile image');
              // Handle error, such as showing an error message
            }
          });
        } else {
          console.error('Invalid user ID:', this.userId);
        }
      } else {
        console.error('User ID is null');
      }
    } else {
      console.error('No file selected');
      this.showErrorMessage('No file selected');

    }
  }


  getCurrentUserId(): void {
    this.profileService.getCurrentUserId().subscribe({
      next: (userId: number) => { // Change type to number
        console.log('Current user ID:', userId);
        // Use userId as needed
      },
      error: (err: any) => {
        console.error('Error fetching current user ID:', err);
        // Handle error
      }
    });
  }
  countryNameMatches(countryName: string, selectedCountry: string | undefined): boolean {
    if (!selectedCountry) {
      return false; // No country selected, so no option should be selected
    }
    return countryName.toLowerCase() === selectedCountry.toLowerCase();
  }


  toggleGenderSpan(): void {
    this.showGenderSpan = !this.userProfile?.gender; // Show the span if the gender is not selected
  }
  toggleCountrySpan(): void {
    this.showCountrySpan = !this.userProfile?.country; // Show the span if the country is not selected
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
