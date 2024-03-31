import {GeneratorSyntax, PackageVisibility, ParserSyntax} from "./enum";

export interface LoginForm {
  login: string,
  password: string,
}

export interface RegisterForm {
  pseudonym: string,
  email: string,
  password: string,
}

export interface AccountForm {
  pseudonym: string,
  email: string,
  password?: string,
}

export interface RequestPasswordForm {
  email: string,
}

export interface ChangePasswordForm {
  password: string,
}

export interface PackageCreateForm {
  name: string,
  from?: string,
  password?: string,
}

export interface ParserInfoForm {
  name?: string,
  description?: string,
  syntax?: ParserSyntax,
  visibility?: PackageVisibility,
  password?: string,
}

export interface GeneratorInfoForm {
  name?: string,
  description?: string,
  parserSyntax?: ParserSyntax,
  generatorSyntax?: GeneratorSyntax,
  visibility?: PackageVisibility,
  password?: string,
}

export interface SessionCreateForm {
  name: string,
  from: string,
}

export interface SessionEditForm {
  name?: string,
}

export interface PackagesDeleteForm {
  id: string[],
}
