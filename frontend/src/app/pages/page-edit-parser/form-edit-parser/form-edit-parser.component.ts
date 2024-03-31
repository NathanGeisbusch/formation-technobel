import {Component, EventEmitter, Input, Output} from '@angular/core';
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
import {DropdownModule} from "primeng/dropdown";
import {InputGroupModule} from "primeng/inputgroup";
import {ButtonModule} from "primeng/button";
import {Parser} from "../../../models/syntax/parser";
import {CLASS_INVALID_FORM, getAllFormErrors} from "../../../utils/form-validation";
import {PackageVisibility, ParserSyntax} from "../../../models/enum";
import {ParserInfoForm} from "../../../models/form";
import {SearchService} from "../../../services/search.service";
import {SyntaxVersionService} from "../../../services/syntax.version.service";
import {AppMessageService, MsgReasonSuccess} from "../../../services/app.message.service";
import {REGEX_NAME, toPackageId} from "../../../utils/package-validation";
import {Message} from "primeng/api";
import {map, Observable, of} from "rxjs";
import {toBase64Url} from "../../../utils/base64";

@Component({
  selector: 'app-form-edit-parser',
  templateUrl: './form-edit-parser.component.html',
  styleUrl: './form-edit-parser.component.scss',
  standalone: true,
  imports: [
    ReactiveFormsModule,
    InputTextModule,
    NgClass,
    NgIf,
    MessageComponent,
    DropdownModule,
    InputGroupModule,
    ButtonModule
  ]
})
export class FormEditParserComponent {
  @Input({required:true}) public id!: string;
  @Output() protected readonly parser = new EventEmitter<Parser>();
  protected readonly form: FormGroup;
  protected readonly invalidClass = CLASS_INVALID_FORM;
  protected readonly syntaxOptions: string[];
  protected readonly visibilityOptions: string[] = [
    PackageVisibility.PRIVATE,
    PackageVisibility.PROTECTED,
    PackageVisibility.PUBLIC,
  ];
  protected showInvalid: boolean = false;
  protected nameAlreadyExists: boolean = false;
  private readonly cache: ParserInfoForm = {};

  constructor(
    private readonly _searchService: SearchService,
    private readonly _syntaxVersionService: SyntaxVersionService,
    private readonly _msgService: AppMessageService,
  ) {
    this.syntaxOptions = this._syntaxVersionService.parsers;
    this.form = new FormGroup({
      name: new FormControl(
        '', [Validators.required, Validators.pattern(REGEX_NAME)],
        this.existsParser.bind(this),
      ),
      description: new FormControl('', []),
      syntax: new FormControl(this.syntaxOptions[0], [Validators.required]),
      visibility: new FormControl(PackageVisibility.PRIVATE, [Validators.required]),
      password: new FormControl('', []),
    });
  }

  public validate(): ParserInfoForm|null {
    if(this.form.invalid) {
      this.showInvalid = true;
      this.showValidationErrors();
      return null;
    } else {
      const {
        name, description, syntax, visibility, password,
      } = this.form.value;
      const form: ParserInfoForm = {};
      if(this.cache.name !== name) form.name = name;
      if(this.cache.description !== description) form.description = description;
      if(this.cache.syntax !== syntax) form.syntax = syntax;
      if(this.cache.visibility !== visibility) form.visibility = visibility;
      if(this.form.controls['password'].enabled && this.cache.password !== password) {
        form.password = password;
      }
      return form;
    }
  }

  public set values(values: ParserInfoForm) {
    if(values.syntax && values.syntax !== this.cache.syntax) {
      this.emitParser(values.syntax);
    }
    Object.assign(this.cache, values);
    this.form.patchValue(values);
    this.updatePasswordControl();
  }

  protected updatePasswordControl() {
    const isProtected = this.form.value.visibility === PackageVisibility.PROTECTED;
    if(isProtected) this.form.controls['password'].enable();
    else this.form.controls['password'].disable();
  }

  protected get errorMessage(): Message|null {
    if(!this.nameAlreadyExists) return null;
    return {
      severity: 'error',
      detail: 'A parser with the same name already exists in your collection.',
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
    if(errors.syntax?.required) {
      this._msgService.showValidationFailedRequired('syntax');
    }
    if(errors.visibility?.required) {
      this._msgService.showValidationFailedRequired('visibility');
    }
  }

  protected existsParser(control: AbstractControl): Observable<ValidationErrors|null> {
    const id = toPackageId(this.id)?.name;
    if(!id || id === control.value) return of(null);
    return this._searchService.existsParserByName(control.value).pipe(
      map(exists => {
        this.nameAlreadyExists = exists;
        return exists ? {existsParser: true} : null
      })
    );
  }

  protected async copyProtectedUrl() {
    const uid = toBase64Url(JSON.stringify(
      {id: this.id, password: this.form.value.password}
    ));
    const url: string = `http://${window.location.host}/parsers/shared/${uid}`;
    await navigator.clipboard.writeText(url);
    this._msgService.showSuccess(MsgReasonSuccess.COPY_URL_PROTECTED_PARSER);
  }

  protected emitParser(syntax: ParserSyntax) {
    this.parser.emit(this._syntaxVersionService.getParser(syntax)!);
  }
}
