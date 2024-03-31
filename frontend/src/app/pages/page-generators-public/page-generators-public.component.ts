import { Component } from '@angular/core';
import {MenuItem} from "primeng/api";
import {PublicPackageSearchForm} from "../../models/form-search";
import {Observable} from "rxjs";
import {PackagePublicDTO, PageDTO} from "../../models/dto";
import {ViewPackagesComponent} from "../../components/view-packages/view-packages.component";
import {NgIf} from "@angular/common";
import {GeneratorService} from "../../services/generator.service";
import {
  DialogCreateCopyGeneratorComponent
} from "../../components/dialog-create-copy-generator/dialog-create-copy-generator.component";
import {fromPackageId, PackageId} from "../../utils/package-validation";

@Component({
  selector: 'app-page-generators-public',
  templateUrl: './page-generators-public.component.html',
  styleUrl: './page-generators-public.component.scss',
  standalone: true,
  imports: [
    ViewPackagesComponent,
    NgIf,
    DialogCreateCopyGeneratorComponent
  ]
})
export class PageGeneratorsPublicComponent {
  protected selectedGenerator?: PackageId;
  protected menuItems: MenuItem[];
  protected showDialogCreateCopyGenerator: boolean = false;

  public constructor(private readonly _generatorService: GeneratorService) {
    this.menuItems = [
      {label: 'Create a copy', icon: 'pi pi-clone', command: () => {
        this.showDialogCreateCopyGenerator = true;
      }},
    ];
  }

  protected loadData = (form: PublicPackageSearchForm): Observable<PageDTO<PackagePublicDTO>> => {
    return this._generatorService.findPublic(form);
  }

  protected like = (data: PackagePublicDTO, value: boolean|null): Observable<void> => {
    return this._generatorService.like(fromPackageId(data), value);
  }

  protected bookmark = (data: PackagePublicDTO, value: boolean): Observable<void> => {
    return this._generatorService.bookmark(fromPackageId(data), value);
  }

  protected amountLabel(amount: number): string {
    const plural = amount > 1 ? 's' : '';
    return `${amount} generator${plural} found`;
  }

  protected buildTryUrl(data: PackagePublicDTO): string {
    return `/generators/try/${data.author}:${data.name}@${data.version}`;
  }

  protected onMenuClose() {
    this.selectedGenerator = undefined;
  }

  protected onMenuOpen(data: PackagePublicDTO) {
    this.selectedGenerator = {author: data.author, name: data.name, version: data.version};
  }
}
