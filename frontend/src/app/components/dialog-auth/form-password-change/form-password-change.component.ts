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
import {AuthFormType} from "../dialog-auth.component";
import {HttpErrorResponse} from "@angular/common/http";

@Component({
  selector: 'app-form-password-change',
  templateUrl: './form-password-change.component.html',
  styleUrl: './form-password-change.component.scss',
  standalone: true,
  imports: [
    ReactiveFormsModule,
    NgClass,
    InputTextModule,
    ButtonModule,
    InputGroupModule,
    InputGroupAddonModule
  ]
})
export class FormPasswordChangeComponent {
  @Output() protected readonly formTypeChange = new EventEmitter<AuthFormType>();
  protected readonly invalidClass = CLASS_INVALID_FORM;
  protected readonly form: FormGroup;
  protected showInvalid: boolean = false;

  public constructor(
    private readonly _authService: AuthService,
    private readonly _messageService: AppMessageService,
  ) {
    this.form = new FormGroup({
      email: new FormControl('', Validators.required),
    });
  }

  protected onSubmit() {
    if(this.form.invalid) {
      this.showInvalid = true;
      this.showValidationErrors();
    } else {
      this._authService.requestPassword(this.form.value).subscribe({
        next: (value) => {
          this._messageService.showSuccess(MsgReasonSuccess.PW_CHANGE_REQ);
          this.formTypeChange.emit(null);
        },
        error: (err: HttpErrorResponse) => {
          if(err.status === 500) this._messageService.showError(MsgReasonError.EMAIL_ERROR);
        }
      });

    }
  }

  protected showValidationErrors() {
    const errors = getAllFormErrors(this.form);
    if(errors.email?.required) {
      this._messageService.showValidationFailedRequired('email');
    }
  }

  protected signIn() {
    this.formTypeChange.emit('sign_in');
  }
}
