import {AfterViewInit, Component, OnDestroy} from '@angular/core';
import {ToolbarModule} from "primeng/toolbar";
import {ButtonModule} from "primeng/button";
import {DropdownChangeEvent, DropdownModule} from "primeng/dropdown";
import {FormsModule} from "@angular/forms";
import {
  EditorInputParserComponent,
  ParserData
} from "../../components/editor-input-parser/editor-input-parser.component";
import {AccountDTO, PackagePublicDTO} from "../../models/dto";
import {Parser} from "../../models/syntax/parser";
import {forkJoin, Subscription} from "rxjs";
import {AuthService} from "../../services/auth.service";
import {SearchService} from "../../services/search.service";
import {SyntaxVersionService} from "../../services/syntax.version.service";
import {ActivatedRoute, Router} from "@angular/router";
import {fromPackageId, REGEX_PACKAGE_ID, REGEX_VERSION, toPackageId} from "../../utils/package-validation";
import {ParserService} from "../../services/parser.service";

interface ParserPackage {
  info: PackagePublicDTO,
  parser: string,
  builder: string,
  doc: string,
  versions: string[],
}

@Component({
  selector: 'app-page-try-parser',
  templateUrl: './page-try-parser.component.html',
  styleUrl: './page-try-parser.component.scss',
  standalone: true,
  imports: [
    ToolbarModule,
    ButtonModule,
    DropdownModule,
    FormsModule,
    EditorInputParserComponent
  ]
})
export class PageTryParserComponent implements AfterViewInit, OnDestroy {
  protected readonly id: string = '';
  protected package?: ParserPackage;
  protected selectedVersion: string = '';
  protected parser?: Parser;
  protected user: AccountDTO|null = null;
  private readonly _user$?: Subscription;

  constructor(
    private readonly _auth: AuthService,
    private readonly _parserService: ParserService,
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
      info: this._parserService.getPublic(this.id),
      parser: this._parserService.getParserCode(this.id),
      builder: this._parserService.getBuilderCode(this.id),
      doc: this._parserService.getDocCode(this.id),
      versions: this._searchService.findParserVersions(id.author, id.name, ''),
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

  protected onChangeVersion($event: DropdownChangeEvent) {
    if(REGEX_VERSION.test($event.value)) {
      if(this.package!.versions.includes($event.value)) {
        const id = toPackageId(this.id)!;
        id.version = $event.value;
        this._auth.redirectTo('/parsers/try/'+fromPackageId(id));
      }
    }
    else this.updateVersions($event.value);
  }

  private updateVersions(version: string) {
    const id = toPackageId(this.id)!;
    this._searchService.findParserVersions(id.author, id.name, version).subscribe({
      next: (versions) => this.package!.versions = versions,
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

  protected dislike() {
    const info = this.package!.info;
    if(info.liked === false) {
      this._parserService.like(this.id, null).subscribe({
        next: () => {
          info.dislikes--;
          info.liked = null;
        }
      });
    } else {
      this._parserService.like(this.id, false).subscribe({
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
      this._parserService.like(this.id, null).subscribe({
        next: () => {
          info.likes--;
          info.liked = null;
        }
      });
    } else {
      this._parserService.like(this.id, true).subscribe({
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
    this._parserService.bookmark(this.id, !info.bookmarked).subscribe({
      next: () => {
        info.bookmarked = !info.bookmarked;
      }
    });
  }
}
