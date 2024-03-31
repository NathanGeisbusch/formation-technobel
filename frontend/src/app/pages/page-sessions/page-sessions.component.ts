import {Component, OnDestroy} from '@angular/core';
import {TableSessionsComponent} from "../../components/table-sessions/table-sessions.component";
import {Subject, Subscription} from "rxjs";
import {PageDTO, SessionDTO} from "../../models/dto";
import {SessionSearchForm} from "../../models/form-search";
import {AppMessageService, DeleteConfirmResource, MsgReasonSuccess} from "../../services/app.message.service";
import {PackagesDeleteForm} from "../../models/form";
import {SessionService} from "../../services/session.service";

@Component({
  selector: 'app-page-sessions',
  templateUrl: './page-sessions.component.html',
  styleUrl: './page-sessions.component.scss',
  standalone: true,
  imports: [
    TableSessionsComponent
  ]
})
export class PageSessionsComponent implements OnDestroy {
  protected data = new Subject<PageDTO<SessionDTO>>();
  private _data$?: Subscription;
  private _searchForm?: SessionSearchForm;

  public constructor(
    private readonly _sessionService: SessionService,
    private readonly _msgService: AppMessageService,
  ) {}

  ngOnDestroy(): void {
    if(this._data$) this._data$.unsubscribe();
  }

  protected amountLabel(amount: number): string {
    const plural = amount > 1 ? 's' : '';
    return `${amount} session${plural} found`;
  }

  protected buildEditUrl(data: SessionDTO): string {
    return '/sessions/'+data.name;
  }

  protected onSearch($event: SessionSearchForm) {
    if(this._data$) this._data$.unsubscribe();
    this._searchForm = $event;
    this._data$ = this._sessionService.find($event).subscribe({
      next: (value) => this.data.next(value),
      error: (err) => this.data.error(err),
    });
  }

  protected onDelete(data: SessionDTO) {
    this._msgService.deleteConfirmDialog(
      DeleteConfirmResource.SESSION, false,
      () => {
        this._sessionService.delete(data.name).subscribe({
          next: () => {
            this._msgService.showSuccess(MsgReasonSuccess.SESSION_DELETE);
            if(this._searchForm) this.onSearch(this._searchForm);
          }
        })
      }
    );
  }

  protected onBulkDelete(form: PackagesDeleteForm) {
    const plural = form.id.length > 1;
    this._msgService.deleteConfirmDialog(
      DeleteConfirmResource.SESSION, plural,
      () => {
        this._sessionService.bulkDelete(form).subscribe({
          next: () => {
            this._msgService.showSuccess(plural ?
              MsgReasonSuccess.SESSIONS_DELETE : MsgReasonSuccess.SESSION_DELETE
            );
            if(this._searchForm) this.onSearch(this._searchForm);
          }
        })
      }
    );
  }
}
