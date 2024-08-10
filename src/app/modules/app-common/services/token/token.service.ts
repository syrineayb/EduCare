import { Injectable } from '@angular/core';
import {JwtHelperService} from '@auth0/angular-jwt';
@Injectable({
  providedIn: 'root'
})
export class TokenService {
  private jwtHelper: JwtHelperService;

  constructor() {
    this.jwtHelper = new JwtHelperService();
  }

  set accessToken(accessToken: string) {
    localStorage.setItem('accessToken', accessToken);
  }

  get accessToken() {
    return localStorage.getItem('accessToken') as string;
  }

  isTokenValid() {
    const accessToken = this.accessToken;
    if (!accessToken) {
      return false;
    }
    // Decode the token
    const jwtHelper = new JwtHelperService();
    const decodedToken = jwtHelper.decodeToken(accessToken);
    console.log('Decoded Token:', decodedToken); // Log decoded token payload
    // Check expiry date
    const isTokenExpired = jwtHelper.isTokenExpired(accessToken);
    if (isTokenExpired) {
      localStorage.clear();
      return false;
    }
    return true;
  }


  isTokenNotValid() {
    return !this.isTokenValid();
  }

  get userRoles(): string[] {
    const accessToken = this.accessToken;
    if (accessToken) {
      const jwtHelper = new JwtHelperService();
      const decodedToken = jwtHelper.decodeToken(accessToken);
      console.log(decodedToken.authorities);
      return decodedToken.role || []; // Assuming roles are stored under 'role' in the token
    }
    return [];
  }

  isUser(): boolean {
    return this.userRoles.includes('ROLE_USER');
  }

  isAdmin(): boolean {
    const accessToken = this.accessToken;
    if (!accessToken) {
      return false;
    }

    // Decode the token
    const jwtHelper = new JwtHelperService();
    const decodedToken = jwtHelper.decodeToken(accessToken);

    // Check if "role" array exists in the decoded token payload
    if (decodedToken && decodedToken.role) {
      // Check if the user has the "ROLE_ADMIN" role
      return decodedToken.role.some((role: any) => role.name === 'ROLE_ADMIN');
    }

    return false;
  }

  isCandidate(): boolean {
    const accessToken = this.accessToken;
    if (!accessToken) {
      return false;
    }
    // Decode the token
    const jwtHelper = new JwtHelperService();
    const decodedToken = jwtHelper.decodeToken(accessToken);

    // Check if "role" array exists in the decoded token payload
    if (decodedToken && decodedToken.role) {
      // Check if the user has the "ROLE_CANDIDATE" role
      return decodedToken.role.some((role: any) => role.name === 'ROLE_CANDIDATE');
    }

    return false;
  }


  isInstructor(): boolean {
    const accessToken = this.accessToken;
    if (!accessToken) {
      return false;
    }

    // Decode the token
    const jwtHelper = new JwtHelperService();
    const decodedToken = jwtHelper.decodeToken(accessToken);

    // Check if "role" array exists in the decoded token payload
    if (decodedToken && decodedToken.role) {
      // Check if the user has the "ROLE_INSTRUCTOR" role
      return decodedToken.role.some((role: any) => role.name === 'ROLE_INSTRUCTOR');
    }

    return false;
  }

  isNotCandidate(): boolean {
    return !this.isCandidate();
  }
  isNotAdmin(): boolean {
    return !this.isAdmin();
  }
  isNotInstructor(): boolean {
    return !this.isInstructor();
  }

  hasNoRole(): boolean {
    const roles = this.userRoles;
    return roles.length === 0 || roles.includes('ROLE_NO_ROLE');
  }
  getUserFirstName(): string | null {
    const accessToken = this.accessToken;
    if (!accessToken) {
      return null;
    }

    const decodedToken = this.jwtHelper.decodeToken(accessToken);
    return decodedToken?.firstname || null;
  }

  getUserLastName(): string | null {
    const accessToken = this.accessToken;
    if (!accessToken) {
      return null;
    }

    const decodedToken = this.jwtHelper.decodeToken(accessToken);
    return decodedToken?.lastname || null;
  }
}
