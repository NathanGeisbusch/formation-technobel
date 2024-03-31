import {Component, EventEmitter, Input, OnDestroy, OnInit, Output, ViewChild} from '@angular/core';
import {Menu, MenuModule} from "primeng/menu";
import {MenuItem, SortEvent} from "primeng/api";
import {Observable, Subscription} from "rxjs";
import {PackagePrivateDTO, PageDTO} from "../../models/dto";
import {TableHeaderCheckbox, TableLazyLoadEvent, TableModule} from "primeng/table";
import {CheckboxChangeEvent, CheckboxModule} from "primeng/checkbox";
import {PrivatePackageSearchForm} from "../../models/form-search";
import {PackageVisibility, SortPrivatePackage} from "../../models/enum";
import {FormsModule} from "@angular/forms";
import {Location, NgForOf, NgIf, TitleCasePipe} from "@angular/common";
import {TimeAgoPipe} from "../../pipes/time-ago.pipe";
import {ButtonModule} from "primeng/button";
import {SidebarModule} from "primeng/sidebar";
import {InputTextModule} from "primeng/inputtext";
import {ActivatedRoute} from "@angular/router";
import {objectToQueryParams} from "../../utils/http";
import {RadioButtonClickEvent, RadioButtonModule} from "primeng/radiobutton";
import {PackagesDeleteForm} from "../../models/form";
import {fromPackageId} from "../../utils/package-validation";

interface SelectMenuItem {label: string, value?: string, disabled: boolean}

@Component({
  selector: 'app-table-packages',
  templateUrl: './table-packages.component.html',
  styleUrl: './table-packages.component.scss',
  standalone: true,
  imports: [
    TableModule,
    CheckboxModule,
    FormsModule,
    NgForOf,
    NgIf,
    TitleCasePipe,
    TimeAgoPipe,
    ButtonModule,
    SidebarModule,
    MenuModule,
    InputTextModule,
    RadioButtonModule
  ]
})
export class TablePackagesComponent implements OnInit, OnDestroy {
  @ViewChild('tableHeaderCheckbox') protected readonly tableHeaderCheckbox!: TableHeaderCheckbox;
  @ViewChild('menuCopy') protected readonly menuCopy!: Menu;
  @Input({required:true}) public menuItems!: MenuItem[];
  @Input({required:true}) public bookmarkView!: boolean;
  @Input({required:true}) public data!: Observable<PageDTO<PackagePrivateDTO>>;
  @Input({required:true}) public amountLabel!: (amount: number) => string;
  @Input({required:true}) public buildTryUrl!: (data: PackagePrivateDTO) => string;
  @Input({required:true}) public buildEditUrl!: (data: PackagePrivateDTO) => string;
  @Output() protected readonly openMenu = new EventEmitter<PackagePrivateDTO>;
  @Output() protected readonly closeMenu = new EventEmitter<void>;
  @Output() protected readonly search = new EventEmitter<PrivatePackageSearchForm>;
  @Output() protected readonly add = new EventEmitter<void>;
  @Output() protected readonly delete = new EventEmitter<{
    data: PackagePrivateDTO, allVersions: boolean
  }>;
  @Output() protected readonly bulkDelete = new EventEmitter<{
    form: PackagesDeleteForm, allVersions: boolean
  }>;
  protected pageData: PageDTO<PackagePrivateDTO>;
  protected versionFilter!: SelectMenuItem;
  protected visibilityFilters!: SelectMenuItem[];
  protected pageLinks: number;
  protected sidebarData?: PackagePrivateDTO;
  protected isLoading: boolean = true;
  protected readonly searchForm: PrivatePackageSearchForm;
  private readonly pageSize: number = 10;
  private _data$?: Subscription;
  private selectedRows: PackagePrivateDTO[] = [];

  public constructor(
    private readonly _location: Location,
    private readonly _activatedRoute: ActivatedRoute,
  ) {
    this.versionFilter = {label: 'Show all versions', disabled: false};
    this.visibilityFilters = [
      {label: 'Show all versions', disabled: false},
      {label: 'Show public versions', value: PackageVisibility.PUBLIC, disabled: true},
      {label: 'Show private versions', value: PackageVisibility.PRIVATE, disabled: true},
      {label: 'Show protected versions', value: PackageVisibility.PROTECTED, disabled: true},
    ];
    this.pageLinks = window.innerWidth < 640 ? 1 :
      window.innerWidth < 960 ? 3 :
        window.innerWidth < 1280 ? 5 : 10;
    this.pageData = {data: [], page: 0, pages: 0, size: this.pageSize, elements: 0};
    this.searchForm = {
      page: 0, size: this.pageSize, search: '', allVersions: false,
      sort: SortPrivatePackage.NAME_ASC, visibility: undefined,
    }
    const {page, search, allVersions, sort, visibility} = this._activatedRoute.snapshot.queryParams;
    if(page && !isNaN(parseInt(page))) {
      this.searchForm.page = parseInt(page);
      this.pageData.page = this.searchForm.page;
    }
    if(search) {
      this.searchForm.search = search;
    }
    if(allVersions === 'true') {
      this.searchForm.allVersions = true;
      if(visibility) {
        switch(visibility) {
          case PackageVisibility.PRIVATE:
          case PackageVisibility.PUBLIC:
          case PackageVisibility.PROTECTED:
            this.searchForm.visibility = visibility;
        }
      }
    }
    if(sort) {
      switch(sort) {
        case SortPrivatePackage.NAME_ASC:
        case SortPrivatePackage.NAME_DSC:
        case SortPrivatePackage.UPDATE_ASC:
        case SortPrivatePackage.UPDATE_DSC:
        case SortPrivatePackage.VERSION_ASC:
        case SortPrivatePackage.VERSION_DSC:
          this.searchForm.sort = sort;
      }
    }
  }

  public ngOnInit(): void {
    this._data$ = this.data.subscribe({
      next: (value) => {
        this.fixTableSelection();
        this.isLoading = false;
        this.pageData = value;
        this.updateLocation();
      },
      error: () => this.isLoading = false,
    });
    this.updateData();
  }

  public ngOnDestroy(): void {
    if(this._data$) this._data$.unsubscribe();
  }

  protected updateData() {
    this.search.emit(this.searchForm);
    this.isLoading = true;
  }

  private updateLocation() {
    let query = objectToQueryParams(this.searchForm);
    if(query) query = '?' + query;
    const baseUrl = this._location.path().split('?')[0];
    this._location.replaceState(baseUrl, query)
  }

  protected get sortField(): string {
    switch(this.searchForm.sort) {
      case SortPrivatePackage.NAME_ASC:
      case SortPrivatePackage.NAME_DSC:
        return 'name';
      case SortPrivatePackage.VERSION_ASC:
      case SortPrivatePackage.VERSION_DSC:
        return 'version';
      case SortPrivatePackage.UPDATE_ASC:
      case SortPrivatePackage.UPDATE_DSC:
        return 'updatedAt';
      default:
        return 'name';
    }
  }

  protected get sortOrder(): number {
    switch(this.searchForm.sort) {
      case SortPrivatePackage.NAME_ASC:
      case SortPrivatePackage.VERSION_ASC:
      case SortPrivatePackage.UPDATE_ASC:
        return -1;
      case SortPrivatePackage.NAME_DSC:
      case SortPrivatePackage.VERSION_DSC:
      case SortPrivatePackage.UPDATE_DSC:
        return 1;
      default:
        return -1;
    }
  }

  protected onPaginationChanged($event: TableLazyLoadEvent) {
    const page = $event.first! / $event.rows!;
    if(this.searchForm.page !== page) {
      this.searchForm.page = page;
      this.updateData();
    }
  }

  protected onSortChange($event: SortEvent) {
    let sort = SortPrivatePackage.NAME_DSC;
    switch($event.field) {
      case 'name':
        sort = $event.order === -1 ?
          SortPrivatePackage.NAME_ASC : SortPrivatePackage.NAME_DSC
        break;
      case 'version':
        sort = $event.order === -1 ?
          SortPrivatePackage.VERSION_ASC : SortPrivatePackage.VERSION_DSC
        break;
      case 'updatedAt':
        sort = $event.order === -1 ?
          SortPrivatePackage.UPDATE_ASC : SortPrivatePackage.UPDATE_DSC
        break;
    }
    if(this.searchForm.sort !== sort) {
      this.searchForm.sort = sort;
      this.updateData();
    }
  }

  protected onSearchChange($event: Event) {
    this.searchForm.search = ($event.target as HTMLInputElement).value;
    this.updateData();
  }

  protected onSearchClear() {
    this.searchForm.search = '';
    this.updateData();
  }

  protected onFilterVersion($event: CheckboxChangeEvent) {
    this.searchForm.allVersions = $event.checked;
    if(!$event.checked) this.searchForm.visibility = undefined;
    this.updateData();
  }

  protected onFilterVisibility($event: RadioButtonClickEvent) {
    this.searchForm.visibility = $event.value;
    this.updateData();
  }

  protected onSidebarVisibility($event: boolean) {
    if(!$event) this.sidebarData = undefined;
  }

  protected onMenuClose() {
    this.closeMenu.emit();
  }

  protected onMenuOpen($event: MouseEvent, data: PackagePrivateDTO) {
    this.openMenu.emit(data);
    this.menuCopy.show($event);
  }

  protected onAdd() {
    this.add.emit();
  }

  protected onDelete(data?: PackagePrivateDTO) {
    if(data) {
      this.delete.emit({data, allVersions: !this.searchForm.allVersions});
    } else {
      const id = this.selectedRows.map(row => fromPackageId(row));
      this.bulkDelete.emit({form: {id}, allVersions: !this.searchForm.allVersions})
    }
  }

  protected onSelection($event: PackagePrivateDTO[]) {
    this.selectedRows = $event;
  }

  /**
   * Fix: clear table to remove elements not present anymore
   * in the table but still present in selection.
   */
  private fixTableSelection() {
    setTimeout(() => {
      this.tableHeaderCheckbox.dt.selectionChange.emit(
        this.tableHeaderCheckbox.dt.selection = []
      );
    });
  }
}
