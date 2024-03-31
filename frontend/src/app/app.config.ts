import { ApplicationConfig } from '@angular/core';
import { provideRouter } from '@angular/router';
import { routes } from './app.routes';
import {provideAnimations} from "@angular/platform-browser/animations";
import {provideHttpClient, withInterceptors} from "@angular/common/http";
import {authInterceptor, unauthorizedInterceptor} from "./interceptors/auth.interceptor";
import {ConfirmationService, MessageService} from "primeng/api";

export const appConfig: ApplicationConfig = {
  providers: [
    provideRouter(routes),
    provideHttpClient(withInterceptors([unauthorizedInterceptor, authInterceptor])),
    provideAnimations(),
    MessageService,
    ConfirmationService,
  ],
};
