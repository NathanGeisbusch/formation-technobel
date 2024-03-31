import {AfterViewInit, ChangeDetectorRef, Component, ViewChild} from '@angular/core';
import {ToolbarModule} from "primeng/toolbar";
import {ButtonModule} from "primeng/button";
import {TabViewModule} from "primeng/tabview";
import {FormEditSessionComponent} from "./form-edit-session/form-edit-session.component";
import {
  EditorInputGeneratorComponent,
  GeneratorData
} from "../../components/editor-input-generator/editor-input-generator.component";
import {SessionDTO} from "../../models/dto";
import {Parser} from "../../models/syntax/parser";
import {AuthService} from "../../services/auth.service";
import {AppMessageService, MsgReasonSuccess} from "../../services/app.message.service";
import {ActivatedRoute, Router} from "@angular/router";
import {concatMap, forkJoin, from, toArray} from "rxjs";
import {Generator} from "../../models/syntax/generator";
import {SessionService} from "../../services/session.service";
import {EditorGeneratorComponent} from "../../components/editor-generator/editor-generator.component";
import {SyntaxVersionService} from "../../services/syntax.version.service";

interface SessionPackage {
  info: SessionDTO,
  parser: string,
  builder: string,
  generator: string,
  doc: string,
  input: string,
}

@Component({
  selector: 'app-page-edit-session',
  templateUrl: './page-edit-session.component.html',
  styleUrl: './page-edit-session.component.scss',
  standalone: true,
  imports: [
    ToolbarModule,
    ButtonModule,
    TabViewModule,
    FormEditSessionComponent,
    EditorInputGeneratorComponent
  ]
})
export class PageEditSessionComponent implements AfterViewInit {
  @ViewChild('form') protected form!: FormEditSessionComponent;
  @ViewChild('inputEditor') protected inputEditor!: EditorGeneratorComponent;
  protected readonly id: string = '';
  protected package?: SessionPackage;
  protected parser?: Parser;
  protected generator?: Generator;

  constructor(
    private readonly _auth: AuthService,
    private readonly _sessionService: SessionService,
    private readonly _syntaxVersionService: SyntaxVersionService,
    private readonly _msgService: AppMessageService,
    private readonly _ref: ChangeDetectorRef,
    private readonly _router: Router,
    activatedRoute: ActivatedRoute
  ) {
    this.id = activatedRoute.snapshot.params['id'];
  }

  public ngAfterViewInit() {
    forkJoin({
      info: this._sessionService.get(this.id),
      parser: this._sessionService.getParserCode(this.id),
      builder: this._sessionService.getBuilderCode(this.id),
      generator: this._sessionService.getGeneratorCode(this.id),
      doc: this._sessionService.getDocCode(this.id),
      input: this._sessionService.getInputText(this.id),
    }).subscribe({
      next: (value) => {
        this.package = value;
        this.parser = this._syntaxVersionService.getParser(value.info.parserSyntax)!;
        this.generator = this._syntaxVersionService.getGenerator(value.info.generatorSyntax)!;
        this._ref.detectChanges();
        this.form.values = {
          name: this.package.info.name,
        };
      },
      error: () => {
        this._router.navigate(['404'], {skipLocationChange: true}).then();
      }
    });
  }

  protected save() {
    const form = this.form.validate();
    const input = this.inputEditor.content;
    const formChanged = form && Object.keys(form).length !== 0;
    const inputChanged = this.package!.input !== input;
    const update = [];
    if(inputChanged) update.push(this._sessionService.updateInputText(this.id, input));
    if(formChanged) update.push(this._sessionService.update(this.id, form));
    if(update.length !== 0) from(update).pipe(
      concatMap(observable => observable),
      toArray(),
    ).subscribe({
      next: () => {
        this._msgService.showSuccess(MsgReasonSuccess.SESSION_UPDATE);
        if(form && form.name && this.package!.info.name !== form.name) {
          this._auth.redirectTo('/sessions/'+form.name);
          return;
        }
        if(formChanged) {
          Object.assign(this.package!.info, form);
          this.form.values = form;
        }
        if(inputChanged) this.package!.input = input;
      },
    });
  }

  protected get data(): GeneratorData|null {
    if(this.parser && this.generator && this.package) {
      return {
        parser: this.parser,
        generator: this.generator,
        parserCode: this.package.parser,
        builderCode: this.package.builder,
        generatorCode: this.package.generator,
      }
    }
    return null;
  }
}
