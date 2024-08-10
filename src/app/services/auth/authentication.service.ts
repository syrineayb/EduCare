import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { catchError, map, Observable, of, throwError } from 'rxjs';
import { Router } from '@angular/router';
import { environment } from '../../../environments/environment';
import { RegisterRequest } from '../../models/authentication/register-request';
import { AccountService } from '../account/account.service';
import { AuthResponse } from '../../models/authentication/auth-response';
import {AuthRequest} from "../../models/authentication/auth-request";
import {TokenService} from "../../modules/app-common/services/token/token.service";
import {ToastrService} from "ngx-toastr";

@Injectable({
  providedIn: 'root'
})
export class AuthenticationService {
  baseUrl = environment.app.baseUrl;
  isLogged = false;
  fullname: string = '';
  userId: string | undefined;
  currentUser: any;

  constructor(
    private http: HttpClient,
    private router: Router,
    private tokenService: TokenService,
    private toasterService: ToastrService,
    private accountService: AccountService
  ) {
    this.checkLoginStatus();
  }

  login(authRequest: AuthRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.baseUrl}/api/auth/login`, authRequest).pipe(
      map(response => {
        this.handleLoginResponse(response);
        return response;
      }),
      catchError(error => {
        this.handleError(error);
        throw error;
      })
    );
  }


  //use logout api in backend
  logout(): void {
    localStorage.removeItem('accessToken');
    localStorage.removeItem('fullname');
    localStorage.removeItem('userId');
    this.isLogged = false;
    this.fullname = '';
    this.userId = undefined;
    this.router.navigate(['/']);
  }

  private checkLoginStatus(): void {
    const token = localStorage.getItem('accessToken');
    this.isLogged = !!token;
    if (this.isLogged) {
      this.fullname = localStorage.getItem('fullname') || '';
      this.userId = localStorage.getItem('userId') || '';
    } else {
      // If no token is found, user is not authenticated
      this.isLogged = false;
      this.fullname = '';
      this.userId = undefined; // Assign undefined instead of null
    }
  }


  private handleError(error: any): void {
    if (error.status === 403) {
      this.toasterService.error('Invalid email or password. Please try again.', 'Login Error', {
        positionClass: 'toast-center-center',
        toastClass: 'custom-toast-error',
      });
    } else {
      this.toasterService.error('An unexpected error occurred. Please try again later.', 'Server Error', {
        positionClass: 'toast-center-center',
        toastClass: 'custom-toast-error',
      });
    }
  }

  register(registerRequest: RegisterRequest) {
    console.log("role: " + registerRequest.role);
    return this.http.post<any>(`${this.baseUrl}/api/auth/register`, registerRequest);
  }

  isAuthenticated(): boolean {
    const token = localStorage.getItem('accessToken');
    return !!token;
  }

  getUsersByRole(role: string): Observable<any[]> {
    const url = `${this.baseUrl}/api/users/by-role/${role}`;
    return this.http.get<any[]>(url).pipe(
        catchError((error: any) => {
          console.error(`Error fetching users by role ${role}: ${error.message}`);
          return throwError(error);
        })
    );
  }

  getUserId(): string | null {
    return this.userId || null;
  }

  getUsername(): string {
    return this.fullname;
  }
  private handleLoginResponse(response: AuthResponse): void {
    this.isLogged = true;
    this.fullname = response.fullname || '';
    this.userId = response.userId || '';
    this.currentUser = response;
    if (response.access_token) {
      this.tokenService.accessToken = response.access_token;
    }
    localStorage.setItem('accessToken', response.access_token || '');
    localStorage.setItem('fullname', response.fullname || '');
    localStorage.setItem('userId', response.userId || '');

    if (this.userId) {
      this.accountService.activateUser(this.userId).subscribe(
        () => console.log('Active status updated successfully.'),
        error => console.error('Error updating active status:', error)
      );
    }
  }
  refreshToken(): Observable<void> {
    return this.http.post<void>('/api/auth/refresh-token', {});
  }

 /* getCurrentUser(): Observable<any> {
    return this.http.get<any>(`${this.baseUrl}/api/users/current-user`).pipe(
      map(response => {
        this.currentUser = response;
        return response;
      }),
      catchError(error => {
        console.error('Error fetching current user:', error);
        throw error;
      })
    );
  }

  */
  getCurrentUser(): Observable<any> {
    const token = localStorage.getItem('accessToken');
    const headers = new HttpHeaders({
      'Content-Type': 'application/json',
      Authorization: `Bearer ${token}`
    });
    return this.http.get<any>(`${this.baseUrl}/api/users/current-user`, { headers });
  }
}
