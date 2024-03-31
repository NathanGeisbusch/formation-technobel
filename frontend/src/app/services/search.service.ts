import {Injectable} from "@angular/core";
import {HttpClient} from "@angular/common/http";
import {environment} from "../../environments/environment";

@Injectable({
  providedIn: 'root'
})
export class SearchService {
  public constructor(private readonly _httpClient: HttpClient) {}

  public existsPseudonym(search: string) {
    return this._httpClient.get<boolean>(
      `${environment.apiUrl}/search/pseudonym`, {params: {value: search}}
    );
  }

  public existsEmail(search: string) {
    return this._httpClient.get<boolean>(
      `${environment.apiUrl}/search/email`, {params: {value: search}}
    );
  }

  public existsParserByName(name: string) {
    return this._httpClient.get<boolean>(
      `${environment.apiUrl}/search/parser/name`, {params: {name}}
    );
  }

  public existsParserByVersion(name: string, version: string) {
    return this._httpClient.get<boolean>(
      `${environment.apiUrl}/search/parser/version`, {params: {name,version}}
    );
  }

  public existsGeneratorByName(name: string) {
    return this._httpClient.get<boolean>(
      `${environment.apiUrl}/search/generator/name`, {params: {name}}
    );
  }
  public existsGeneratorByVersion(name: string, version: string) {
    return this._httpClient.get<boolean>(
      `${environment.apiUrl}/search/generator/version`, {params: {name,version}}
    );
  }


  public existsSession(name: string) {
    return this._httpClient.get<boolean>(
      `${environment.apiUrl}/search/session/name`, {params: {name}}
    );
  }

  public findParserVersions(author: string, name: string, search: string) {
    return this._httpClient.get<string[]>(
      `${environment.apiUrl}/search/parser/versions`,
      {params: {author, name, value: search}}
    );
  }

  public findGeneratorVersions(author: string, name: string, search: string) {
    return this._httpClient.get<string[]>(
      `${environment.apiUrl}/search/generator/versions`,
      {params: {author, name, value: search}}
    );
  }
}
