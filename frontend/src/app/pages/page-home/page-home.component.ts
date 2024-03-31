import {Component, OnDestroy} from '@angular/core';
import {AuthenticatedUser, AuthService} from "../../services/auth.service";
import {PanelModule} from "primeng/panel";
import {ButtonModule} from "primeng/button";
import {RouterLink} from "@angular/router";
import {Subscription} from "rxjs";
import {
  DialogCreateGeneratorComponent
} from "../../components/dialog-create-generator/dialog-create-generator.component";
import {NgIf} from "@angular/common";

@Component({
  selector: 'app-page-home',
  templateUrl: './page-home.component.html',
  styleUrl: './page-home.component.scss',
  standalone: true,
  imports: [
    PanelModule,
    ButtonModule,
    RouterLink,
    DialogCreateGeneratorComponent,
    NgIf
  ]
})
export class PageHomeComponent implements OnDestroy {
  protected user: AuthenticatedUser|null = null;
  private readonly _user$?: Subscription;
  protected showDialogCreateGenerator: boolean = false;

  public constructor(private readonly _authService: AuthService) {
    this._user$ = this._authService.authenticatedUser.subscribe((value) => {
      this.user = value;
    });
  }
  public ngOnDestroy() {
    if(this._user$) this._user$.unsubscribe();
  }

  protected signIn() {
    this._authService.authDialogFormType.next('sign_in');
  }

  protected signUp() {
    this._authService.authDialogFormType.next('sign_up');
  }
}
