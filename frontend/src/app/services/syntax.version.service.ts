import { Injectable } from '@angular/core';
import {Parser_0_0_1} from "../models/syntax/pm-parser@0.0.1/parser";
import {Parser} from "../models/syntax/parser";
import {Generator} from "../models/syntax/generator";
import {Generator_0_0_1} from "../models/syntax/pm-generator@0.0.1/generator";
import {GeneratorSyntax, ParserSyntax} from "../models/enum";

@Injectable({
  providedIn: 'root'
})
export class SyntaxVersionService {
  public readonly parsers: ParserSyntax[] = [
    ParserSyntax.PM_PARSER_0_0_1,
  ];

  public readonly generators: GeneratorSyntax[] = [
    GeneratorSyntax.PM_GENERATOR_0_0_1,
  ];

  /** Get a parser instance from a parser syntax version. */
  public getParser(version: ParserSyntax): Parser|null {
    switch(version) {
      case ParserSyntax.PM_PARSER_0_0_1:
        return new Parser_0_0_1();
      default:
        return null;
    }
  }

  /** Get a generator instance from a generator syntax version. */
  public getGenerator(version: GeneratorSyntax): Generator|null {
    switch(version) {
      case GeneratorSyntax.PM_GENERATOR_0_0_1:
        return new Generator_0_0_1();
      default:
        return null;
    }
  }
}
