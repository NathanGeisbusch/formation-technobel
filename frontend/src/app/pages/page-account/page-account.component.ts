import { Component } from '@angular/core';
import {CLASS_INVALID_FORM, getAllFormErrors} from "../../utils/form-validation";
import {
  AbstractControl,
  FormControl,
  FormGroup,
  ReactiveFormsModule,
  ValidationErrors,
  Validators
} from "@angular/forms";
import {AuthService} from "../../services/auth.service";
import {AppMessageService, MsgReasonSuccess} from "../../services/app.message.service";
import {REGEX_NAME} from "../../utils/package-validation";
import {SearchService} from "../../services/search.service";
import {map, Observable, of} from "rxjs";
import {AccountForm} from "../../models/form";
import {NgClass} from "@angular/common";
import {CardModule} from "primeng/card";
import {InputGroupModule} from "primeng/inputgroup";
import {InputGroupAddonModule} from "primeng/inputgroupaddon";
import {InputTextModule} from "primeng/inputtext";
import {DividerModule} from "primeng/divider";
import {PasswordModule} from "primeng/password";
import {ButtonModule} from "primeng/button";
import {AccountDTO} from "../../models/dto";

@Component({
  selector: 'app-page-account',
  templateUrl: './page-account.component.html',
  styleUrl: './page-account.component.scss',
  standalone: true,
  imports: [
    ReactiveFormsModule,
    NgClass,
    CardModule,
    InputGroupModule,
    InputGroupAddonModule,
    InputTextModule,
    DividerModule,
    PasswordModule,
    ButtonModule
  ]
})
export class PageAccountComponent {
  protected readonly invalidClass = CLASS_INVALID_FORM;
  protected readonly form: FormGroup;
  protected showInvalid: boolean = false;
  private account?: AccountDTO;

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
      password: new FormControl('', null),
    });
    this._authService.account().subscribe({
      next: (value) => this.form.patchValue(value)
    });
  }

  protected onSubmit() {
    if(this.form.invalid) {
      this.showInvalid = true;
      this.showValidationErrors();
    } else {
      const form: AccountForm = this.form.value;
      if(!form.password) form.password = undefined;
      this._authService.updateAccount(form).subscribe({
        next: (value) => {
          this._messageService.showSuccess(MsgReasonSuccess.ACCOUNT_UPDATE);
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
  }

  protected existsPseudonym(control: AbstractControl): Observable<ValidationErrors|null> {
    if(!this.account || this.account.pseudonym === control.value) return of(null);
    return this._searchService.existsPseudonym(control.value).pipe(
      map(exists => exists ? {existsPseudonym: true} : null)
    );
  }

  protected existsEmail(control: AbstractControl): Observable<ValidationErrors|null> {
    if(!this.account || this.account.email === control.value) return of(null);
    return this._searchService.existsEmail(control.value).pipe(
      map(exists => exists ? {existsEmail: true} : null)
    );
  }
}
