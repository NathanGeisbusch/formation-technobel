import {Component, EventEmitter, Output} from '@angular/core';
import {CLASS_INVALID_FORM, getAllFormErrors} from "../../../utils/form-validation";
import {FormControl, FormGroup, ReactiveFormsModule, Validators} from "@angular/forms";
import {AuthService} from "../../../services/auth.service";
import {AppMessageService, MsgReasonError, MsgReasonSuccess} from "../../../services/app.message.service";
import {NgClass} from "@angular/common";
import {InputTextModule} from "primeng/inputtext";
import {ButtonModule} from "primeng/button";
import {InputGroupModule} from "primeng/inputgroup";
import {InputGroupAddonModule} from "primeng/inputgroupaddon";
import {PasswordModule} from "primeng/password";
import {AuthFormType} from "../dialog-auth.component";
import {REGEX_NAME} from "../../../utils/package-validation";

@Component({
  selector: 'app-form-sign-in',
  templateUrl: './form-sign-in.component.html',
  styleUrl: './form-sign-in.component.scss',
  standalone: true,
  imports: [
    ReactiveFormsModule,
    NgClass,
    InputTextModule,
    ButtonModule,
    InputGroupModule,
    InputGroupAddonModule,
    PasswordModule
  ]
})
export class FormSignInComponent {
  @Output() protected readonly formTypeChange = new EventEmitter<AuthFormType>();
  protected readonly invalidClass = CLASS_INVALID_FORM;
  protected readonly form: FormGroup;
  protected showInvalid: boolean = false;

  public constructor(
    private readonly _authService: AuthService,
    private readonly _messageService: AppMessageService,
  ) {
    this.form = new FormGroup({
      login: new FormControl('', [Validators.required]),
      password: new FormControl('', Validators.required),
    });
  }

  protected onSubmit() {
    if(this.form.invalid) {
      this.showInvalid = true;
      this.showValidationErrors();
    } else {
      this._authService.signIn(this.form.value).subscribe({
        next: (value) => {
          this._messageService.showSuccess(MsgReasonSuccess.SIGN_IN);
          this.formTypeChange.emit(null)
        },
        error: (err) => this._messageService.showError(MsgReasonError.BAD_CREDENTIALS),
      });
    }
  }

  protected showValidationErrors() {
    const errors = getAllFormErrors(this.form);
    if(errors.username?.required) {
      this._messageService.showValidationFailedRequired('username');
    }
    if(errors.username?.pattern) {
      this._messageService.showValidationFailedPattern('username', this.form.value.username);
    }
    if(errors.password?.required) {
      this._messageService.showValidationFailedRequired('password');
    }
  }

  protected passwordChange() {
    this.formTypeChange.emit('pw_change');
  }

  protected signUp() {
    this.formTypeChange.emit('sign_up');
  }
}
