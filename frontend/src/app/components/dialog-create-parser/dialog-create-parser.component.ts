import {Component, EventEmitter, Input, Output} from '@angular/core';
import {
  AbstractControl,
  FormControl,
  FormGroup,
  ReactiveFormsModule, ValidationErrors,
  Validators
} from "@angular/forms";
import {AppMessageService, MsgReasonSuccess} from "../../services/app.message.service";
import {REGEX_NAME} from "../../utils/package-validation";
import {Message} from "primeng/api";
import {CLASS_INVALID_FORM, getAllFormErrors, hasAnyFormErrors} from "../../utils/form-validation";
import {DropdownModule} from "primeng/dropdown";
import {DialogModule} from "primeng/dialog";
import {InputTextModule} from "primeng/inputtext";
import {NgClass, NgIf} from "@angular/common";
import {ButtonModule} from "primeng/button";
import {MessageComponent} from "../message/message.component";
import {ParserService} from "../../services/parser.service";
import {SearchService} from "../../services/search.service";
import {map, Observable} from "rxjs";

@Component({
  selector: 'app-dialog-create-parser',
  templateUrl: './dialog-create-parser.component.html',
  styleUrl: './dialog-create-parser.component.scss',
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
export class DialogCreateParserComponent {
  @Input() public visible: boolean = false;
  @Output() protected readonly visibleChange = new EventEmitter<boolean>();
  @Output() protected readonly save = new EventEmitter<void>();
  protected readonly form: FormGroup;
  protected readonly invalidClass = CLASS_INVALID_FORM;
  protected showInvalid: boolean = false;
  protected nameAlreadyExists: boolean = false;

  public constructor(
    private readonly _parserService: ParserService,
    private readonly _searchService: SearchService,
    private readonly _messageService: AppMessageService,
  ) {
    this.form = new FormGroup({
      name: new FormControl(
        '', [Validators.required, Validators.pattern(REGEX_NAME)],
        this.existsParser.bind(this),
      )
    });
  }

  protected get errorMessage(): Message|null {
    if(!this.nameAlreadyExists) return null;
    return {
      severity: 'error',
      detail: 'A parser with the same name already exists in your collection.',
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
      this._parserService.create(this.form.value).subscribe({
        next: () => {
          this._messageService.showSuccess(MsgReasonSuccess.PARSER_CREATE);
          this.visibleChange.emit(false);
          this.save.emit();
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

  protected existsParser(control: AbstractControl): Observable<ValidationErrors|null> {
    return this._searchService.existsParserByName(control.value).pipe(
      map(exists => {
        this.nameAlreadyExists = exists;
        return exists ? {existsParser: true} : null
      })
    );
  }

  protected get hasAnyFormErrors(): boolean {
    return hasAnyFormErrors(this.form);
  }
}
