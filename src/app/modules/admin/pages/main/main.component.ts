import { Component } from '@angular/core';
import {navbarData, NavItem} from "../../../app-common/components/side-bar/nav-data";
import {TokenService} from "../../../app-common/services/token/token.service";

@Component({
  selector: 'app-main',
  templateUrl: './main.component.html',
  styleUrls: ['./main.component.css']
})
export class MainComponent {
  sidebarActive = true;
  navItems: NavItem[];

  constructor(private tokenService: TokenService) {
    const isUserAdmin = this.tokenService.isCandidate(); // Check if the user is an instructor

    // Assign the appropriate navigation items based on whether the user is an instructor
    this.navItems = isUserAdmin ? navbarData.admin : navbarData.admin;
  }
  // Define the toggleSidebar function
  toggleSidebar() {
    this.sidebarActive = !this.sidebarActive;
  }
}
