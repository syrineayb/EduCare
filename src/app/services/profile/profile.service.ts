import { Injectable } from '@angular/core';
import {environment} from "../../../environments/environment";
import {HttpClient, HttpEvent, HttpHeaders, HttpRequest} from "@angular/common/http";
import {AuthenticationService} from "../auth/authentication.service";
import {Observable} from "rxjs";
import {ProfileResponse} from "../../models/profile/profile-response";

@Injectable({
  providedIn: 'root'
})
export class ProfileService {
  baseUrl = environment.app.baseUrl;

  constructor(private http: HttpClient,private authService: AuthenticationService) { }

  getCurrentUserProfile(): Observable<ProfileResponse> {
    // Assuming you're passing the access token as a bearer token in the Authorization header
    //const token = localStorage.getItem('accessToken');
    //const headers = new HttpHeaders({
      //'Content-Type': 'application/json',
      //Authorization: `Bearer ${token}`
    //});

    return this.http.get<ProfileResponse>(`${this.baseUrl}/api/profiles/me`);
  }

  // Assuming this method is to update the user profile
  updateUserProfile(profileRequest: ProfileResponse): Observable<ProfileResponse> {
    // Assuming you're passing the access token as a bearer token in the Authorization header
    const token = localStorage.getItem('accessToken');
    const headers = new HttpHeaders({
      'Content-Type': 'application/json',
      Authorization: `Bearer ${token}`
    });

    return this.http.put<ProfileResponse>(`${this.baseUrl}/api/profiles/update`, profileRequest, { headers });
  }
  uploadProfileImage(userId: number, file: File): Observable<HttpEvent<any>> {
    const token = localStorage.getItem('accessToken');
    const headers = new HttpHeaders({
      Authorization: `Bearer ${token}`
    });

    const formData: FormData = new FormData();
    formData.append('id', userId.toString()); // Convert userId to string before appending
    formData.append('file', file);

    const req = new HttpRequest('POST', `${this.baseUrl}/api/files/upload`, formData, {
      reportProgress: true,
      responseType: 'json',
      headers: headers // Corrected placement of headers
    });

    return this.http.request(req);
  }


  getCurrentUserId(): Observable<number> {
    return this.http.get<number>(`${this.baseUrl}/api/profiles/profileId`);
  }
  getFiles(): Observable<any> {
    return this.http.get(`${this.baseUrl}/files`);
  }

}
