import {InputData} from "./utils";

export interface Parser {
  /**
   * Parses and loads the source code of the parser.
   * @param text Source code of the parser
   * @throws ParsingError ParsingError
   * @returns Generated parser syntax
   */
  parseSrcParser(text: string): any;

  /**
   * Parses and loads the source code of the builder.
   * @param text Source code of the builder
   * @throws ParsingError ParsingError
   * @returns Generated builder syntax
   */
  parseSrcBuilder(text: string): any;

	/**
   * Parses a text using given syntax or default one.
   * @param text Text to parse
   * @param parserSyntax Syntax of the parser
   * @param builderSyntax Syntax of the builder
   * @throws ParsingError ParsingError
   * @returns The map object generated with the extracted data from the text
   */
  parse(text: string, parserSyntax: any, builderSyntax: any): InputData;

  /** Returns parser documentation */
  get docParser(): string

  /** Returns builder documentation */
  get docBuilder(): string
}
