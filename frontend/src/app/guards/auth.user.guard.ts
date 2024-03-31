import {CanActivateFn, Router} from '@angular/router';
import {inject} from "@angular/core";
import {AuthService} from "../services/auth.service";
import {Role} from "../models/enum";

/** Redirects to sign-in when user not authenticated. */
export const authUserGuard: CanActivateFn = (route, state) => {
  const auth = inject(AuthService);
  const user = auth.authenticatedUser.value;
  if(user !== null && user.role === Role.USER) return true;
  else {
    const router = inject(Router);
    auth.redirectionUrl = route.url.join('/');
    auth.authDialogFormType.next('sign_in');
    return router.parseUrl('redirect');
  }
};
