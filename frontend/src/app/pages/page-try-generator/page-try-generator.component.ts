import {AfterViewInit, Component, OnDestroy, ViewChild} from '@angular/core';
import {ToolbarModule} from "primeng/toolbar";
import {DropdownChangeEvent, DropdownModule} from "primeng/dropdown";
import {SplitButtonModule} from "primeng/splitbutton";
import {TabViewModule} from "primeng/tabview";
import {
  EditorInputGeneratorComponent,
  GeneratorData
} from "../../components/editor-input-generator/editor-input-generator.component";
import {DialogCreateSessionComponent} from "../../components/dialog-create-session/dialog-create-session.component";
import {NgIf} from "@angular/common";
import {FormsModule} from "@angular/forms";
import {Parser} from "../../models/syntax/parser";
import {Generator} from "../../models/syntax/generator";
import {AuthService} from "../../services/auth.service";
import {GeneratorService} from "../../services/generator.service";
import {SearchService} from "../../services/search.service";
import {ActivatedRoute, Router} from "@angular/router";
import {fromPackageId, PackageId, REGEX_PACKAGE_ID, REGEX_VERSION, toPackageId} from "../../utils/package-validation";
import {forkJoin, Subscription} from "rxjs";
import {AccountDTO, PackagePublicDTO} from "../../models/dto";
import {SyntaxVersionService} from "../../services/syntax.version.service";
import {SessionService} from "../../services/session.service";

interface GeneratorPackage {
  info: PackagePublicDTO,
  parser: string,
  builder: string,
  generator: string,
  doc: string,
  versions: string[],
}

@Component({
  selector: 'app-page-try-generator',
  templateUrl: './page-try-generator.component.html',
  styleUrl: './page-try-generator.component.scss',
  standalone: true,
  imports: [
    ToolbarModule,
    DropdownModule,
    SplitButtonModule,
    TabViewModule,
    EditorInputGeneratorComponent,
    DialogCreateSessionComponent,
    NgIf,
    FormsModule
  ]
})
export class PageTryGeneratorComponent implements AfterViewInit, OnDestroy {
  @ViewChild('inputEditor') protected inputEditor!: EditorInputGeneratorComponent;
  protected readonly id: string = '';
  protected package?: GeneratorPackage;
  protected selectedVersion: string = '';
  protected showDialogCreateSession: boolean = false;
  protected parser?: Parser;
  protected generator?: Generator;
  protected user: AccountDTO|null = null;
  private readonly _user$?: Subscription;

  constructor(
    private readonly _auth: AuthService,
    private readonly _generatorService: GeneratorService,
    private readonly _sessionService: SessionService,
    private readonly _searchService: SearchService,
    private readonly _syntaxService: SyntaxVersionService,
    private readonly _router: Router,
    activatedRoute: ActivatedRoute
  ) {
    const id = activatedRoute.snapshot.params['id'];
    if(REGEX_PACKAGE_ID.test(id)) this.id = id;
    else this._router.navigate(['404'], {skipLocationChange: true}).then();
    this._user$ = this._auth.authenticatedUser.subscribe((value) => {
      if(value) this._auth.account().subscribe((value) => {
        this.user = value;
      });
    });
  }

  public ngOnDestroy() {
    if(this._user$) this._user$.unsubscribe();
  }

  public ngAfterViewInit() {
    const id = toPackageId(this.id)!;
    this.selectedVersion = id.version;
    forkJoin({
      info: this._generatorService.getPublic(this.id),
      parser: this._generatorService.getParserCode(this.id),
      builder: this._generatorService.getBuilderCode(this.id),
      generator: this._generatorService.getGeneratorCode(this.id),
      doc: this._generatorService.getDocCode(this.id),
      versions: this._searchService.findGeneratorVersions(id.author, id.name, ''),
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

  protected get packageId(): PackageId {
    return toPackageId(this.id)!;
  }

  protected createSession() {
    this.showDialogCreateSession = true;
  }

  protected onChangeVersion($event: DropdownChangeEvent) {
    if(REGEX_VERSION.test($event.value)) {
      if(this.package!.versions.includes($event.value)) {
        const id = toPackageId(this.id)!;
        id.version = $event.value;
        this._auth.redirectTo('/generators/try/'+fromPackageId(id));
      }
    }
    else this.updateVersions($event.value);
  }

  private updateVersions(version: string) {
    const id = toPackageId(this.id)!;
    this._searchService.findGeneratorVersions(id.author, id.name, version).subscribe({
      next: (versions) => this.package!.versions = versions,
    });
  }

  protected onCreateSession(name: string) {
    this._sessionService.updateInputText(name, this.inputEditor.content).subscribe({
      next: () => this._router.navigate(['/sessions/'+name]).then()
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

  protected dislike() {
    const info = this.package!.info;
    if(info.liked === false) {
      this._generatorService.like(this.id, null).subscribe({
        next: () => {
          info.dislikes--;
          info.liked = null;
        }
      });
    } else {
      this._generatorService.like(this.id, false).subscribe({
        next: () => {
          if(info.liked === true) info.likes--;
          info.dislikes++;
          info.liked = false;
        }
      });
    }
  }

  protected like() {
    const info = this.package!.info;
    if(info.liked === true) {
      this._generatorService.like(this.id, null).subscribe({
        next: () => {
          info.likes--;
          info.liked = null;
        }
      });
    } else {
      this._generatorService.like(this.id, true).subscribe({
        next: () => {
          if(info.liked === false) info.dislikes--;
          info.likes++;
          info.liked = true;
        }
      });
    }
  }

  protected bookmark() {
    const info = this.package!.info;
    this._generatorService.bookmark(this.id, !info.bookmarked).subscribe({
      next: () => {
        info.bookmarked = !info.bookmarked;
      }
    });
  }
}
