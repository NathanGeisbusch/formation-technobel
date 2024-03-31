import {AfterViewInit, ChangeDetectorRef, Component, ViewChild} from '@angular/core';
import {ActivatedRoute, Router} from "@angular/router";
import {fromPackageId, PackageId, REGEX_PACKAGE_ID, REGEX_VERSION, toPackageId} from "../../utils/package-validation";
import {GeneratorService} from "../../services/generator.service";
import {SearchService} from "../../services/search.service";
import {concatMap, forkJoin, from, toArray} from "rxjs";
import {GeneratorEditDTO} from "../../models/dto";
import {AppMessageService, MsgReasonSuccess} from "../../services/app.message.service";
import {ToolbarModule} from "primeng/toolbar";
import {SplitButtonModule} from "primeng/splitbutton";
import {MenuItem} from "primeng/api";
import {DropdownChangeEvent, DropdownModule} from "primeng/dropdown";
import {FormsModule} from "@angular/forms";
import {TabViewModule} from "primeng/tabview";
import {NgIf} from "@angular/common";
import {Parser} from "../../models/syntax/parser";
import {AuthService} from "../../services/auth.service";
import {DialogCreateSessionComponent} from "../../components/dialog-create-session/dialog-create-session.component";
import {Generator} from "../../models/syntax/generator";
import {FormEditGeneratorComponent} from "./form-edit-generator/form-edit-generator.component";
import {EditorParserComponent} from "../../components/editor-parser/editor-parser.component";
import {EditorBuilderComponent} from "../../components/editor-builder/editor-builder.component";
import {EditorGeneratorComponent} from "../../components/editor-generator/editor-generator.component";
import {EditorDocComponent} from "../../components/editor-doc/editor-doc.component";
import {
  EditorInputGeneratorComponent,
  GeneratorData
} from "../../components/editor-input-generator/editor-input-generator.component";
import {SessionService} from "../../services/session.service";

interface GeneratorPackage {
  info: GeneratorEditDTO,
  parser: string,
  builder: string,
  generator: string,
  doc: string,
  versions: string[],
}

@Component({
  selector: 'app-page-edit-generator',
  templateUrl: './page-edit-generator.component.html',
  styleUrl: './page-edit-generator.component.scss',
  standalone: true,
  imports: [
    ToolbarModule,
    DropdownModule,
    SplitButtonModule,
    TabViewModule,
    FormEditGeneratorComponent,
    EditorParserComponent,
    EditorBuilderComponent,
    EditorGeneratorComponent,
    EditorDocComponent,
    EditorInputGeneratorComponent,
    DialogCreateSessionComponent,
    NgIf,
    FormsModule
  ]
})
export class PageEditGeneratorComponent implements AfterViewInit {
  @ViewChild('form') protected form!: FormEditGeneratorComponent;
  @ViewChild('parserEditor') protected parserEditor!: EditorParserComponent;
  @ViewChild('builderEditor') protected builderEditor!: EditorBuilderComponent;
  @ViewChild('generatorEditor') protected generatorEditor!: EditorGeneratorComponent;
  @ViewChild('docEditor') protected docEditor!: EditorDocComponent;
  @ViewChild('inputEditor') protected inputEditor!: EditorInputGeneratorComponent;
  protected readonly id: string = '';
  protected package?: GeneratorPackage;
  protected actions: MenuItem[] | undefined;
  protected selectedVersion: string = '';
  protected showDialogCreateSession: boolean = false;
  protected parser?: Parser;
  protected generator?: Generator;

  constructor(
    private readonly _auth: AuthService,
    private readonly _generatorService: GeneratorService,
    private readonly _sessionService: SessionService,
    private readonly _searchService: SearchService,
    private readonly _msgService: AppMessageService,
    private readonly _ref: ChangeDetectorRef,
    private readonly _router: Router,
    activatedRoute: ActivatedRoute
  ) {
    const id = activatedRoute.snapshot.params['id'];
    if(REGEX_PACKAGE_ID.test(id)) this.id = id;
    else this._router.navigate(['404'], {skipLocationChange: true}).then();
    this.actions = [
      {label: 'Create a session', icon: 'pi pi-box', command: () => {
        this.showDialogCreateSession = true;
      }},
      {label: 'New major version', icon: 'pi pi-plus', command: () => {
        this._generatorService.createMajorVersion(this.id).subscribe({
          next: (created) => {
            this._msgService.showSuccess(MsgReasonSuccess.GENERATOR_VERSION_CREATE);
            this._auth.redirectTo('/generators/edit/'+created.id);
          }
        });
      }},
      {label: 'New minor version', icon: 'pi pi-plus', command: () => {
          this._generatorService.createMinorVersion(this.id).subscribe({
            next: (created) => {
              this._msgService.showSuccess(MsgReasonSuccess.GENERATOR_VERSION_CREATE);
              this._auth.redirectTo('/generators/edit/'+created.id);
            }
          });
        }},
      {label: 'New patch version', icon: 'pi pi-plus', command: () => {
        this._generatorService.createPatchVersion(this.id).subscribe({
          next: (created) => {
            this._msgService.showSuccess(MsgReasonSuccess.GENERATOR_VERSION_CREATE);
            this._auth.redirectTo('/generators/edit/'+created.id);
          }
        });
      }},
      {label: 'Delete version', icon: 'pi pi-trash', command: () => {
        this._generatorService.delete(this.id).subscribe({
          next: () => {
            this._msgService.showSuccess(MsgReasonSuccess.GENERATOR_VERSION_DELETE);
            const id = toPackageId(this.id)!;
            this._searchService.findGeneratorVersions(id.author, id.name, '').subscribe({
              next: (versions) => {
                if(versions.length > 0) {
                  id.version = versions[0];
                  this._auth.redirectTo('/generators/edit/'+fromPackageId(id));
                } else {
                  this._router.navigate(['/generators/own']).then();
                }
              },
              error: () => {
                this._router.navigate(['/generators/own']).then();
              },
            });
          }
        })
      }},
      {label: 'Delete generator', icon: 'pi pi-trash', command: () => {
        this._generatorService.delete(this.id, true).subscribe({
          next: () => {
            this._msgService.showSuccess(MsgReasonSuccess.GENERATOR_DELETE);
            this._router.navigate(['/generators/own']).then();
          }
        })
      }},
    ];
  }

  public ngAfterViewInit() {
    const id = toPackageId(this.id)!;
    this.selectedVersion = id.version;
    forkJoin({
      info: this._generatorService.getEditable(this.id),
      parser: this._generatorService.getParserCode(this.id),
      builder: this._generatorService.getBuilderCode(this.id),
      generator: this._generatorService.getGeneratorCode(this.id),
      doc: this._generatorService.getDocCode(this.id),
      versions: this._searchService.findGeneratorVersions(id.author, id.name, ''),
    }).subscribe({
      next: (value) => {
        this.package = value;
        this._ref.detectChanges();
        this.form.values = {
          name: this.package.info.name,
          description: this.package.info.description,
          parserSyntax: this.package.info.parserSyntax,
          generatorSyntax: this.package.info.generatorSyntax,
          visibility: this.package.info.visibility,
          password: this.package.info.password,
        };
      },
      error: () => {
        this._router.navigate(['404'], {skipLocationChange: true}).then();
      }
    });
  }

  protected get packageId(): PackageId {
    return toPackageId(this.id)!;
  }

  protected save() {
    const form = this.form.validate();
    const parser = this.parserEditor.content;
    const builder = this.builderEditor.content;
    const generator = this.generatorEditor.content;
    const doc = this.docEditor.content;
    const formChanged = form && Object.keys(form).length !== 0;
    const parserChanged = this.package!.parser !== parser;
    const builderChanged = this.package!.builder !== builder;
    const generatorChanged = this.package!.generator !== generator;
    const docChanged = this.package!.doc !== doc;
    const update = [];
    if(parserChanged) update.push(this._generatorService.updateParserCode(this.id, parser));
    if(builderChanged) update.push(this._generatorService.updateBuilderCode(this.id, builder));
    if(generatorChanged) update.push(this._generatorService.updateGeneratorCode(this.id, generator));
    if(docChanged) update.push(this._generatorService.updateDocCode(this.id, doc));
    if(formChanged) update.push(this._generatorService.update(this.id, form));
    if(update.length !== 0) from(update).pipe(
      concatMap(observable => observable),
      toArray(),
    ).subscribe({
      next: () => {
        this._msgService.showSuccess(MsgReasonSuccess.GENERATOR_UPDATE);
        if(form && form.name && this.package!.info.name !== form.name) {
          const id = toPackageId(this.id)!;
          id.name = form.name!;
          this._auth.redirectTo('/generators/edit/'+fromPackageId(id));
          return;
        }
        if(formChanged) {
          Object.assign(this.package!.info, form);
          this.form.values = form;
        }
        if(parserChanged) this.package!.parser = parser;
        if(builderChanged) this.package!.builder = builder;
        if(generatorChanged) this.package!.generator = generator;
        if(docChanged) this.package!.doc = doc;
      },
    });
  }

  protected onChangeVersion($event: DropdownChangeEvent) {
    if(REGEX_VERSION.test($event.value)) {
      if(this.package!.versions.includes($event.value)) {
        const id = toPackageId(this.id)!;
        id.version = $event.value;
        this._auth.redirectTo('/generators/edit/'+fromPackageId(id));
      }
    }
    else this.updateVersions($event.value);
  }

  private updateVersions(version: string) {
    const id = toPackageId(this.id)!;
    this._searchService.findGeneratorVersions(id.author, id.name, version).subscribe({
      next: (versions) => this.package!.versions = versions,
    });
  }

  protected onCreateSession(name: string) {
    this._sessionService.updateInputText(name, this.inputEditor.content).subscribe({
      next: () => this._router.navigate(['/sessions/'+name]).then()
    });
  }

  protected get data(): GeneratorData|null {
    if(this.parser && this.generator && this.package) {
      return {
        parser: this.parser,
        generator: this.generator,
        parserCode: this.parserEditor.content,
        builderCode: this.builderEditor.content,
        generatorCode: this.generatorEditor.content,
      }
    }
    return null;
  }
}
