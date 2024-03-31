import { Component } from '@angular/core';
import {MenuItem} from "primeng/api";
import {ParserService} from "../../services/parser.service";
import {PackagePublicDTO, PageDTO} from "../../models/dto";
import {Observable} from "rxjs";
import {PublicPackageSearchForm} from "../../models/form-search";
import {ViewPackagesComponent} from "../../components/view-packages/view-packages.component";
import {
  DialogCreateCopyParserComponent
} from "../../components/dialog-create-copy-parser/dialog-create-copy-parser.component";
import {
  DialogCreateGeneratorComponent
} from "../../components/dialog-create-generator/dialog-create-generator.component";
import {NgIf} from "@angular/common";
import {fromPackageId, PackageId} from "../../utils/package-validation";

@Component({
  selector: 'app-page-parsers-public',
  templateUrl: './page-parsers-public.component.html',
  styleUrl: './page-parsers-public.component.scss',
  standalone: true,
  imports: [
    ViewPackagesComponent,
    DialogCreateCopyParserComponent,
    DialogCreateGeneratorComponent,
    NgIf
  ]
})
export class PageParsersPublicComponent {
  protected selectedParser?: PackageId;
  protected menuItems: MenuItem[];
  protected showDialogCreateCopyParser: boolean = false;
  protected showDialogCreateGenerator: boolean = false;

  public constructor(private readonly _parserService: ParserService) {
    this.menuItems = [
      {label: 'Create a copy', icon: 'pi pi-clone', command: () => {
        this.showDialogCreateCopyParser = true;
      }},
      {label: 'Create a generator', icon: 'pi pi-plus', command: () => {
        this.showDialogCreateGenerator = true;
      }},
    ];
  }

  protected loadData = (form: PublicPackageSearchForm): Observable<PageDTO<PackagePublicDTO>> => {
    return this._parserService.findPublic(form);
  }

  protected like = (data: PackagePublicDTO, value: boolean|null): Observable<void> => {
    return this._parserService.like(fromPackageId(data), value);
  }

  protected bookmark = (data: PackagePublicDTO, value: boolean): Observable<void> => {
    return this._parserService.bookmark(fromPackageId(data), value);
  }

  protected amountLabel(amount: number): string {
    const plural = amount > 1 ? 's' : '';
    return `${amount} parser${plural} found`;
  }

  protected buildTryUrl(data: PackagePublicDTO): string {
    return `/parsers/try/${data.author}:${data.name}@${data.version}`;
  }

  protected onMenuClose() {
    this.selectedParser = undefined;
  }

  protected onMenuOpen(data: PackagePublicDTO) {
    this.selectedParser = {author: data.author, name: data.name, version: data.version};
  }
}
