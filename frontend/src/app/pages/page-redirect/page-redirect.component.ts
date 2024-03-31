import {Component, OnDestroy} from '@angular/core';
import {AuthService} from "../../services/auth.service";

@Component({
  selector: 'app-page-redirect',
  templateUrl: './page-redirect.component.html',
  styleUrl: './page-redirect.component.scss',
  standalone: true,
})
export class PageRedirectComponent implements OnDestroy {
  public constructor(private readonly _authService: AuthService) {}

  public ngOnDestroy() {
    this._authService.redirectionUrl = null;
  }
}
