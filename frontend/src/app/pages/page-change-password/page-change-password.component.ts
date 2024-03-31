import { Component } from '@angular/core';
import {CLASS_INVALID_FORM, getAllFormErrors} from "../../utils/form-validation";
import {FormControl, FormGroup, ReactiveFormsModule, Validators} from "@angular/forms";
import {ActivatedRoute, Router} from "@angular/router";
import {AppMessageService, MsgReasonSuccess} from "../../services/app.message.service";
import {CardModule} from "primeng/card";
import {InputGroupModule} from "primeng/inputgroup";
import {InputGroupAddonModule} from "primeng/inputgroupaddon";
import {PasswordModule} from "primeng/password";
import {ButtonModule} from "primeng/button";
import {AuthService} from "../../services/auth.service";
import {NgIf} from "@angular/common";

@Component({
  selector: 'app-page-change-password',
  templateUrl: './page-change-password.component.html',
  styleUrl: './page-change-password.component.scss',
  standalone: true,
  imports: [
    ReactiveFormsModule,
    CardModule,
    InputGroupModule,
    InputGroupAddonModule,
    PasswordModule,
    ButtonModule,
    NgIf
  ]
})
export class PageChangePasswordComponent {
  protected readonly invalidClass = CLASS_INVALID_FORM;
  protected readonly form: FormGroup;
  protected showInvalid: boolean = false;
  protected isTokenValid: boolean = false;
  private readonly token: string;

  public constructor(
    private readonly _authService: AuthService,
    private readonly _activatedRoute: ActivatedRoute,
    private readonly _router: Router,
    private readonly _messageService: AppMessageService,
  ) {
    this.form = new FormGroup({
      password: new FormControl('', Validators.required),
    });
    this.token = this._activatedRoute.snapshot.params['token'];
    this._authService.canChangePassword(this.token).subscribe({
      next: (value) => {
        if(value) this.isTokenValid = true;
        else this._router.navigateByUrl('404', {skipLocationChange: true}).then();
      }
    });
  }

  protected onSubmit() {
    if(this.form.invalid) {
      this.showInvalid = true;
      this.showValidationErrors();
    } else {
      this._authService.changePassword(this.token, this.form.value).subscribe({
        next: (value) => this._messageService.showSuccess(MsgReasonSuccess.PW_CHANGE),
      });
    }
  }

  protected showValidationErrors() {
    const errors = getAllFormErrors(this.form);
    if(errors.password?.required) {
      this._messageService.showValidationFailedRequired('password');
    }
  }
}
