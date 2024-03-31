import { Injectable } from '@angular/core';
import {BehaviorSubject, tap} from "rxjs";
import {HttpClient} from "@angular/common/http";
import {AccountForm, ChangePasswordForm, LoginForm, RegisterForm, RequestPasswordForm} from "../models/form";
import {AccountDTO, AuthDTO, CreatedDTO} from "../models/dto";
import {environment} from "../../environments/environment";
import {AuthFormType} from "../components/dialog-auth/dialog-auth.component";
import {Router} from "@angular/router";

interface TokenPayload {sub: string, role: string}

export interface AuthenticatedUser {login: string, role: string, token: string}

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  /** The currently authenticated user. */
  public readonly authenticatedUser = new BehaviorSubject<AuthenticatedUser|null>(null);

  /** The current form type of authentication dialog. */
  public readonly authDialogFormType = new BehaviorSubject<AuthFormType|null>(null);

  /** The URL to redirect on next sign-in. */
  public redirectionUrl: string|null = null;

  public constructor(
    private readonly _httpClient: HttpClient,
    private readonly _router: Router,
  ) {
    const token = localStorage.getItem("token");
    if(token) this.authenticatedUser.next(this.extractToken(token));
  }

  public signIn(form: LoginForm) {
    return this._httpClient.post<AuthDTO>(`${environment.apiUrl}/auth/sign-in`, form).pipe(
      tap( data => {
        localStorage.setItem("token", data.token);
        localStorage.setItem("login", form.login);
        localStorage.setItem("password", form.password);
        const user = this.extractToken(data.token);
        this.authenticatedUser.next(user);
        if(this.redirectionUrl) this.redirectTo(this.redirectionUrl);
      })
    );
  }

  public signOut() {
    return this._httpClient.post<void>(`${environment.apiUrl}/auth/sign-out`, null).pipe(
      tap( () => {
        localStorage.removeItem("token");
        localStorage.removeItem("login");
        localStorage.removeItem("password");
        this.authenticatedUser.next(null);
      })
    );
  }

  public signUp(form: RegisterForm) {
    return this._httpClient.post<CreatedDTO>(`${environment.apiUrl}/auth/sign-up`, form).pipe(
      tap( () => {
        this.signIn({login: form.email, password: form.password}).subscribe();
      })
    );
  }

  public requestPassword(form: RequestPasswordForm) {
    return this._httpClient.post<void>(`${environment.apiUrl}/auth/password`, form);
  }

  public changePassword(token: string, form: ChangePasswordForm) {
    return this._httpClient.post<void>(`${environment.apiUrl}/auth/password/${token}`, form);
  }

  public canChangePassword(token: string) {
    return this._httpClient.get<boolean>(`${environment.apiUrl}/auth/password/${token}`);
  }

  public account() {
    return this._httpClient.get<AccountDTO>(`${environment.apiUrl}/auth/account`);
  }

  public updateAccount(form: AccountForm) {
    return this._httpClient.patch<void>(`${environment.apiUrl}/auth/account`, form).pipe(
      tap( () => {
        const password = form.password || localStorage.getItem('password');
        if(password) this.signIn({login: form.email, password}).subscribe();
      })
    );
  }

  /** Force redirection to an url path */
  public redirectTo(url: string) {
    this._router.navigateByUrl('/redirect', { skipLocationChange: true }).then(() =>
      this._router.navigate([url]).then()
    );
  }

  /** Extract user from a bearer token. */
  private extractToken(token: string): AuthenticatedUser|null {
    try {
      const payload: TokenPayload = JSON.parse(atob(token.split('Bearer ')[1].split('.')[1]));
      return {login: payload.sub, role: payload.role, token};
    } catch(err) {
      return null;
    }
  }
}
