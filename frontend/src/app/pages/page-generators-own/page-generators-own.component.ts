import {Component, OnDestroy} from '@angular/core';
import {MenuItem} from "primeng/api";
import {Subject, Subscription} from "rxjs";
import {PackagePrivateDTO, PageDTO} from "../../models/dto";
import {NgIf} from "@angular/common";
import {TablePackagesComponent} from "../../components/table-packages/table-packages.component";
import {PrivatePackageSearchForm} from "../../models/form-search";
import {AppMessageService, DeleteConfirmResource, MsgReasonSuccess} from "../../services/app.message.service";
import {PackagesDeleteForm} from "../../models/form";
import {
  DialogCreateCopyGeneratorComponent
} from "../../components/dialog-create-copy-generator/dialog-create-copy-generator.component";
import {
  DialogCreateGeneratorComponent
} from "../../components/dialog-create-generator/dialog-create-generator.component";
import {GeneratorService} from "../../services/generator.service";
import {fromPackageId, PackageId} from "../../utils/package-validation";

@Component({
  selector: 'app-page-generators-own',
  templateUrl: './page-generators-own.component.html',
  styleUrl: './page-generators-own.component.scss',
  standalone: true,
  imports: [
    NgIf,
    DialogCreateCopyGeneratorComponent,
    DialogCreateGeneratorComponent,
    TablePackagesComponent,
  ]
})
export class PageGeneratorsOwnComponent implements OnDestroy {
  protected selectedGenerator?: PackageId;
  protected menuItems: MenuItem[];
  protected showDialogCreateGenerator: boolean = false;
  protected showDialogCreateCopyGenerator: boolean = false;
  protected data = new Subject<PageDTO<PackagePrivateDTO>>();
  private _data$?: Subscription;
  private _searchForm?: PrivatePackageSearchForm;

  public constructor(
    private readonly _generatorService: GeneratorService,
    private readonly _msgService: AppMessageService,
  ) {
    this.menuItems = [
      {label: 'Create a copy', icon: 'pi pi-clone', command: () => {
        this.showDialogCreateCopyGenerator = true;
      }},
    ];
  }

  ngOnDestroy(): void {
    if(this._data$) this._data$.unsubscribe();
  }

  protected amountLabel(amount: number): string {
    const plural = amount > 1 ? 's' : '';
    return `${amount} generator${plural} found`;
  }

  protected buildTryUrl(data: PackagePrivateDTO): string {
    return '/generators/try/'+data.author+':'+data.name+'@'+data.version;
  }

  protected buildEditUrl(data: PackagePrivateDTO): string {
    return '/generators/edit/'+data.author+':'+data.name+'@'+data.version;
  }

  protected onMenuClose() {
    this.selectedGenerator = undefined;
  }

  protected onMenuOpen(data: PackagePrivateDTO) {
    this.selectedGenerator = {author: data.author, name: data.name, version: data.version};
  }

  protected onSearch($event: PrivatePackageSearchForm) {
    if(this._data$) this._data$.unsubscribe();
    this._searchForm = $event;
    this._data$ = this._generatorService.findOwn($event).subscribe({
      next: (value) => this.data.next(value),
      error: (err) => this.data.error(err),
    });
  }

  protected onAdd() {
    this.showDialogCreateGenerator = true;
  }

  protected onDelete($event: {data: PackagePrivateDTO; allVersions: boolean}) {
    const {data, allVersions} = $event;
    this._msgService.deleteConfirmDialog(
      DeleteConfirmResource.GENERATOR, false,
      () => {
        this._generatorService.delete(fromPackageId(data), allVersions).subscribe({
          next: () => {
            this._msgService.showSuccess(MsgReasonSuccess.GENERATOR_DELETE);
            if(this._searchForm) this.onSearch(this._searchForm);
          }
        })
      }
    );
  }

  protected onBulkDelete($event: {form: PackagesDeleteForm; allVersions: boolean}) {
    const {form, allVersions} = $event;
    const plural = form.id.length > 1;
    this._msgService.deleteConfirmDialog(
      DeleteConfirmResource.GENERATOR, plural,
      () => {
        this._generatorService.bulkDelete(form, allVersions).subscribe({
          next: () => {
            this._msgService.showSuccess(plural ?
              MsgReasonSuccess.GENERATORS_DELETE : MsgReasonSuccess.GENERATOR_DELETE
            );
            if(this._searchForm) this.onSearch(this._searchForm);
          }
        })
      }
    );
  }

  protected reloadData() {
    if(this._searchForm) this.onSearch(this._searchForm);
  }
}
