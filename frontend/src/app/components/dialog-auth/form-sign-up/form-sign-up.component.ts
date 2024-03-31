import {Component, EventEmitter, Output} from '@angular/core';
import {
  AbstractControl,
  FormControl,
  FormGroup,
  ReactiveFormsModule,
  ValidationErrors,
  Validators
} from "@angular/forms";
import {NgClass} from "@angular/common";
import {InputTextModule} from "primeng/inputtext";
import {ButtonModule} from "primeng/button";
import {InputGroupModule} from "primeng/inputgroup";
import {InputGroupAddonModule} from "primeng/inputgroupaddon";
import {PasswordModule} from "primeng/password";
import {CLASS_INVALID_FORM, getAllFormErrors} from "../../../utils/form-validation";
import {AppMessageService, MsgReasonSuccess} from "../../../services/app.message.service";
import {AuthFormType} from "../dialog-auth.component";
import {REGEX_NAME} from "../../../utils/package-validation";
import {AuthService} from "../../../services/auth.service";
import {map, Observable} from "rxjs";
import {SearchService} from "../../../services/search.service";

@Component({
  selector: 'app-form-sign-up',
  templateUrl: './form-sign-up.component.html',
  styleUrl: './form-sign-up.component.scss',
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
export class FormSignUpComponent {
  @Output() protected readonly formTypeChange = new EventEmitter<AuthFormType>();
  protected readonly invalidClass = CLASS_INVALID_FORM;
  protected readonly form: FormGroup;
  protected showInvalid: boolean = false;

  public constructor(
    private readonly _authService: AuthService,
    private readonly _searchService: SearchService,
    private readonly _messageService: AppMessageService,
  ) {
    this.form = new FormGroup({
      pseudonym: new FormControl(
        '', [Validators.required, Validators.pattern(REGEX_NAME)],
        this.existsPseudonym.bind(this)
      ),
      email: new FormControl(
        '', [Validators.required, Validators.email],
        this.existsEmail.bind(this)
      ),
      password: new FormControl('', Validators.required),
    });
  }

  protected onSubmit() {
    if(this.form.invalid) {
      this.showInvalid = true;
      this.showValidationErrors();
    } else {
      this._authService.signUp(this.form.value).subscribe({
        next: (value) => {
          this._messageService.showSuccess(MsgReasonSuccess.SIGN_UP);
          this.formTypeChange.emit(null);
        },
      });
    }
  }

  protected showValidationErrors() {
    const errors = getAllFormErrors(this.form);
    if(errors.pseudonym?.required) {
      this._messageService.showValidationFailedRequired('username');
    }
    if(errors.pseudonym?.pattern) {
      this._messageService.showValidationFailedPattern('username', this.form.value.pseudonym);
    }
    if(errors.pseudonym?.existsPseudonym) {
      this._messageService.showValidationFailedAlreadyExists('username', this.form.value.pseudonym);
    }
    if(errors.email?.required) {
      this._messageService.showValidationFailedRequired('email');
    }
    if(errors.email?.email) {
      this._messageService.showValidationFailedPattern('email', this.form.value.email);
    }
    if(errors.email?.existsEmail) {
      this._messageService.showValidationFailedAlreadyExists('email', this.form.value.email);
    }
    if(errors.password?.required) {
      this._messageService.showValidationFailedRequired('password');
    }
  }

  protected existsPseudonym(control: AbstractControl): Observable<ValidationErrors|null> {
    return this._searchService.existsPseudonym(control.value).pipe(
      map(exists => exists ? {existsPseudonym: true} : null)
    );
  }

  protected existsEmail(control: AbstractControl): Observable<ValidationErrors|null> {
    return this._searchService.existsEmail(control.value).pipe(
      map(exists => exists ? {existsEmail: true} : null)
    );
  }

  protected signIn() {
    this.formTypeChange.emit('sign_in');
  }
}
