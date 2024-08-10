import { Component, Input, OnInit } from '@angular/core';
import { AuthenticationService } from "../../../../services/auth/authentication.service";
import { ProfileService } from "../../../../services/profile/profile.service";
import { ProfileResponse } from "../../../../models/profile/profile-response";
import { NavItem } from "./nav-data";

@Component({
  selector: 'app-side-bar',
  templateUrl: './side-bar.component.html',
  styleUrls: ['./side-bar.component.css']
})
export class SideBarComponent implements OnInit {
  @Input() navItems: NavItem[] = [];
  username: string | null = '';

  userProfile: ProfileResponse | undefined;

  constructor(
      private authService: AuthenticationService,
      private profileService: ProfileService
  ) { }

  ngOnInit(): void {
    this.username = this.authService.getUsername();
    this.loadUserProfile();
  }

  loadUserProfile(): void {
    this.profileService.getCurrentUserProfile().subscribe({
      next: (profile: ProfileResponse) => {
        this.userProfile = profile;
      },
      error: (error: any) => {
        console.error('Error fetching user profile:', error);
      }
    });
  }

  updateProfileImage(event: any): void {
    if (this.userProfile && this.userProfile.profileId) { // Check if userProfile and userId are defined
      const userId = this.userProfile.profileId;
      this.profileService.uploadProfileImage(userId, event.target.files[0]).subscribe({
        next: () => {
          // Reload user profile after updating profile image
          this.loadUserProfile();
        },
        error: (error: any) => {
          console.error('Error updating profile image:', error);
        }
      });
    } else {
      console.error('User profile or user ID is not available.');
    }
  }
}
