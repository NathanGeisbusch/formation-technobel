import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {environment} from "../../environments/environment";
import {CreatedDTO, PageDTO, SessionDTO} from "../models/dto";
import {HttpClientParams} from "../utils/http";
import {SessionSearchForm} from "../models/form-search";
import {PackagesDeleteForm, SessionCreateForm, SessionEditForm} from "../models/form";
import {switchMap} from "rxjs";
import {CompressionService} from "./compression.service";

@Injectable({
  providedIn: 'root'
})
export class SessionService {
  public constructor(
    private readonly _httpClient: HttpClient,
    private readonly _compressionService: CompressionService,
  ) {}

  public find(form: SessionSearchForm) {
    const params: HttpClientParams = {page: form.page, size: form.size};
    if(form.search) params["search"] = form.search;
    if(form.sort) params["sort"] = form.sort;
    return this._httpClient.get<PageDTO<SessionDTO>>(
      `${environment.apiUrl}/sessions`, {params}
    );
  }

  public get(id: string) {
    return this._httpClient.get<SessionDTO>(
      `${environment.apiUrl}/sessions/${id}`
    );
  }

  public getParserCode(id: string) {
    const options: Record<string, unknown> = {responseType: 'blob'};
    return this._httpClient.get<Blob>(
      `${environment.apiUrl}/sessions/${id}/parser`, options
    ).pipe(switchMap(blob => this._compressionService.decode(blob)));
  }

  public getBuilderCode(id: string) {
    const options: Record<string, unknown> = {responseType: 'blob'};
    return this._httpClient.get<Blob>(
      `${environment.apiUrl}/sessions/${id}/builder`, options
    ).pipe(switchMap(blob => this._compressionService.decode(blob)));
  }

  public getGeneratorCode(id: string) {
    const options: Record<string, unknown> = {responseType: 'blob'};
    return this._httpClient.get<Blob>(
      `${environment.apiUrl}/sessions/${id}/generator`, options
    ).pipe(switchMap(blob => this._compressionService.decode(blob)));
  }

  public getDocCode(id: string) {
    const options: Record<string, unknown> = {responseType: 'blob'};
    return this._httpClient.get<Blob>(
      `${environment.apiUrl}/sessions/${id}/doc`, options
    ).pipe(switchMap(blob => this._compressionService.decode(blob)));
  }

  public getInputText(id: string) {
    const options: Record<string, unknown> = {responseType: 'blob'};
    return this._httpClient.get<Blob>(
      `${environment.apiUrl}/sessions/${id}/input`, options
    ).pipe(switchMap(blob => this._compressionService.decode(blob)));
  }

  public updateInputText(id: string, code: string) {
    const headers = {'Content-Type': 'application/octet-stream'};
    return this._compressionService.encode(code).pipe(
      switchMap(encoded => this._httpClient.patch<void>(
        `${environment.apiUrl}/sessions/${id}/input`, encoded, {headers}
      ))
    );
  }

  public create(form: SessionCreateForm) {
    return this._httpClient.post<CreatedDTO>(
      `${environment.apiUrl}/sessions`, form
    );
  }

  public update(id: string, form: SessionEditForm) {
    return this._httpClient.patch<void>(
      `${environment.apiUrl}/sessions/${id}`, form
    );
  }

  public delete(id: string) {
    return this._httpClient.delete<void>(
      `${environment.apiUrl}/sessions/${id}`
    );
  }

  public bulkDelete(form: PackagesDeleteForm) {
    return this._httpClient.post<void>(
      `${environment.apiUrl}/delete/sessions`, form
    );
  }
}
