export enum Role {
  USER = "user",
}

export enum ParserSyntax {
  PM_PARSER_0_0_1 = "pm-parser@0.0.1",
}

export enum GeneratorSyntax {
  PM_GENERATOR_0_0_1 = "pm-generator@0.0.1",
}

export enum PackageVisibility {
  PUBLIC = "public",
  PRIVATE = "private",
  PROTECTED = "protected",
}

export enum SortPublicPackage {
  RELEVANCE = "relevance",
  POPULARITY = "popularity",
  UPDATE = "update",
}

export enum SortPrivatePackage {
  NAME_ASC = "name,asc",
  NAME_DSC = "name,dsc",
  VERSION_ASC = "version,asc",
  VERSION_DSC = "version,dsc",
  UPDATE_ASC = "update,asc",
  UPDATE_DSC = "update,dsc",
}

export enum SortSession {
  NAME_ASC = "name,asc",
  NAME_DSC = "name,dsc",
  UPDATE_ASC = "update,asc",
  UPDATE_DSC = "update,dsc",
}
