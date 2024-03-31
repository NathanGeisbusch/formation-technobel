import {AfterViewInit, Component, Input, ViewChild} from '@angular/core';
import {CodeEditorComponent} from "../code-editor/code-editor.component";
import {Parser} from "../../models/syntax/parser";
import {markdownToHtml} from "../../utils/markdown";
import {Text} from "@codemirror/state";
import {SplitterModule} from "primeng/splitter";
import {ButtonModule} from "primeng/button";
import {Tree, TreeModule} from "primeng/tree";
import {TreeNode} from "primeng/api";
import {Generator, resultFilesToTree} from "../../models/syntax/generator";
import {ToolbarModule} from "primeng/toolbar";
import {SvgIconComponent} from "../svg-icon/svg-icon.component";
import {AppMessageService, MsgReasonError, MsgReasonSuccess} from "../../services/app.message.service";
import {zipNodes} from "../../utils/zip";
import {GeneratorError, handleCodeErrors, InputData, LintErrors} from "../../models/syntax/utils";

export interface GeneratorData {
  parser: Parser,
  generator: Generator,
  parserCode: string,
  builderCode: string,
  generatorCode: string,
}

@Component({
  selector: 'app-editor-input-generator',
  templateUrl: './editor-input-generator.component.html',
  styleUrl: './editor-input-generator.component.scss',
  standalone: true,
  imports: [
    SplitterModule,
    ButtonModule,
    CodeEditorComponent,
    TreeModule,
    ToolbarModule,
    SvgIconComponent
  ]
})
export class EditorInputGeneratorComponent implements AfterViewInit {
  @ViewChild('editor') protected editor!: CodeEditorComponent;
  @ViewChild('previewEditor') protected previewEditor!: CodeEditorComponent;
  @ViewChild('resultTree') protected resultTree!: Tree;
  @Input({alias:'content'}) public _content?: string;
  @Input({alias:'doc'}) public _doc?: string;
  @Input({required:true}) data: GeneratorData|null = null;
  protected isDocVisible: boolean = false;
  protected isResultVisible: boolean = false;
  protected results: TreeNode[] = [];
  protected selectedFile?: TreeNode;
  protected generatorConfig: any|null = null;
  protected inputData: InputData|null = null;
  private firstLintPass: boolean = true;

  constructor(private readonly _msgService: AppMessageService) {}

  public ngAfterViewInit() {
    if(this._content) this.editor.content = this._content;
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

  protected toggleResult(isResultVisible = !this.isResultVisible) {
    this.isResultVisible = isResultVisible;
    this.isDocVisible = false;
  }

  protected setGeneratorState(running: boolean) {
    if(this.data) {
      if(running) {
        this.results.length = 0;
        this.selectedFile = undefined;
        this.resultTree.selection = undefined;
        this.previewEditor.content = '';
        try {
          if(this.linter(this.editor.content)) {
            this._msgService.showError(MsgReasonError.PARSER);
            return;
          }
          if(this.generatorConfig && this.inputData) {
            const files = this.data!.generator.generate(this.inputData, this.generatorConfig);
            this.results = resultFilesToTree(files);
            if(this.results.length === 1 && (!this.results[0].children || this.results[0].children.length === 0)) {
              this.resultTree.selection = this.results[0];
              this.onSelectResult(this.results[0]);
            }
            this.toggleResult(true);
          }
        }
        catch(ex) {
          if(ex instanceof GeneratorError) {
            if(this._doc) this._msgService.showError(MsgReasonError.GENERATOR);
            else this._msgService.showCustomError('Generator error', ex.message);
          }
          else throw ex;
        }
      }
      else this.data.generator.stop();
    }
  }

  protected onSelectResult($event: TreeNode<any> | TreeNode<any>[] | null) {
    if(!$event || Array.isArray($event)) return;
    if($event.children) {
      $event.expanded = !$event.expanded;
      this.resultTree.selection = undefined;
    }
    else if($event.data !== undefined) {
      this.selectedFile = $event;
      this.previewEditor.content = $event.data;
    }
  }

  protected async copyFileToClipboard() {
    if(!this.selectedFile) return;
    await navigator.clipboard.writeText(this.selectedFile.data);
    this._msgService.showSuccess(MsgReasonSuccess.COPY_RESULT_FILE);
  }

  protected zipFiles() {
    if(this.results.length === 0) return;
    zipNodes(this.results);
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
      this.firstLintPass = false;
      return;
    }
    try {
      builderConfig = this.data.parser.parseSrcBuilder(this.data.builderCode);
    }
    catch(ex) {
      if(!this.firstLintPass) this._msgService.showError(MsgReasonError.SRC_BUILDER);
      this.firstLintPass = false;
      return;
    }
    try {
      this.generatorConfig = this.data.generator.parseSrcGenerator(this.data.generatorCode);
    }
    catch(ex) {
      if(!this.firstLintPass) this._msgService.showError(MsgReasonError.SRC_GENERATOR);
      this.firstLintPass = false;
      return;
    }
    this.firstLintPass = false;
    return handleCodeErrors(text, () => {
      this.inputData = this.data!.parser.parse(this.editor.text, parserConfig, builderConfig);
    });
  }
}

