import {PackageVisibility, SortPrivatePackage, SortPublicPackage, SortSession} from "./enum";

export interface PublicPackageSearchForm {
  page: number,
  size: number,
  search?: string,
  sort?: SortPublicPackage,
}

export interface PrivatePackageSearchForm {
  page: number,
  size: number,
  search?: string,
  sort?: SortPrivatePackage,
  visibility?: PackageVisibility,
  allVersions?: boolean,
}

export interface SessionSearchForm {
  page: number,
  size: number,
  search?: string,
  sort?: SortSession,
}
