import { Component } from '@angular/core';
import {TokenService} from "../../../app-common/services/token/token.service";
import {navbarData, NavItem} from "../../../app-common/components/side-bar/nav-data";

@Component({
  selector: 'app-main',
  templateUrl: './main.component.html',
  styleUrls: ['./main.component.css']
})
export class MainComponent {
  sidebarActive = true;
  navItems: NavItem[];

  constructor(private tokenService: TokenService) {
    const isUserInstarcator = this.tokenService.isInstructor(); // Check if the user is an instructor

    // Assign the appropriate navigation items based on whether the user is an instructor
    this.navItems = isUserInstarcator ? navbarData.instructor : navbarData.instructor;
  }
}
