/** Encode the text to url-safe base64 */
export function toBase64Url(text: string): string {
  return btoa(String.fromCodePoint(...new TextEncoder().encode(text)))
    .replace(/\+/g, '-')
    .replace(/\//g, '_')
    .replace(/=+$/, '');
}

/** Decode an url-safe base64 text */
export function fromBase64Url(text: string): string {
  const decoded = text
    .replace(/-/g, '+')
    .replace(/_/g, '/');
  return new TextDecoder().decode(
    Uint8Array.from(atob(decoded), (m) => m.codePointAt(0)!)
  );
}
