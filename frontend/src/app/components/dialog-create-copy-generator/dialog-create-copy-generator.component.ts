import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {DialogModule} from "primeng/dialog";
import {InputTextModule} from "primeng/inputtext";
import {DropdownChangeEvent, DropdownModule} from "primeng/dropdown";
import {
  AbstractControl,
  FormControl,
  FormGroup,
  ReactiveFormsModule,
  ValidationErrors,
  Validators
} from "@angular/forms";
import {NgClass, NgIf} from "@angular/common";
import {ButtonModule} from "primeng/button";
import {MessageComponent} from "../message/message.component";
import {CLASS_INVALID_FORM, getAllFormErrors, hasAnyFormErrors} from "../../utils/form-validation";
import {SearchService} from "../../services/search.service";
import {AppMessageService, MsgReasonSuccess} from "../../services/app.message.service";
import {PackageId, REGEX_NAME, REGEX_VERSION} from "../../utils/package-validation";
import {Message} from "primeng/api";
import {PackageCreateForm} from "../../models/form";
import {forkJoin, map, Observable} from "rxjs";
import {GeneratorService} from "../../services/generator.service";

@Component({
  selector: 'app-dialog-create-copy-generator',
  templateUrl: './dialog-create-copy-generator.component.html',
  styleUrl: './dialog-create-copy-generator.component.scss',
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
export class DialogCreateCopyGeneratorComponent implements OnInit{
  @Input({required:true}) public fromGenerator!: PackageId;
  @Input() public fixedVersion: boolean = false;
  @Input() public visible: boolean = false;
  @Output() protected readonly visibleChange = new EventEmitter<boolean>();
  @Output() protected readonly save = new EventEmitter<void>();
  protected readonly form: FormGroup;
  protected readonly invalidClass = CLASS_INVALID_FORM;
  protected showInvalid: boolean = false;
  protected nameAlreadyExists: boolean = false;
  protected versionAlreadyExists: boolean = false;
  protected versions: {value:string}[] = [];

  public constructor(
    private readonly _generatorService: GeneratorService,
    private readonly _searchService: SearchService,
    private readonly _messageService: AppMessageService,
  ) {
    this.form = new FormGroup({
      name: new FormControl(
        '', [Validators.required, Validators.pattern(REGEX_NAME)],
        this.existsGeneratorByName.bind(this),
      ),
      sourceVersion: new FormControl(
        '', [Validators.required, Validators.pattern(REGEX_VERSION)],
        this.existsGeneratorByVersion.bind(this),
      ),
    });
  }

  public ngOnInit() {
    if(this.fixedVersion) this.form.controls['sourceVersion'].disable();
    this.form.patchValue({sourceVersion: this.fromGenerator.version});
    this.updateVersions(this.fromGenerator.version);
  }

  protected get errorMessage(): Message|null {
    if(this.versionAlreadyExists) return {
      severity: 'error',
      detail: 'A generator with the same name and version already exists in your collection.',
    };
    else if(this.nameAlreadyExists) return {
      severity: 'warn',
      detail: 'A generator with the same name already exists in your collection.'
        +'\nThis content will be added at the given version.',
    };
    else return null;
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
      if(this.fromGenerator) {
        form.from = this.fromGenerator.author+":"+this.fromGenerator.name+"@"+sourceVersion;
      }
      this._generatorService.create(form).subscribe({
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
      this._messageService.showValidationFailedRequired('source version');
    }
    if(errors.sourceVersion?.pattern) {
      this._messageService.showValidationFailedPattern('source version', this.form.value.sourceVersion);
    }
  }

  protected existsGeneratorByVersion(control: AbstractControl): Observable<ValidationErrors|null> {
    return this._searchService.existsGeneratorByVersion(
      this.form.value.name, control.value
    ).pipe(
      map(exists => {
        this.versionAlreadyExists = exists;
        return exists ? {existsGenerator: true} : null
      })
    );
  }

  protected existsGeneratorByName(control: AbstractControl): Observable<ValidationErrors|null> {
    const {sourceVersion} = this.form.value;
    return forkJoin({
      name: this._searchService.existsGeneratorByName(control.value),
      version: this._searchService.existsGeneratorByVersion(control.value, sourceVersion),
    }).pipe(
      map((exists) => {
        this.nameAlreadyExists = exists.name;
        this.versionAlreadyExists = exists.version;
        return exists.version ? {existsParser: true} : null
      })
    );
  }

  protected get hasAnyFormErrors(): boolean {
    return hasAnyFormErrors(this.form);
  }

  private updateVersions(version: string) {
    this._searchService.findGeneratorVersions(
      this.fromGenerator!.author, this.fromGenerator!.name, version
    ).subscribe({
      next: (versions) => this.versions = versions.map(value => ({value})),
    });
  }

  protected onChangeVersion($event: DropdownChangeEvent) {
    this.updateVersions($event.value);
  }
}
