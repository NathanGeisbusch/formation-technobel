import { Injectable } from '@angular/core';
import {from, Observable, take} from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class CompressionService {
  /** Encode a string to a gzip blob. */
  public encode(text: string): Observable<Blob> {
    return from(this._encode(text)).pipe(take(1));
  }

  /** Decode a gzip blob into a string. */
  public decode(blob: Blob): Observable<string> {
    return from(this._decode(blob)).pipe(take(1));
  }

  private async _encode(text: string): Promise<Blob> {
    return await new Response(
      new Blob([text]).stream().pipeThrough(
        new CompressionStream('gzip')
      )
    ).blob();
  }

  private async _decode(blob: Blob): Promise<string> {
    return await new Response(
      blob.stream().pipeThrough(new DecompressionStream('gzip'))
    ).text();
  }
}
