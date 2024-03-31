import {AfterViewInit, Component} from '@angular/core';
import {PackagePrivateDTO} from "../../models/dto";
import {ToolbarModule} from "primeng/toolbar";
import {
  EditorInputParserComponent,
  ParserData
} from "../../components/editor-input-parser/editor-input-parser.component";
import {Parser} from "../../models/syntax/parser";
import {SyntaxVersionService} from "../../services/syntax.version.service";
import {ActivatedRoute, Router} from "@angular/router";
import {fromBase64Url} from "../../utils/base64";
import {REGEX_PACKAGE_ID} from "../../utils/package-validation";
import {forkJoin} from "rxjs";
import {ParserService} from "../../services/parser.service";

interface ParserPackage {
  info: PackagePrivateDTO,
  parser: string,
  builder: string,
  doc: string,
}

interface ProtectedId {
  id: string,
  password: string,
}

@Component({
  selector: 'app-page-shared-parser',
  templateUrl: './page-shared-parser.component.html',
  styleUrl: './page-shared-parser.component.scss',
  standalone: true,
  imports: [
    ToolbarModule,
    EditorInputParserComponent,
  ]
})
export class PageSharedParserComponent implements AfterViewInit {
  protected readonly id: string = '';
  protected readonly password: string = '';
  protected package?: ParserPackage;
  protected parser?: Parser;

  constructor(
    private readonly _parserService: ParserService,
    private readonly _syntaxService: SyntaxVersionService,
    private readonly _router: Router,
    activatedRoute: ActivatedRoute
  ) {
    try {
      const id: ProtectedId = JSON.parse(fromBase64Url(activatedRoute.snapshot.params['id']));
      if(REGEX_PACKAGE_ID.test(id.id)) {
        this.id = id.id;
        this.password = id.password;
      }
      else this._router.navigate(['404'], {skipLocationChange: true}).then();
    }
    catch(ex) {
      this._router.navigate(['404'], {skipLocationChange: true}).then();
    }
  }

  public ngAfterViewInit() {
    forkJoin({
      info: this._parserService.getProtected(this.id, this.password),
      parser: this._parserService.getParserCode(this.id, this.password),
      builder: this._parserService.getBuilderCode(this.id, this.password),
      doc: this._parserService.getDocCode(this.id, this.password),
    }).subscribe({
      next: (value) => {
        this.package = value;
        this.parser = this._syntaxService.getParser(value.info.parserSyntax)!;
      },
      error: () => {
        this._router.navigate(['404'], {skipLocationChange: true}).then();
      }
    });
  }

  protected get data(): ParserData|null {
    if(this.parser && this.package) {
      return {
        parser: this.parser,
        parserCode: this.package.parser,
        builderCode: this.package.builder,
      }
    }
    return null;
  }
}
