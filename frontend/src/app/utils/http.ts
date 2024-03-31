import {HttpParams} from "@angular/common/http";

export type HttpClientParams = HttpParams | {[p: string]: string | number | boolean | readonly (string | number | boolean)[]} | undefined;

/**
 * Convert the fields of an object into URI-encoded query params.
 * @param obj object to convert to query params
 * @returns the URI-encoded query params
 */
export function objectToQueryParams(obj: {[k: string]: any}): string {
  return Object.keys(obj)
    .filter(key => obj[key] !== undefined && obj[key] !== null)
    .map(key => `${encodeURIComponent(key)}=${encodeURIComponent(obj[key])}`)
    .join('&');
}
