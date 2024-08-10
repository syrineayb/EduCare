import { Injectable } from '@angular/core';
import {Router} from "@angular/router";
import {TokenService} from "../token/token.service";

@Injectable({
  providedIn: 'root'
})
export class UserNavigationService {

  constructor() { }
  determineRole(tokenService: TokenService): string | undefined {
    if (tokenService.isCandidate()) {
      return 'candidate';
    } else if (tokenService.isInstructor()) {
      return 'instructor';
    } else if (tokenService.isAdmin()) {
      return 'admin';
    }
    return undefined;
  }

  setRouterLinks(role: string): { profileRouterLink: string, securityRouterLink: string } {
    let profileRouterLink = '';
    let securityRouterLink = '';

    switch (role) {
      case 'admin':
        profileRouterLink = '/admin/profile';
        securityRouterLink = '/admin/account-settings';
        break;
      case 'instructor':
        profileRouterLink = '/instructor/profile';
        securityRouterLink = '/instructor/account-settings';
        break;
      case 'candidate':
        profileRouterLink = '/candidate/profile';
        securityRouterLink = '/candidate/account-settings';
        break;
    }

    return { profileRouterLink, securityRouterLink };
  }

  isLinkActive(router: Router, link: string): boolean {
    return router.isActive(link, true);
  }
}
