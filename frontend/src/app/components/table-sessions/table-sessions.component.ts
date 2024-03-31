import {Component, EventEmitter, Input, OnDestroy, OnInit, Output, ViewChild} from "@angular/core";
import {TableHeaderCheckbox, TableLazyLoadEvent, TableModule} from "primeng/table";
import {Observable, Subscription} from "rxjs";
import {PageDTO, SessionDTO} from "../../models/dto";
import {SessionSearchForm} from "../../models/form-search";
import {PackagesDeleteForm} from "../../models/form";
import { Location } from "@angular/common";
import {ActivatedRoute, RouterLink} from "@angular/router";
import {SortSession} from "../../models/enum";
import {objectToQueryParams} from "../../utils/http";
import {SortEvent} from "primeng/api";
import {InputTextModule} from "primeng/inputtext";
import {ButtonModule} from "primeng/button";
import {TimeAgoPipe} from "../../pipes/time-ago.pipe";

@Component({
  selector: 'app-table-sessions',
  templateUrl: './table-sessions.component.html',
  styleUrl: './table-sessions.component.scss',
  standalone: true,
  imports: [
    TableModule,
    InputTextModule,
    ButtonModule,
    RouterLink,
    TimeAgoPipe
  ]
})
export class TableSessionsComponent implements OnInit, OnDestroy {
  @ViewChild('tableHeaderCheckbox') protected readonly tableHeaderCheckbox!: TableHeaderCheckbox;
  @Input({required:true}) public data!: Observable<PageDTO<SessionDTO>>;
  @Input({required:true}) public amountLabel!: (amount: number) => string;
  @Input({required:true}) public buildEditUrl!: (data: SessionDTO) => string;
  @Output() protected readonly search = new EventEmitter<SessionSearchForm>;
  @Output() protected readonly delete = new EventEmitter<SessionDTO>;
  @Output() protected readonly bulkDelete = new EventEmitter<PackagesDeleteForm>;
  protected pageData: PageDTO<SessionDTO>;
  protected pageLinks: number;
  protected isLoading: boolean = true;
  protected readonly searchForm: SessionSearchForm;
  private readonly pageSize: number = 10;
  private _data$?: Subscription;
  private selectedRows: SessionDTO[] = [];

  public constructor(
    private readonly _location: Location,
    private readonly _activatedRoute: ActivatedRoute,
  ) {
    this.pageLinks = window.innerWidth < 640 ? 1 :
      window.innerWidth < 960 ? 3 :
        window.innerWidth < 1280 ? 5 : 10;
    this.pageData = {data: [], page: 0, pages: 0, size: this.pageSize, elements: 0};
    this.searchForm = {page: 0, size: this.pageSize, search: '', sort: SortSession.NAME_ASC};
    const {page, search, sort} = this._activatedRoute.snapshot.queryParams;
    if(page && !isNaN(parseInt(page))) {
      this.searchForm.page = parseInt(page);
      this.pageData.page = this.searchForm.page;
    }
    if(search) {
      this.searchForm.search = search;
    }
    if(sort) {
      switch(sort) {
        case SortSession.NAME_ASC:
        case SortSession.NAME_DSC:
        case SortSession.UPDATE_ASC:
        case SortSession.UPDATE_DSC:
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
      case SortSession.NAME_ASC:
      case SortSession.NAME_DSC:
        return 'name';
      case SortSession.UPDATE_ASC:
      case SortSession.UPDATE_DSC:
        return 'updatedAt';
      default:
        return 'name';
    }
  }

  protected get sortOrder(): number {
    switch(this.searchForm.sort) {
      case SortSession.NAME_ASC:
      case SortSession.UPDATE_ASC:
        return -1;
      case SortSession.NAME_DSC:
      case SortSession.UPDATE_DSC:
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
    let sort = SortSession.NAME_DSC;
    switch($event.field) {
      case 'name':
        sort = $event.order === -1 ?
          SortSession.NAME_ASC : SortSession.NAME_DSC
        break;
      case 'updatedAt':
        sort = $event.order === -1 ?
          SortSession.UPDATE_ASC : SortSession.UPDATE_DSC
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

  protected onDelete(data?: SessionDTO) {
    if(data) {
      this.delete.emit(data);
    } else {
      const id = this.selectedRows.map(row => row.name);
      this.bulkDelete.emit({id})
    }
  }

  protected onSelection($event: SessionDTO[]) {
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
