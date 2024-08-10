import { Component } from '@angular/core';
import {Router} from "@angular/router";
import {TokenService} from "../../services/token/token.service";

@Component({
  selector: 'app-member-footer',
  templateUrl: './member-footer.component.html',
  styleUrls: ['./member-footer.component.css']
})
export class MemberFooterComponent {


  constructor(private router: Router, private tokenService: TokenService) {}

  redirectToDashboard(): void {
    if (this.tokenService.isAdmin()) {
      this.router.navigateByUrl('/admin');
    } else if (this.tokenService.isCandidate()) {
      this.router.navigateByUrl('/candidate');
    } else if (this.tokenService.isInstructor()) {
      this.router.navigateByUrl('/instructor');
    } else {
      // Redirect to a default dashboard or handle the case where the user has no role
      this.router.navigateByUrl('/');
    }
  }

  /*
   counters: { value: number, postfix: string, title: string }[] = [

   { value: 97, postfix: '%', title: 'Graduates' },

   { value: 650, postfix: '', title: 'Students' }
 ];

   */
}
