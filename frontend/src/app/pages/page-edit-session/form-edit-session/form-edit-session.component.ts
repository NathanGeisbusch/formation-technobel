import {Component, Input} from '@angular/core';
import {
  AbstractControl,
  FormControl,
  FormGroup,
  ReactiveFormsModule,
  ValidationErrors,
  Validators
} from "@angular/forms";
import {InputTextModule} from "primeng/inputtext";
import {NgClass, NgIf} from "@angular/common";
import {MessageComponent} from "../../../components/message/message.component";
import {CLASS_INVALID_FORM, getAllFormErrors} from "../../../utils/form-validation";
import {SessionEditForm} from "../../../models/form";
import {SearchService} from "../../../services/search.service";
import {AppMessageService} from "../../../services/app.message.service";
import {REGEX_NAME} from "../../../utils/package-validation";
import {Message} from "primeng/api";
import {map, Observable, of} from "rxjs";

@Component({
  selector: 'app-form-edit-session',
  templateUrl: './form-edit-session.component.html',
  styleUrl: './form-edit-session.component.scss',
  standalone: true,
  imports: [
    ReactiveFormsModule,
    InputTextModule,
    NgClass,
    NgIf,
    MessageComponent,
  ]
})
export class FormEditSessionComponent {
  @Input({required:true}) public id!: string;
  protected readonly form: FormGroup;
  protected readonly invalidClass = CLASS_INVALID_FORM;
  protected showInvalid: boolean = false;
  protected nameAlreadyExists: boolean = false;
  private readonly cache: SessionEditForm = {};

  constructor(
    private readonly _searchService: SearchService,
    private readonly _msgService: AppMessageService,
  ) {
    this.form = new FormGroup({
      name: new FormControl(
        '', [Validators.required, Validators.pattern(REGEX_NAME)],
        this.existsSession.bind(this),
      ),
    });
  }

  public validate(): SessionEditForm|null {
    if(this.form.invalid) {
      this.showInvalid = true;
      this.showValidationErrors();
      return null;
    } else {
      const { name } = this.form.value;
      const form: SessionEditForm = {};
      if(this.cache.name !== name) form.name = name;
      return form;
    }
  }

  public set values(values: SessionEditForm) {
    Object.assign(this.cache, values);
    this.form.patchValue(values);
  }

  protected get errorMessage(): Message|null {
    if(!this.nameAlreadyExists) return null;
    return {
      severity: 'error',
      detail: 'A session with the same name already exists in your collection.',
    };
  }

  protected showValidationErrors() {
    const errors = getAllFormErrors(this.form);
    if(errors.name?.required) {
      this._msgService.showValidationFailedRequired('name');
    }
    if(errors.name?.pattern) {
      this._msgService.showValidationFailedPattern('name', this.form.value.name);
    }
  }

  protected existsSession(control: AbstractControl): Observable<ValidationErrors|null> {
    if(this.id === control.value) return of(null);
    return this._searchService.existsSession(control.value).pipe(
      map(exists => {
        this.nameAlreadyExists = exists;
        return exists ? {existsSession: true} : null
      })
    );
  }
}
