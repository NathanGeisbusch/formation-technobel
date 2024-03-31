import {AfterViewInit, Component} from '@angular/core';
import {ToolbarModule} from "primeng/toolbar";
import {
  EditorInputGeneratorComponent,
  GeneratorData
} from "../../components/editor-input-generator/editor-input-generator.component";
import {Parser} from "../../models/syntax/parser";
import {Generator} from "../../models/syntax/generator";
import {PackagePrivateDTO} from "../../models/dto";
import {GeneratorService} from "../../services/generator.service";
import {SyntaxVersionService} from "../../services/syntax.version.service";
import {ActivatedRoute, Router} from "@angular/router";
import {REGEX_PACKAGE_ID} from "../../utils/package-validation";
import {forkJoin} from "rxjs";
import {fromBase64Url} from "../../utils/base64";

interface GeneratorPackage {
  info: PackagePrivateDTO,
  parser: string,
  builder: string,
  generator: string,
  doc: string,
}

interface ProtectedId {
  id: string,
  password: string,
}

@Component({
  selector: 'app-page-shared-generator',
  templateUrl: './page-shared-generator.component.html',
  styleUrl: './page-shared-generator.component.scss',
  standalone: true,
  imports: [
    ToolbarModule,
    EditorInputGeneratorComponent
  ]
})
export class PageSharedGeneratorComponent implements AfterViewInit {
  protected readonly id: string = '';
  protected readonly password: string = '';
  protected package?: GeneratorPackage;
  protected parser?: Parser;
  protected generator?: Generator;

  constructor(
    private readonly _generatorService: GeneratorService,
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
      info: this._generatorService.getProtected(this.id, this.password),
      parser: this._generatorService.getParserCode(this.id, this.password),
      builder: this._generatorService.getBuilderCode(this.id, this.password),
      generator: this._generatorService.getGeneratorCode(this.id, this.password),
      doc: this._generatorService.getDocCode(this.id, this.password),
    }).subscribe({
      next: (value) => {
        this.package = value;
        this.parser = this._syntaxService.getParser(value.info.parserSyntax)!;
        this.generator = this._syntaxService.getGenerator(value.info.generatorSyntax!)!;
      },
      error: () => {
        this._router.navigate(['404'], {skipLocationChange: true}).then();
      }
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
