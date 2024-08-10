import { Injectable } from '@angular/core';
import { environment } from "../../../environments/environment";
import { HttpClient, HttpHeaders } from "@angular/common/http";
import { ContactRequest } from "../../models/contact/contact-request";
import { catchError } from "rxjs/operators";
import { throwError } from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class ContactService {
  baseUrl = environment.app.baseUrl;

  constructor(private http: HttpClient) { }

  sendMessage(contactData: ContactRequest) {
    const url = `${this.baseUrl}/api/contacts/send-email`;
    const headers = new HttpHeaders({
      'Content-Type': 'application/json',
      'Accept': 'text/plain' // Specify that we expect plain text response
    });

    return this.http.post<any>(url, contactData, { headers, responseType: 'text' as 'json' })
      .pipe(
        catchError(error => {
          console.error('Error sending message:', error);
          return throwError(error);
        })
      );
  }
}
