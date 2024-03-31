import {AfterViewInit, Component, Input, ViewChild} from '@angular/core';
import {SplitterModule} from "primeng/splitter";
import {ButtonModule} from "primeng/button";
import {CodeEditorComponent} from "../code-editor/code-editor.component";
import {Text} from "@codemirror/state";
import {markdownToHtml} from "../../utils/markdown";
import {LintErrors} from "../../models/syntax/utils";

@Component({
  selector: 'app-editor-doc',
  templateUrl: './editor-doc.component.html',
  styleUrl: './editor-doc.component.scss',
  standalone: true,
  imports: [
    SplitterModule,
    ButtonModule,
    CodeEditorComponent
  ]
})
export class EditorDocComponent implements AfterViewInit {
  @ViewChild('editor') protected editor!: CodeEditorComponent;
  @Input({required:true,alias:'content'}) public _content!: string;
  protected isDocVisible: boolean = true;
  protected doc: string = '';

  public ngAfterViewInit() {
    this.editor.content = this._content;
  }

  public get content(): string {
    return this.editor.text;
  }

  protected toggleDoc() {
    this.isDocVisible = !this.isDocVisible;
  }

  protected linter(text: Text): LintErrors {
    this.doc = markdownToHtml(text.toString());
    return;
  }
}
