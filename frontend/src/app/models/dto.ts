import {GeneratorSyntax, PackageVisibility, ParserSyntax} from "./enum";

export interface AuthDTO {
  token: string,
}

export interface CreatedDTO {
  id: string,
}

export interface PageDTO<T> {
  elements: number,
  pages: number,
  page: number,
  size: number,
  data: T[],
}

export interface ErrorFieldDTO {
  field: string,
  reason: string,
}

export interface ErrorDTO {
  status: number,
  path: string,
  error: string,
}

export interface ErrorFormDTO {
  status: number,
  path: string,
  errors: ErrorFieldDTO[],
}

export interface AccountDTO {
  pseudonym: string,
  email: string,
}

export interface PackagePublicDTO {
  name: string,
  version: string,
  author: string,
  description: string,
  updatedAt: string,
  likes: number,
  dislikes: number,
  liked: boolean|null,
  bookmarked: boolean,
  parserSyntax: ParserSyntax,
  generatorSyntax?: GeneratorSyntax,
}

export interface PackagePrivateDTO {
  name: string,
  version: string,
  author: string,
  description: string,
  updatedAt: string,
  visibility: PackageVisibility,
  parserSyntax: ParserSyntax,
  generatorSyntax?: GeneratorSyntax,
}

export interface ParserEditDTO {
  name: string,
  version: string,
  description: string,
  syntax: ParserSyntax,
  visibility: PackageVisibility,
  password: string,
}

export interface GeneratorEditDTO {
  name: string,
  version: string,
  description: string,
  parserSyntax: ParserSyntax,
  generatorSyntax: GeneratorSyntax,
  visibility: PackageVisibility,
  password: string,
}

export interface SessionDTO {
  name: string,
  generator: string,
  updatedAt: string,
  parserSyntax: ParserSyntax,
  generatorSyntax: GeneratorSyntax,
}
