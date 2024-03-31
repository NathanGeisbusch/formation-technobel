import {AfterViewInit, Component, ElementRef, Input, OnDestroy, ViewChild} from '@angular/core';
import { basicSetup } from 'codemirror';
import {EditorState, Extension, Text} from '@codemirror/state';
import { EditorView } from '@codemirror/view';
import {
  oneDark,
} from '@codemirror/theme-one-dark';
import {ThemeService} from "../../services/theme.service";
import {Subscription} from "rxjs";
import { linter, lintGutter } from '@codemirror/lint';
import {Linter} from "../../models/syntax/utils";

@Component({
  selector: 'app-code-editor',
  templateUrl: './code-editor.component.html',
  styleUrl: './code-editor.component.scss',
  standalone: true,
})
export class CodeEditorComponent implements AfterViewInit, OnDestroy {
  @ViewChild('codeEditor') protected codeEditor!: ElementRef;
  @Input() linter?: Linter;
  @Input() readOnly?: boolean;
  private view?: EditorView;
  private extensions: Extension = this.getExtensions(false);
  private _darkTheme$?: Subscription;

  public constructor(private readonly _themeService: ThemeService) {}

  public set content(txt: string) {
    let state = EditorState.create({doc: txt, extensions: this.extensions});
    this.view!.setState(state);
  }

  public get content(): Text {
    return this.view!.state.doc;
  }

  public get text(): string {
    return this.view!.state.doc.toString();
  }

  public set cursorOffset(offset: number) {
    this.view!.focus();
    this.view!.dispatch({
      selection: { head: offset, anchor: offset },
      scrollIntoView: true
    });
  }

  public set cursorLineAtOffset(offset: number) {
    const line = this.view!.state.doc.lineAt(offset);
    this.view!.focus();
    this.view!.dispatch({
      selection: { head: line.from, anchor: line.from },
      scrollIntoView: true
    });
  }

  public ngAfterViewInit() {
    this.view = new EditorView({parent: this.codeEditor.nativeElement});
    this._darkTheme$ = this._themeService.darkTheme.subscribe((darkTheme) => {
      const state = this.view!.state;
      this.extensions = this.getExtensions(darkTheme);
      this.view!.setState(
        EditorState.create({doc: state.doc, extensions: this.extensions})
      );
    });
  }

  public ngOnDestroy() {
    if(this._darkTheme$) this._darkTheme$.unsubscribe();
  }

  private getExtensions(darkTheme: boolean): Extension {
    const ext = [basicSetup, lintGutter()];
    if(darkTheme) ext.push(oneDark);
    if(this.readOnly) ext.push(EditorView.editable.of(false));
    if(this.linter) ext.push(
      linter(view => {
        const diagnostics = this.linter!(view.state.doc);
        if(Array.isArray(diagnostics)) return diagnostics;
        if(diagnostics) return [diagnostics];
        return [];
      })
    );
    return ext;
  }
}
