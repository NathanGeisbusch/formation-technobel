import {Component, OnDestroy} from '@angular/core';
import {MenuItem} from "primeng/api";
import {Subject, Subscription} from "rxjs";
import {PackagePrivateDTO, PageDTO} from "../../models/dto";
import {
  DialogCreateCopyParserComponent
} from "../../components/dialog-create-copy-parser/dialog-create-copy-parser.component";
import {
  DialogCreateGeneratorComponent
} from "../../components/dialog-create-generator/dialog-create-generator.component";
import {NgIf} from "@angular/common";
import {TablePackagesComponent} from "../../components/table-packages/table-packages.component";
import {ParserService} from "../../services/parser.service";
import {PrivatePackageSearchForm} from "../../models/form-search";
import {AppMessageService, DeleteConfirmResource, MsgReasonSuccess} from "../../services/app.message.service";
import {DialogCreateParserComponent} from "../../components/dialog-create-parser/dialog-create-parser.component";
import {PackagesDeleteForm} from "../../models/form";
import {fromPackageId, PackageId} from "../../utils/package-validation";

@Component({
  selector: 'app-page-parsers-own',
  templateUrl: './page-parsers-own.component.html',
  styleUrl: './page-parsers-own.component.scss',
  standalone: true,
  imports: [
    NgIf,
    DialogCreateCopyParserComponent,
    DialogCreateGeneratorComponent,
    TablePackagesComponent,
    DialogCreateParserComponent
  ]
})
export class PageParsersOwnComponent implements OnDestroy {
  protected selectedParser?: PackageId;
  protected menuItems: MenuItem[];
  protected showDialogCreateParser: boolean = false;
  protected showDialogCreateCopyParser: boolean = false;
  protected showDialogCreateGenerator: boolean = false;
  protected data = new Subject<PageDTO<PackagePrivateDTO>>();
  private _data$?: Subscription;
  private _searchForm?: PrivatePackageSearchForm;

  public constructor(
    private readonly _parserService: ParserService,
    private readonly _msgService: AppMessageService,
  ) {
    this.menuItems = [
      {label: 'Create a copy', icon: 'pi pi-clone', command: () => {
          this.showDialogCreateCopyParser = true;
        }},
      {label: 'Create a generator', icon: 'pi pi-plus', command: () => {
          this.showDialogCreateGenerator = true;
        }},
    ];
  }

  ngOnDestroy(): void {
    if(this._data$) this._data$.unsubscribe();
  }

  protected amountLabel(amount: number): string {
    const plural = amount > 1 ? 's' : '';
    return `${amount} parser${plural} found`;
  }

  protected buildTryUrl(data: PackagePrivateDTO): string {
    return '/parsers/try/'+data.author+':'+data.name+'@'+data.version;
  }

  protected buildEditUrl(data: PackagePrivateDTO): string {
    return '/parsers/edit/'+data.author+':'+data.name+'@'+data.version;
  }

  protected onMenuClose() {
    this.selectedParser = undefined;
  }

  protected onMenuOpen(data: PackagePrivateDTO) {
    this.selectedParser = {author: data.author, name: data.name, version: data.version};
  }

  protected onSearch($event: PrivatePackageSearchForm) {
    if(this._data$) this._data$.unsubscribe();
    this._searchForm = $event;
    this._data$ = this._parserService.findOwn($event).subscribe({
      next: (value) => this.data.next(value),
      error: (err) => this.data.error(err),
    });
  }

  protected onAdd() {
    this.showDialogCreateParser = true;
  }

  protected onDelete($event: {data: PackagePrivateDTO; allVersions: boolean}) {
    const {data, allVersions} = $event;
    this._msgService.deleteConfirmDialog(
      DeleteConfirmResource.PARSER, false,
      () => {
        this._parserService.delete(fromPackageId(data), allVersions).subscribe({
          next: () => {
            this._msgService.showSuccess(MsgReasonSuccess.PARSER_DELETE);
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
      DeleteConfirmResource.PARSER, plural,
      () => {
        this._parserService.bulkDelete(form, allVersions).subscribe({
          next: () => {
            this._msgService.showSuccess(plural ?
              MsgReasonSuccess.PARSERS_DELETE : MsgReasonSuccess.PARSER_DELETE
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
