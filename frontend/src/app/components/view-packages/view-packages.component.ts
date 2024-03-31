import {Component, EventEmitter, Input, OnDestroy, Output, ViewChild} from '@angular/core';
import {Menu, MenuModule} from "primeng/menu";
import {AccountDTO, PackagePublicDTO, PageDTO} from "../../models/dto";
import {MenuItem} from "primeng/api";
import {AuthService} from "../../services/auth.service";
import {DataViewLazyLoadEvent, DataViewModule} from "primeng/dataview";
import {Observable, Subscription} from "rxjs";
import {PublicPackageSearchForm} from "../../models/form-search";
import {SortPublicPackage} from "../../models/enum";
import {InputTextModule} from "primeng/inputtext";
import {DropdownModule} from "primeng/dropdown";
import {FormsModule} from "@angular/forms";
import {NgClass, NgForOf, NgIf} from "@angular/common";
import {TimeAgoPipe} from "../../pipes/time-ago.pipe";
import {ActivatedRoute, RouterLink} from "@angular/router";
import {ButtonModule} from "primeng/button";
import {objectToQueryParams} from "../../utils/http";
import {Location} from '@angular/common';

@Component({
  selector: 'app-view-packages',
  templateUrl: './view-packages.component.html',
  styleUrl: './view-packages.component.scss',
  standalone: true,
  imports: [
    DataViewModule,
    InputTextModule,
    DropdownModule,
    FormsModule,
    NgForOf,
    NgClass,
    TimeAgoPipe,
    RouterLink,
    ButtonModule,
    MenuModule,
    NgIf
  ]
})
export class ViewPackagesComponent implements OnDestroy {
  @ViewChild('menuCopy') protected readonly menuCopy!: Menu;
  @Input({required:true}) public menuItems!: MenuItem[];
  @Input({required:true}) public bookmarkView!: boolean;
  @Input({required:true}) public loadData!:
    (form: PublicPackageSearchForm) => Observable<PageDTO<PackagePublicDTO>>;
  @Input({required:true}) public like!:
    (data: PackagePublicDTO, value: boolean|null) => Observable<void>;
  @Input({required:true}) public bookmark!:
    (data: PackagePublicDTO, value: boolean) => Observable<void>;
  @Input({required:true}) public amountLabel!: (amount: number) => string;
  @Input({required:true}) public buildTryUrl!: (data: PackagePublicDTO) => string;
  @Output() protected readonly openMenu = new EventEmitter<PackagePublicDTO>;
  @Output() protected readonly closeMenu = new EventEmitter<void>;
  protected data: PageDTO<PackagePublicDTO>;
  protected user: AccountDTO|null = null;
  protected readonly sortOptions: {label: string, value: string}[];
  protected readonly sortKey: string;
  protected readonly pageLinks: number;
  protected readonly searchForm: PublicPackageSearchForm;
  private readonly pageSize: number = 10;
  private _apiPackages$?: Subscription;
  private readonly _user$?: Subscription;

  public constructor(
    private readonly _authService: AuthService,
    private readonly _location: Location,
    private readonly _activatedRoute: ActivatedRoute,
  ) {
    this.sortOptions = [
      { label: 'Relevance', value: SortPublicPackage.RELEVANCE },
      { label: 'Recent updates', value: SortPublicPackage.UPDATE },
      { label: 'Popularity', value: SortPublicPackage.POPULARITY },
    ];
    this.sortKey = this.sortOptions[0].value;
    this.pageLinks = window.innerWidth < 640 ? 1 :
      window.innerWidth < 960 ? 3 :
        window.innerWidth < 1280 ? 5 : 10;
    this.data = {data: [], page: 0, pages: 0, size: this.pageSize, elements: 0};
    this.searchForm = {page: 0, size: this.pageSize, search: '', sort: undefined};
    const {page, search, sort} = this._activatedRoute.snapshot.queryParams;
    if(page && !isNaN(parseInt(page))) {
      this.searchForm.page = parseInt(page);
      this.data.page = this.searchForm.page;
    }
    if(search) {
      this.searchForm.search = search;
    }
    if(sort) {
      const option = this.sortOptions.find(o => o.value === sort);
      if(option) {
        this.sortKey = option.value;
        this.searchForm.sort = sort;
      }
    }
    this._user$ = this._authService.authenticatedUser.subscribe((value) => {
      if(value) this._authService.account().subscribe((value) => this.user = value);
    });
  }

  public ngOnDestroy(): void {
    if(this._user$) this._user$.unsubscribe();
    if(this._apiPackages$) {
      this._apiPackages$.unsubscribe();
      this._apiPackages$ = undefined;
    }
  }

  protected updateData() {
    if(this._apiPackages$) {
      this._apiPackages$.unsubscribe();
      this._apiPackages$ = undefined;
    }
    this._apiPackages$ = this.loadData(this.searchForm).subscribe({
      next: (value) => {
        this.data = value;
        this.updateLocation();
      },
    });
  }

  private updateLocation() {
    let query = objectToQueryParams(this.searchForm);
    if(query) query = '?' + query;
    const baseUrl = this._location.path().split('?')[0];
    this._location.replaceState(baseUrl, query)
  }

  protected onPaginationChanged($event: DataViewLazyLoadEvent) {
    this.searchForm.page = $event.first / this.searchForm.size;
    this.updateData();
  }

  protected onSortChange(event: any) {
    this.searchForm.sort = event.value;
    this.updateData();
  }

  protected onSearchChange($event: Event) {
    this.searchForm.search = ($event.target as HTMLInputElement).value;
    this.updateData();
  }

  protected onLike(data: PackagePublicDTO) {
    if(data.liked === true) {
      this.like(data, null).subscribe({
        next: () => {
          data.likes--;
          data.liked = null;
        }
      });
    } else {
      this.like(data, true).subscribe({
        next: () => {
          if(data.liked === false) data.dislikes--;
          data.likes++;
          data.liked = true;
        }
      });
    }
  }

  protected onDislike(data: PackagePublicDTO) {
    if(data.liked === false) {
      this.like(data, null).subscribe({
        next: () => {
          data.dislikes--;
          data.liked = null;
        }
      });
    } else {
      this.like(data, false).subscribe({
        next: () => {
          if(data.liked === true) data.likes--;
          data.dislikes++;
          data.liked = false;
        }
      });
    }
  }

  protected onBookmark(data: PackagePublicDTO) {
    this.bookmark(data, !data.bookmarked).subscribe({
      next: () => {
        data.bookmarked = !data.bookmarked;
        if(this.bookmarkView) this.updateData();
      }
    });
  }

  protected onMenuClose() {
    this.closeMenu.emit();
  }

  protected onMenuOpen($event: MouseEvent, data: PackagePublicDTO) {
    this.openMenu.emit(data);
    this.menuCopy.show($event);
  }
}
