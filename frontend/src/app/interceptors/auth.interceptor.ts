import {HttpErrorResponse, HttpInterceptorFn} from '@angular/common/http';
import {catchError, of, retry, timer} from "rxjs";
import {ErrorDTO} from "../models/dto";
import {AuthService} from "../services/auth.service";
import {inject} from "@angular/core";
import {Router} from "@angular/router";

/** Inject JWT Token in http request headers. */
export const authInterceptor: HttpInterceptorFn = (req, next) => {
  if(localStorage.getItem('token')) {
    req = req.clone({
      setHeaders: {
        Authorization: localStorage.getItem('token')!
      }
    });
  }
  return next(req);
};

/** Redirect to sign-in when http error 401 unauthorized */
export const unauthorizedInterceptor: HttpInterceptorFn = (req, next) => {
  const router = inject(Router);
  const auth = inject(AuthService);
  return next(req).pipe(
    retry({count: 1, delay: (error: HttpErrorResponse) => {
      if(error.status === 401 && (error.error as ErrorDTO).error !== 'bad_credentials') {
        localStorage.removeItem('token');
        return timer(0);
      }
      throw error;
    }}),
    catchError((error: HttpErrorResponse) => {
      if(error.status === 401 && (error.error as ErrorDTO).error !== 'bad_credentials') {
        localStorage.removeItem('token');
        auth.redirectionUrl = router.url;
        auth.authenticatedUser.next(null);
        auth.redirectTo('/redirect');
        auth.authDialogFormType.next('sign_in');
        return of();
      }
      throw error;
    }),
  );
};

