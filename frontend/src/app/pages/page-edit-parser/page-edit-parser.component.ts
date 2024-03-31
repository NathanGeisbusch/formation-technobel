import {AfterViewInit, ChangeDetectorRef, Component, ViewChild} from '@angular/core';
import {ToolbarModule} from "primeng/toolbar";
import {DropdownChangeEvent, DropdownModule} from "primeng/dropdown";
import {SplitButtonModule} from "primeng/splitbutton";
import {FormsModule} from "@angular/forms";
import {TabViewModule} from "primeng/tabview";
import {FormEditParserComponent} from "./form-edit-parser/form-edit-parser.component";
import {EditorParserComponent} from "../../components/editor-parser/editor-parser.component";
import {EditorBuilderComponent} from "../../components/editor-builder/editor-builder.component";
import {EditorDocComponent} from "../../components/editor-doc/editor-doc.component";
import {
  EditorInputParserComponent,
  ParserData
} from "../../components/editor-input-parser/editor-input-parser.component";
import {MenuItem} from "primeng/api";
import {Parser} from "../../models/syntax/parser";
import {AuthService} from "../../services/auth.service";
import {SearchService} from "../../services/search.service";
import {AppMessageService, MsgReasonSuccess} from "../../services/app.message.service";
import {ActivatedRoute, Router} from "@angular/router";
import {fromPackageId, REGEX_PACKAGE_ID, REGEX_VERSION, toPackageId} from "../../utils/package-validation";
import {concatMap, forkJoin, from, toArray} from "rxjs";
import {ParserEditDTO} from "../../models/dto";
import {ParserService} from "../../services/parser.service";

interface ParserPackage {
  info: ParserEditDTO,
  parser: string,
  builder: string,
  doc: string,
  versions: string[],
}

@Component({
  selector: 'app-page-edit-parser',
  templateUrl: './page-edit-parser.component.html',
  styleUrl: './page-edit-parser.component.scss',
  standalone: true,
  imports: [
    ToolbarModule,
    DropdownModule,
    SplitButtonModule,
    FormsModule,
    TabViewModule,
    FormEditParserComponent,
    EditorParserComponent,
    EditorBuilderComponent,
    EditorDocComponent,
    EditorInputParserComponent
  ]
})
export class PageEditParserComponent implements AfterViewInit {
  @ViewChild('form') protected form!: FormEditParserComponent;
  @ViewChild('parserEditor') protected parserEditor!: EditorParserComponent;
  @ViewChild('builderEditor') protected builderEditor!: EditorBuilderComponent;
  @ViewChild('docEditor') protected docEditor!: EditorDocComponent;
  protected readonly id: string = '';
  protected package?: ParserPackage;
  protected actions: MenuItem[] | undefined;
  protected selectedVersion: string = '';
  protected parser?: Parser;

  constructor(
    private readonly _auth: AuthService,
    private readonly _parserService: ParserService,
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
      {label: 'New major version', icon: 'pi pi-plus', command: () => {
        this._parserService.createMajorVersion(this.id).subscribe({
          next: (created) => {
            this._msgService.showSuccess(MsgReasonSuccess.PARSER_VERSION_CREATE);
            this._auth.redirectTo('/parsers/edit/'+created.id);
          }
        });
      }},
      {label: 'New minor version', icon: 'pi pi-plus', command: () => {
        this._parserService.createMinorVersion(this.id).subscribe({
          next: (created) => {
            this._msgService.showSuccess(MsgReasonSuccess.PARSER_VERSION_CREATE);
            this._auth.redirectTo('/parsers/edit/'+created.id);
          }
        });
      }},
      {label: 'New patch version', icon: 'pi pi-plus', command: () => {
        this._parserService.createPatchVersion(this.id).subscribe({
          next: (created) => {
            this._msgService.showSuccess(MsgReasonSuccess.PARSER_VERSION_CREATE);
            this._auth.redirectTo('/parsers/edit/'+created.id);
          }
        });
      }},
      {label: 'Delete version', icon: 'pi pi-trash', command: () => {
        this._parserService.delete(this.id).subscribe({
          next: () => {
            this._msgService.showSuccess(MsgReasonSuccess.PARSER_VERSION_DELETE);
            const id = toPackageId(this.id)!;
            this._searchService.findParserVersions(id.author, id.name, '').subscribe({
              next: (versions) => {
                if(versions.length > 0) {
                  id.version = versions[0];
                  this._auth.redirectTo('/parsers/edit/'+fromPackageId(id));
                } else {
                  this._router.navigate(['/parsers/own']).then();
                }
              },
              error: () => {
                this._router.navigate(['/parsers/own']).then();
              },
            });
          }
        })
      }},
      {label: 'Delete parser', icon: 'pi pi-trash', command: () => {
        this._parserService.delete(this.id, true).subscribe({
          next: () => {
            this._msgService.showSuccess(MsgReasonSuccess.PARSER_DELETE);
            this._router.navigate(['/parsers/own']).then();
          }
        })
      }},
    ];
  }

  public ngAfterViewInit() {
    const id = toPackageId(this.id)!;
    this.selectedVersion = id.version;
    forkJoin({
      info: this._parserService.getEditable(this.id),
      parser: this._parserService.getParserCode(this.id),
      builder: this._parserService.getBuilderCode(this.id),
      doc: this._parserService.getDocCode(this.id),
      versions: this._searchService.findParserVersions(id.author, id.name, ''),
    }).subscribe({
      next: (value) => {
        this.package = value;
        this._ref.detectChanges();
        this.form.values = {
          name: this.package.info.name,
          description: this.package.info.description,
          syntax: this.package.info.syntax,
          visibility: this.package.info.visibility,
          password: this.package.info.password,
        };
      },
      error: () => {
        this._router.navigate(['404'], {skipLocationChange: true}).then();
      }
    });
  }

  protected save() {
    const form = this.form.validate();
    const parser = this.parserEditor.content;
    const builder = this.builderEditor.content;
    const doc = this.docEditor.content;
    const formChanged = form && Object.keys(form).length !== 0;
    const parserChanged = this.package!.parser !== parser;
    const builderChanged = this.package!.builder !== builder;
    const docChanged = this.package!.doc !== doc;
    const update = [];
    if(parserChanged) update.push(this._parserService.updateParserCode(this.id, parser));
    if(builderChanged) update.push(this._parserService.updateBuilderCode(this.id, builder));
    if(docChanged) update.push(this._parserService.updateDocCode(this.id, doc));
    if(formChanged) update.push(this._parserService.update(this.id, form));
    if(update.length !== 0) from(update).pipe(
      concatMap(observable => observable),
      toArray(),
    ).subscribe({
      next: () => {
        this._msgService.showSuccess(MsgReasonSuccess.PARSER_UPDATE);
        if(form && form.name && this.package!.info.name !== form.name) {
          const id = toPackageId(this.id)!;
          id.name = form.name!;
          this._auth.redirectTo('/parsers/edit/'+fromPackageId(id));
          return;
        }
        if(formChanged) {
          Object.assign(this.package!.info, form);
          this.form.values = form;
        }
        if(parserChanged) this.package!.parser = parser;
        if(builderChanged) this.package!.builder = builder;
        if(docChanged) this.package!.doc = doc;
      },
    });
  }

  protected onChangeVersion($event: DropdownChangeEvent) {
    if(REGEX_VERSION.test($event.value)) {
      if(this.package!.versions.includes($event.value)) {
        const id = toPackageId(this.id)!;
        id.version = $event.value;
        this._auth.redirectTo('/parsers/edit/'+fromPackageId(id));
      }
    }
    else this.updateVersions($event.value);
  }

  private updateVersions(version: string) {
    const id = toPackageId(this.id)!;
    this._searchService.findParserVersions(id.author, id.name, version).subscribe({
      next: (versions) => this.package!.versions = versions,
    });
  }

  protected get data(): ParserData|null {
    if(this.parser && this.package) {
      return {
        parser: this.parser,
        parserCode: this.parserEditor.content,
        builderCode: this.builderEditor.content,
      }
    }
    return null;
  }
}
