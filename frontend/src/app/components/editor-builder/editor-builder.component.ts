import {AfterViewInit, Component, Input, ViewChild} from '@angular/core';
import {SplitterModule} from "primeng/splitter";
import {ButtonModule} from "primeng/button";
import {CodeEditorComponent} from "../code-editor/code-editor.component";
import {Parser} from "../../models/syntax/parser";
import {Text} from "@codemirror/state";
import {markdownToHtml} from "../../utils/markdown";
import {handleCodeErrors, LintErrors} from "../../models/syntax/utils";

@Component({
  selector: 'app-editor-builder',
  templateUrl: './editor-builder.component.html',
  styleUrl: './editor-builder.component.scss',
  standalone: true,
  imports: [
    SplitterModule,
    ButtonModule,
    CodeEditorComponent
  ]
})
export class EditorBuilderComponent implements AfterViewInit {
  @ViewChild('editor') protected editor!: CodeEditorComponent;
  @Input({required:true,alias:'content'}) public _content!: string;
  @Input({required:true}) public parser?: Parser;
  protected isDocVisible: boolean = true;

  public ngAfterViewInit() {
    this.editor.content = this._content;
  }

  public get content(): string {
    return this.editor.text;
  }

  protected get doc(): string {
    return this.parser ? markdownToHtml(this.parser.docBuilder) : '';
  }

  protected toggleDoc() {
    this.isDocVisible = !this.isDocVisible;
  }

  protected linter(text: Text): LintErrors {
    return handleCodeErrors(text, () => {
      if(!this.parser) return;
      this.parser.parseSrcBuilder(text.toString());
    });
  }
}
