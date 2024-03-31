import {Component, EventEmitter, Input, Output} from '@angular/core';
import {DialogModule} from "primeng/dialog";
import {FormSignInComponent} from "./form-sign-in/form-sign-in.component";
import {FormSignUpComponent} from "./form-sign-up/form-sign-up.component";
import {FormPasswordChangeComponent} from "./form-password-change/form-password-change.component";

export type AuthFormType = 'sign_in'|'sign_up'|'pw_change'|null;

@Component({
  selector: 'app-dialog-auth',
  templateUrl: './dialog-auth.component.html',
  styleUrl: './dialog-auth.component.scss',
  standalone: true,
  imports: [
    DialogModule,
    FormSignInComponent,
    FormSignUpComponent,
    FormPasswordChangeComponent
  ]
})
export class DialogAuthComponent {
  @Input() public formType: AuthFormType = null;
  @Output() protected readonly formTypeChange = new EventEmitter<AuthFormType>();

  protected get formHeader() {
    switch(this.formType) {
      case 'sign_in': return 'Sign in';
      case 'sign_up': return 'Sign up';
      case 'pw_change': return 'New password';
      case null: return '';
    }
  }

  protected onVisibilityChange($event: boolean) {
    if(!$event) {
      this.formType = null;
      this.formTypeChange.emit(null);
    }
  }

  protected onFormTypeChange($event: AuthFormType) {
    this.formTypeChange.emit($event);
  }
}
