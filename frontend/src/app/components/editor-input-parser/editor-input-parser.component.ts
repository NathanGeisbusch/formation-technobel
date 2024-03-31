import {AfterViewInit, Component, Input, ViewChild} from '@angular/core';
import {Parser} from "../../models/syntax/parser";
import {CodeEditorComponent} from "../code-editor/code-editor.component";
import {AppMessageService, MsgReasonError, MsgReasonSuccess} from "../../services/app.message.service";
import {markdownToHtml} from "../../utils/markdown";
import {Text} from "@codemirror/state";
import {SplitterModule} from "primeng/splitter";
import {ToolbarModule} from "primeng/toolbar";
import {ButtonModule} from "primeng/button";
import {handleCodeErrors, LintErrors} from "../../models/syntax/utils";

export interface ParserData {
  parser: Parser,
  parserCode: string,
  builderCode: string,
}

@Component({
  selector: 'app-editor-input-parser',
  templateUrl: './editor-input-parser.component.html',
  styleUrl: './editor-input-parser.component.scss',
  standalone: true,
  imports: [
    SplitterModule,
    ToolbarModule,
    ButtonModule,
    CodeEditorComponent
  ]
})
export class EditorInputParserComponent implements AfterViewInit {
  @ViewChild('editor') protected editor!: CodeEditorComponent;
  @ViewChild('previewEditor') protected previewEditor!: CodeEditorComponent;
  @Input({alias:'content'}) public _content?: string;
  @Input({alias:'doc'}) public _doc?: string;
  @Input({required:true}) data: ParserData|null = null;
  protected isDocVisible: boolean = false;
  protected isResultVisible: boolean = false;
  private firstLintPass: boolean = true;

  constructor(private readonly _msgService: AppMessageService) {}

  public ngAfterViewInit() {
    if(this._content) this.editor.content = this._content;
    if(this._doc) this.isDocVisible = true;
    else this.isResultVisible = true;
  }

  public get content(): string {
    return this.editor.text;
  }

  protected get doc(): string {
    return this._doc ? markdownToHtml(this._doc) : '';
  }

  protected toggleDoc() {
    this.isDocVisible = !this.isDocVisible;
    this.isResultVisible = false;
  }

  protected toggleResult() {
    this.isResultVisible = !this.isResultVisible;
    this.isDocVisible = false;
  }

  protected async copyFileToClipboard() {
    await navigator.clipboard.writeText(this.previewEditor.text);
    this._msgService.showSuccess(MsgReasonSuccess.COPY_RESULT_FILE);
  }

  protected linter(text: Text): LintErrors {
    if(!this.data) return;
    let parserConfig: any;
    let builderConfig: any;
    try {
      parserConfig = this.data.parser.parseSrcParser(this.data.parserCode);
    }
    catch(ex) {
      if(!this.firstLintPass) this._msgService.showError(MsgReasonError.SRC_PARSER);
      this.previewEditor.content = '{}';
      this.firstLintPass = false;
      return;
    }
    try {
      builderConfig = this.data.parser.parseSrcBuilder(this.data.builderCode);
    }
    catch(ex) {
      if(!this.firstLintPass) this._msgService.showError(MsgReasonError.SRC_BUILDER);
      this.previewEditor.content = '{}';
      this.firstLintPass = false;
      return;
    }
    this.firstLintPass = false;
    return handleCodeErrors(text, () => {
      const data = this.data!.parser.parse(this.editor.text, parserConfig, builderConfig);
      this.previewEditor.content = JSON.stringify(data, null, 2);
    });
  }
}
