  import { Injectable } from '@angular/core';
  import {environment} from "../../../environments/environment";
  import {HttpClient, HttpHeaders, HttpParams} from "@angular/common/http";
  import {map, Observable} from "rxjs";
  import {UserRequest} from "../../models/account/user-request";
  import {UserResponse} from "../../models/account/user-response";
  import {AccountPageResponse} from "../../models/account/AccountPageResponse";
  import {ChangePasswordRequest} from "../../models/account/change-password";
  import {UserStats} from "../../models/account/UserStats";

  @Injectable({
    providedIn: 'root'
  })
  export class AccountService {
    baseUrl = environment.app.baseUrl;

    constructor(private http: HttpClient) { }
    getUserStats(): Observable<UserStats> {
      return this.http.get<UserStats>(`${this.baseUrl}/api/users/stats`);
    }

    getMonthlyUserActivity(): Observable<UserStats> {
      return this.http.get<UserStats>(`${this.baseUrl}/api/users/monthly-activity`);
    }

    getCurrentUser(): Observable<UserResponse> {
      const token = localStorage.getItem('accessToken');
      const headers = new HttpHeaders({
        'Content-Type': 'application/json',
        Authorization: `Bearer ${token}`
      });
      return this.http.get<UserResponse>(`${this.baseUrl}/current-user`, { headers });
    }
    createUser(user: UserRequest): Observable<void> {
      return this.http.post<void>(`${this.baseUrl}/api/users`, user);
    }

    getAllUsers(page: number, size: number): Observable<AccountPageResponse> {
      return this.http.get<AccountPageResponse>(`${this.baseUrl}/api/users?page=${page}&size=${size}`);
    }

    /*getAllUsers(): Observable<UserResponse[]> {
      return this.http.get<UserResponse[]>(`${this.baseUrl}/api/users`);
  }
     */
   /* .pipe(  map((users: UserResponse[]) => {
          // Filter out users with the admin role

      //    return users.filter(user => !user.roles.includes('ROLE_ADMIN'));
        })
      );

    */



    getUserById(userId: number): Observable<UserResponse> {
      return this.http.get<UserResponse>(`${this.baseUrl}/api/users/${userId}`);
    }
    getProfileById(userId: number): Observable<UserResponse> {
      const token = localStorage.getItem('accessToken');
      const headers = new HttpHeaders({
        'Content-Type': 'application/json',
        Authorization: `Bearer ${token}`
      });
      return this.http.get<UserResponse>(`${this.baseUrl}/api/profiles/${userId}`, { headers });
    }

    updateUser(userId: number, user: UserRequest): Observable<UserResponse> {
      const token = localStorage.getItem('accessToken');
      const headers = new HttpHeaders({
        'Content-Type': 'application/json',
        Authorization: `Bearer ${token}`
      });
      return this.http.put<UserResponse>(`${this.baseUrl}/api/users/${userId}`, user, { headers });
    }

    deleteUser(userId: number): Observable<void> {
      const token = localStorage.getItem('accessToken');
      const headers = new HttpHeaders({
        Authorization: `Bearer ${token}`
      });
      return this.http.delete<void>(`${this.baseUrl}/api/users/${userId}`, { headers });
    }

    deleteAccount(): Observable<any> {
      const token = localStorage.getItem('accessToken');
      const headers = new HttpHeaders({
        Authorization: `Bearer ${token}`
      });
      return this.http.delete<any>(`${this.baseUrl}/api/users/deleteAccount`, { headers });
    }
    getUsersByFirstname(firstname: string): Observable<UserResponse[]> {
      return this.http.get<UserResponse[]>(`${this.baseUrl}/api/users/by-firstname/${firstname}`);
    }
    getUsersByLastname(lastname: string): Observable<UserResponse[]> {
      return this.http.get<UserResponse[]>(`${this.baseUrl}/api/users/by-lastname//${lastname}`);
    }

    getUsersByRoleName(role: string): Observable<UserResponse[]> {
      return this.http.get<UserResponse[]>(`${this.baseUrl}/api/users/by-role/${role}`);
    }
    searchUsersByEmail(email: string): Observable<UserResponse[]> {
      return this.http.get<UserResponse[]>(`${this.baseUrl}/api/users/by-mail/${email}`);
    }

   /* checkEmailExists(email: string) {
      return this.http.get<boolean>(`${this.baseUrl}/api/users/check-email?email=${email}`);
    }

    */
    checkEmailExists(email: string) {
      return this.http.get<boolean>(`${this.baseUrl}/api/users/check-email?email=${email}`);
    }
    checkEmailExistence(email: string): Observable<boolean> {
      return this.http.post<boolean>(`${this.baseUrl}/api/users/check-email-and-send-code?email=${email}`, { email });
    }
    verifyCode(email: string, code: string): Observable<boolean> {
      // Make the POST request with parameters and expect a boolean response
      return this.http.post<boolean>(`${this.baseUrl}/api/users/verify-code?email=${email}&code=${code}`, { email, code });
    }
    activateUser(userId: string): Observable<void> {
      return this.http.put<void>(`${this.baseUrl}/api/users/${userId}/activate`, {});
    }

    deactivateUser(userId: string): Observable<void> {
      return this.http.put<void>(`${this.baseUrl}/api/users/${userId}/deactivate`, {});
    }
    deactivateCurrentUserAccount(): Observable<void> {
      const token = localStorage.getItem('accessToken');
      const headers = new HttpHeaders({
        Authorization: `Bearer ${token}`
      });
      // Pass the headers as an options object to the put method
      return this.http.put<void>(`${this.baseUrl}/api/users/deactivateCurrent`, {}, { headers });
    }

    getUserActiveStatus(userId: number): Observable<boolean> {
      return this.http.get<boolean>(`/api/users/${userId}/active`);
    }
    deactivateInactiveUsers(daysInactive: number): Observable<void> {
      return this.http.put<void>(`/api/users/deactivate-inactive?daysInactive=${daysInactive}`, {});
    }

    changePassword(request: ChangePasswordRequest): Observable<void> {
      const token = localStorage.getItem('accessToken');
      const headers = new HttpHeaders({
        'Content-Type': 'application/json',
        Authorization: `Bearer ${token}`
      });
      return this.http.patch<void>(`${this.baseUrl}/api/users/changePassword`, request, {headers});
    }
    forgotpassword(email: string, request: ChangePasswordRequest): Observable<void> {
      return this.http.put<void>(`${this.baseUrl}/api/users/${email}/changePassword`, request);

    }


  }
