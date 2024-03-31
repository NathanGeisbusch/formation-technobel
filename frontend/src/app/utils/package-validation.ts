const _REGEX_NAME = "[\\p{L}\\-+_0-9]{1,64}";
const _REGEX_255 = '([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])';
const _REGEX_65535 = '([0-9]|[1-9][0-9]{1,3}|[1-5][0-9]{4}|6[0-4][0-9]{3}|65[0-4][0-9]{2}|655[0-2][0-9]|6553[0-5])';
const _REGEX_PACKAGE_ID_SEP = /[:@]/g;

export const REGEX_NAME = new RegExp(`^${_REGEX_NAME}$`, 'u');
export const REGEX_VERSION = new RegExp(`^${_REGEX_65535}\\.${_REGEX_255}\\.${_REGEX_255}$`);
export const REGEX_PACKAGE_ID = new RegExp(`^${_REGEX_NAME}:${_REGEX_NAME}@${_REGEX_65535}\\.${_REGEX_255}\\.${_REGEX_255}$`, 'u');

export interface PackageId { author: string, name: string, version: string }

/**
 * Converts a string id to PackageId.
 * @param id id to convert
 */
export function toPackageId(id: string): PackageId|null {
  if(!REGEX_PACKAGE_ID.test(id)) return null;
  const tokens = id.split(_REGEX_PACKAGE_ID_SEP);
  if(tokens.length !== 3) return null;
  if(tokens[0].length > 64 || tokens[1].length > 64) return null;
  const version = tokens[2].split('.');
  if(version.length !== 3) return null;
  return {author: tokens[0], name: tokens[1], version: tokens[2]};
}

/**
 * Converts a package id to string.
 * @param id id to convert
 */
export function fromPackageId(id: PackageId): string {
  return `${id.author}:${id.name}@${id.version}`;
}
