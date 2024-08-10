  import {Injectable} from '@angular/core';
  import {
    HttpRequest,
    HttpHandler,
    HttpEvent,
    HttpInterceptor
  } from '@angular/common/http';
  import {Observable} from 'rxjs';
  import {TokenService} from '../token/token.service';

  @Injectable()
  export class HttpTokenInterceptor implements HttpInterceptor {

    constructor(
      private tokenService: TokenService
    ) {
    }

    intercept(request: HttpRequest<unknown>, next: HttpHandler): Observable<HttpEvent<unknown>> {
      const token = this.tokenService.accessToken;
      // Check if the request URL is for /api/topics/titles
      if (request.url.includes('/api/topics/titles') || request.url.includes('/api/topics/search') || request.url.includes('/api/topics') || request.url.includes('/api/profiles/instructors') || request.url.includes('/api/courses/findAll') || request.url.includes('/api/contacts/send-email')|| request.url.includes('/api/courses/by-topic') || request.url.includes('/api/users') ||    request.url.includes('/api/users/check-email') ||  request.url.includes('api/users/current-user') )  {
        // No need for authorization, so just pass the request through
        return next.handle(request);
      }
      // For other requests, add the authorization header if token is present
      if (token) {
        const authReq = request.clone({
          headers: request.headers.set('Authorization', 'Bearer ' + token)
        });
        return next.handle(authReq);
      } else {
        return next.handle(request);
      }
    }
  }
