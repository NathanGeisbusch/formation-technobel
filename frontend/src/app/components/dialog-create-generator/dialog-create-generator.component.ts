import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {
  AbstractControl,
  FormControl,
  FormGroup,
  ReactiveFormsModule, ValidationErrors,
  Validators
} from "@angular/forms";
import {AppMessageService, MsgReasonSuccess} from "../../services/app.message.service";
import {PackageId, REGEX_NAME, REGEX_VERSION} from "../../utils/package-validation";
import {Message} from "primeng/api";
import {CLASS_INVALID_FORM, getAllFormErrors, hasAnyFormErrors} from "../../utils/form-validation";
import {DropdownChangeEvent, DropdownModule} from "primeng/dropdown";
import {DialogModule} from "primeng/dialog";
import {InputTextModule} from "primeng/inputtext";
import {NgClass, NgIf} from "@angular/common";
import {ButtonModule} from "primeng/button";
import {MessageComponent} from "../message/message.component";
import {SearchService} from "../../services/search.service";
import {map, Observable} from "rxjs";
import {GeneratorService} from "../../services/generator.service";
import {PackageCreateForm} from "../../models/form";

@Component({
  selector: 'app-dialog-create-generator',
  templateUrl: './dialog-create-generator.component.html',
  styleUrl: './dialog-create-generator.component.scss',
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
export class DialogCreateGeneratorComponent implements OnInit {
  @Input() public fromParser?: PackageId;
  @Input() public fixedVersion: boolean = false;
  @Input() public visible: boolean = false;
  @Output() protected readonly visibleChange = new EventEmitter<boolean>();
  @Output() protected readonly save = new EventEmitter<void>();
  protected readonly form: FormGroup;
  protected readonly invalidClass = CLASS_INVALID_FORM;
  protected showInvalid: boolean = false;
  protected nameAlreadyExists: boolean = false;
  protected versions: {value:string}[] = [];

  public constructor(
    private readonly _generatorService: GeneratorService,
    private readonly _searchService: SearchService,
    private readonly _messageService: AppMessageService,
  ) {
    this.form = new FormGroup({
      name: new FormControl(
        '', [Validators.required, Validators.pattern(REGEX_NAME)],
        this.existsGenerator.bind(this),
      ),
      sourceVersion: new FormControl(
        '', [Validators.required, Validators.pattern(REGEX_VERSION)]
      ),
    });
  }

  public ngOnInit() {
    if(this.fixedVersion || !this.fromParser) {
      this.form.controls['sourceVersion'].disable();
    }
    if(this.fromParser) {
      this.form.patchValue({sourceVersion: this.fromParser.version});
      this.updateVersions(this.fromParser.version);
    }
  }

  protected get errorMessage(): Message|null {
    if(!this.nameAlreadyExists) return null;
    return {
      severity: 'error',
      detail: 'A generator with the same name already exists in your collection.',
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
      const {name, sourceVersion} =  this.form.value;
      const form: PackageCreateForm = {name};
      if(this.fromParser) {
        form.from = this.fromParser.author+":"+this.fromParser.name+"@"+sourceVersion;
      }
      this._generatorService.create(form, !!this.fromParser).subscribe({
        next: () => {
          this._messageService.showSuccess(MsgReasonSuccess.GENERATOR_CREATE);
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
    if(errors.sourceVersion?.required) {
      this._messageService.showValidationFailedRequired('parser version');
    }
    if(errors.sourceVersion?.pattern) {
      this._messageService.showValidationFailedPattern('parser version', this.form.value.sourceVersion);
    }
  }

  protected existsGenerator(control: AbstractControl): Observable<ValidationErrors|null> {
    return this._searchService.existsGeneratorByName(control.value).pipe(
      map(exists => {
        this.nameAlreadyExists = exists;
        return exists ? {existsGenerator: true} : null
      })
    );
  }

  protected get hasAnyFormErrors(): boolean {
    return hasAnyFormErrors(this.form);
  }

  protected onChangeVersion($event: DropdownChangeEvent) {
    this.updateVersions($event.value);
  }

  private updateVersions(version: string) {
    this._searchService.findParserVersions(
      this.fromParser!.author, this.fromParser!.name, version
    ).subscribe({
      next: (versions) => this.versions = versions.map(value => ({value})),
    });
  }
}
