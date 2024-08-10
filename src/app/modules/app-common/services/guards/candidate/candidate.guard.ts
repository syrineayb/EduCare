import {CanActivateFn, Router} from '@angular/router';
import {inject} from "@angular/core";
import {TokenService} from "../../token/token.service";

export const candidateGuard: CanActivateFn = (route, state) => {
  const tokenService = inject(TokenService);
  const router = inject(Router);
  if (tokenService.isNotCandidate()) {
    router.navigate(['login']); // Assuming 'login' is the route for authentication
    return false;
  }
  return true;
};
