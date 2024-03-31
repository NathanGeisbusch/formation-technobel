import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {fromPackageId, PackageId, REGEX_NAME} from "../../utils/package-validation";
import {
  AbstractControl,
  FormControl,
  FormGroup,
  ReactiveFormsModule,
  ValidationErrors,
  Validators
} from "@angular/forms";
import {CLASS_INVALID_FORM, getAllFormErrors, hasAnyFormErrors} from "../../utils/form-validation";
import {SearchService} from "../../services/search.service";
import {AppMessageService, MsgReasonSuccess} from "../../services/app.message.service";
import {Message} from "primeng/api";
import {SessionCreateForm} from "../../models/form";
import {map, Observable} from "rxjs";
import {DropdownModule} from "primeng/dropdown";
import {DialogModule} from "primeng/dialog";
import {InputTextModule} from "primeng/inputtext";
import {NgClass, NgIf} from "@angular/common";
import {ButtonModule} from "primeng/button";
import {MessageComponent} from "../message/message.component";
import {SessionService} from "../../services/session.service";

@Component({
  selector: 'app-dialog-create-session',
  templateUrl: './dialog-create-session.component.html',
  styleUrl: './dialog-create-session.component.scss',
  standalone: true,
  imports: [
    DialogModule,
    InputTextModule,
    DropdownModule,
    ReactiveFormsModule,
    NgIf,
    ButtonModule,
    MessageComponent,
    NgClass
  ]
})
export class DialogCreateSessionComponent implements OnInit {
  @Input({required:true}) public fromGenerator!: PackageId;
  @Input({required:true}) public visible: boolean = false;
  @Output() protected readonly visibleChange = new EventEmitter<boolean>();
  @Output() protected readonly save = new EventEmitter<string>();
  protected readonly form: FormGroup;
  protected readonly invalidClass = CLASS_INVALID_FORM;
  protected showInvalid: boolean = false;
  protected nameAlreadyExists: boolean = false;

  public constructor(
    private readonly _sessionService: SessionService,
    private readonly _searchService: SearchService,
    private readonly _messageService: AppMessageService,
  ) {
    this.form = new FormGroup({
      name: new FormControl(
        '', [Validators.required, Validators.pattern(REGEX_NAME)],
        this.existsSession.bind(this),
      ),
      sourceName: new FormControl(''),
      sourceVersion: new FormControl(''),
    });
    this.form.get('sourceName')!.disable();
    this.form.get('sourceVersion')!.disable();
  }

  public ngOnInit() {
    if(this.fromGenerator) {
      this.form.patchValue({
        sourceName: this.fromGenerator.name,
        sourceVersion: this.fromGenerator.version,
      });
    }
  }

  protected get errorMessage(): Message|null {
    if(!this.nameAlreadyExists) return null;
    return {
      severity: 'error',
      detail: 'A session with the same name already exists in your collection.',
    };
  }

  protected onVisibilityChange($event: boolean) {
    this.visibleChange.emit($event);
  }

  protected onSubmit() {
    if(this.form.invalid) {
      this.showInvalid = true;
      this.showValidationErrors();
    } else {
      const id = fromPackageId(this.fromGenerator);
      const name = this.form.value.name;
      const form: SessionCreateForm = {name, from: id};
      this._sessionService.create(form).subscribe({
        next: () => {
          this._messageService.showSuccess(MsgReasonSuccess.SESSION_CREATE);
          this.visibleChange.emit(false);
          this.save.emit(name);
        },
      });
    }
  }

  protected showValidationErrors() {
    const errors = getAllFormErrors(this.form);
    if(errors.name?.required) {
      this._messageService.showValidationFailedRequired('name');
    }
    if(errors.name?.pattern) {
      this._messageService.showValidationFailedPattern('name', this.form.value.name);
    }
  }

  protected existsSession(control: AbstractControl): Observable<ValidationErrors|null> {
    return this._searchService.existsSession(control.value).pipe(
      map(exists => {
        this.nameAlreadyExists = exists;
        return exists ? {existsSession: true} : null
      })
    );
  }

  protected get hasAnyFormErrors(): boolean {
    return hasAnyFormErrors(this.form);
  }
}
