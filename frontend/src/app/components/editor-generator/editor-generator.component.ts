import {AfterViewInit, Component, Input, ViewChild} from '@angular/core';
import {SplitterModule} from "primeng/splitter";
import {ButtonModule} from "primeng/button";
import {CodeEditorComponent} from "../code-editor/code-editor.component";
import {Text} from "@codemirror/state";
import {markdownToHtml} from "../../utils/markdown";
import {Generator} from "../../models/syntax/generator";
import {TreeModule} from "primeng/tree";
import {TreeNode} from "primeng/api";
import {handleCodeErrors, LintErrors} from "../../models/syntax/utils";

@Component({
  selector: 'app-editor-generator',
  templateUrl: './editor-generator.component.html',
  styleUrl: './editor-generator.component.scss',
  standalone: true,
  imports: [
    SplitterModule,
    ButtonModule,
    CodeEditorComponent,
    TreeModule
  ]
})
export class EditorGeneratorComponent implements AfterViewInit {
  @ViewChild('editor') protected editor!: CodeEditorComponent;
  @Input({required:true,alias:'content'}) public _content!: string;
  @Input({required:true}) public generator?: Generator;
  protected isDocVisible: boolean = true;
  protected isFunctionsVisible: boolean = false;
  protected functions: TreeNode[] = [];

  public ngAfterViewInit() {
    this.editor.content = this._content;
  }

  public get content(): string {
    return this.editor.text;
  }

  protected get doc(): string {
    return this.generator ? markdownToHtml(this.generator.docGenerator) : '';
  }

  protected toggleDoc() {
    this.isDocVisible = !this.isDocVisible;
    this.isFunctionsVisible = false;
  }

  protected toggleFunctionsIndex() {
    this.isFunctionsVisible = !this.isFunctionsVisible;
    this.isDocVisible = false;
  }
  protected onSelectFunction($event: any) {
    if(!$event) return;
    this.editor.cursorLineAtOffset = $event.data;
  }

  protected linter(text: Text): LintErrors {
    return handleCodeErrors(text, () => {
      if(!this.generator) return;
      const generatorSyntax = this.generator.parseSrcGenerator(text.toString());
      this.functions = Object.values<any>(generatorSyntax).sort((a,b) => a.offset-b.offset)
        .map(fct => ({key: fct.name, label: fct.name, data: fct.offset})) as TreeNode[];
    });
  }
}
