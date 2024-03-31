import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {environment} from "../../environments/environment";
import {CreatedDTO, PackagePrivateDTO, PackagePublicDTO, PageDTO, GeneratorEditDTO} from "../models/dto";
import {HttpClientParams} from "../utils/http";
import {PrivatePackageSearchForm, PublicPackageSearchForm} from "../models/form-search";
import {PackageCreateForm, PackagesDeleteForm, GeneratorInfoForm} from "../models/form";
import {switchMap} from "rxjs";
import {CompressionService} from "./compression.service";

@Injectable({
  providedIn: 'root'
})
export class GeneratorService {
  public constructor(
    private readonly _httpClient: HttpClient,
    private readonly _compressionService: CompressionService,
  ) {}

  public findPublic(form: PublicPackageSearchForm) {
    const params: HttpClientParams = {page: form.page, size: form.size};
    if(form.search) params["search"] = form.search;
    if(form.sort) params["sort"] = form.sort;
    return this._httpClient.get<PageDTO<PackagePublicDTO>>(
      `${environment.apiUrl}/generators/public`, {params}
    );
  }

  public findBookmarked(form: PublicPackageSearchForm) {
    const params: HttpClientParams = {page: form.page, size: form.size};
    if(form.search) params["search"] = form.search;
    if(form.sort) params["sort"] = form.sort;
    return this._httpClient.get<PageDTO<PackagePublicDTO>>(
      `${environment.apiUrl}/generators/bookmarked`, {params}
    );
  }

  public findOwn(form: PrivatePackageSearchForm) {
    const params: HttpClientParams = {page: form.page, size: form.size};
    if(form.search) params["search"] = form.search;
    if(form.sort) params["sort"] = form.sort;
    if(form.visibility) params["visibility"] = form.visibility;
    if(form.allVersions) params["allVersions"] = form.allVersions;
    return this._httpClient.get<PageDTO<PackagePrivateDTO>>(
      `${environment.apiUrl}/generators/own`, {params}
    );
  }

  public getPublic(id: string) {
    return this._httpClient.get<PackagePublicDTO>(
      `${environment.apiUrl}/generators/${id}/public`
    );
  }

  public getProtected(id: string, password: string) {
    return this._httpClient.get<PackagePrivateDTO>(
      `${environment.apiUrl}/generators/${id}/protected`, {params: {password}}
    );
  }

  public getPrivate(id: string) {
    return this._httpClient.get<PackagePrivateDTO>(
      `${environment.apiUrl}/generators/${id}/private`
    );
  }

  public getEditable(id: string) {
    return this._httpClient.get<GeneratorEditDTO>(
      `${environment.apiUrl}/generators/${id}/edit`
    );
  }

  public getParserCode(id: string, password?: string) {
    const options: Record<string, unknown> = {responseType: 'blob'};
    if(password) options['params'] = {password};
    return this._httpClient.get<Blob>(
      `${environment.apiUrl}/generators/${id}/parser`, options
    ).pipe(switchMap(blob => this._compressionService.decode(blob)));
  }

  public getBuilderCode(id: string, password?: string) {
    const options: Record<string, unknown> = {responseType: 'blob'};
    if(password) options['params'] = {password};
    return this._httpClient.get<Blob>(
      `${environment.apiUrl}/generators/${id}/builder`, options
    ).pipe(switchMap(blob => this._compressionService.decode(blob)));
  }

  public getGeneratorCode(id: string, password?: string) {
    const options: Record<string, unknown> = {responseType: 'blob'};
    if(password) options['params'] = {password};
    return this._httpClient.get<Blob>(
      `${environment.apiUrl}/generators/${id}/generator`, options
    ).pipe(switchMap(blob => this._compressionService.decode(blob)));
  }

  public getDocCode(id: string, password?: string) {
    const options: Record<string, unknown> = {responseType: 'blob'};
    if(password) options['params'] = {password};
    return this._httpClient.get<Blob>(
      `${environment.apiUrl}/generators/${id}/doc`, options
    ).pipe(switchMap(blob => this._compressionService.decode(blob)));
  }

  public updateParserCode(id: string, code: string) {
    const headers = {'Content-Type': 'application/octet-stream'};
    return this._compressionService.encode(code).pipe(
      switchMap(encoded => this._httpClient.patch<void>(
        `${environment.apiUrl}/generators/${id}/parser`, encoded, {headers}
      ))
    );
  }

  public updateBuilderCode(id: string, code: string) {
    const headers = {'Content-Type': 'application/octet-stream'};
    return this._compressionService.encode(code).pipe(
      switchMap(encoded => this._httpClient.patch<void>(
        `${environment.apiUrl}/generators/${id}/builder`, encoded, {headers}
      ))
    );
  }

  public updateGeneratorCode(id: string, code: string) {
    const headers = {'Content-Type': 'application/octet-stream'};
    return this._compressionService.encode(code).pipe(
      switchMap(encoded => this._httpClient.patch<void>(
        `${environment.apiUrl}/generators/${id}/generator`, encoded, {headers}
      ))
    );
  }

  public updateDocCode(id: string, code: string) {
    const headers = {'Content-Type': 'application/octet-stream'};
    return this._compressionService.encode(code).pipe(
      switchMap(encoded => this._httpClient.patch<void>(
        `${environment.apiUrl}/generators/${id}/doc`, encoded, {headers}
      ))
    );
  }

  public create(form: PackageCreateForm, fromParser?: boolean) {
    const options = fromParser != null ? {params: {fromParser}} : undefined;
    return this._httpClient.post<CreatedDTO>(
      `${environment.apiUrl}/generators`, form, options
    );
  }

  public update(id: string, form: GeneratorInfoForm) {
    return this._httpClient.patch<void>(
      `${environment.apiUrl}/generators/${id}`, form
    );
  }

  public delete(id: string, allVersions?: boolean) {
    const options = allVersions ? {params: {allVersions}} : undefined;
    return this._httpClient.delete<void>(
      `${environment.apiUrl}/generators/${id}`, options
    );
  }

  public bulkDelete(form: PackagesDeleteForm, allVersions?: boolean) {
    const options = allVersions ? {params: {allVersions}} : undefined;
    return this._httpClient.post<void>(
      `${environment.apiUrl}/delete/generators`, form, options
    );
  }

  public like(id: string, value: boolean|null) {
    const options = value ? {params: {value}} : undefined;
    return this._httpClient.patch<void>(
      `${environment.apiUrl}/generators/${id}/like`, null, options
    );
  }

  public bookmark(id: string, value: boolean) {
    return this._httpClient.patch<void>(
      `${environment.apiUrl}/generators/${id}/bookmark`, null, {params: {value}}
    );
  }

  public createMajorVersion(id: string) {
    return this._httpClient.post<CreatedDTO>(
      `${environment.apiUrl}/generators/${id}/major`, null
    );
  }

  public createMinorVersion(id: string) {
    return this._httpClient.post<CreatedDTO>(
      `${environment.apiUrl}/generators/${id}/minor`, null
    );
  }

  public createPatchVersion(id: string) {
    return this._httpClient.post<CreatedDTO>(
      `${environment.apiUrl}/generators/${id}/patch`, null
    );
  }
}
