import {Parser} from "../parser";
import {OptimizedParseSyntax, optimizeParseSyntax, PARSE_SYNTAX, BuildOps, BUILD_SYNTAX} from "./syntax";
import {docParser} from "./doc.parser";
import {docBuilder} from "./doc.builder";
import {
  InputData,
  CodeError,
  Position,
  regexLiteralString,
  setProperty,
  unescapeString, unescapeRegexString
} from "../utils";

type BuildCallback = (token: string, match: string, stack: InputData[], text: string, offset: number) => void;

export class Parser_0_0_1 implements Parser {
  /** Returns parser documentation */
  get docParser(): string {
    return docParser;
  }

  /** Returns builder documentation */
  get docBuilder(): string {
    return docBuilder;
  }

  /**
   * Parses the source code related to a parser written by the user.
   * @param text parser source code
   * @return parsing instructions that will be used when parsing user input file
   */
  public parseSrcParser(text: string): OptimizedParseSyntax {
    const result: any = this._parse(text, PARSE_SYNTAX, this._buildParser.bind(this));
    const syntax =  optimizeParseSyntax(result.tokens || []);
    if(!syntax['*']) throw new CodeError('root not found', 0);
    return syntax;
  }

  /**
   * Parses the source code related to a builder written by the user.
   * @param text builder source code
   * @return building instructions that will be used when parsing user input file
   */
  public parseSrcBuilder(text: string): BuildOps {
    return this._parse(text, BUILD_SYNTAX, this._buildBuilder.bind(this)) as any;
  }

  /**
   * Parses user input file using parsing and building instructions obtained from source code written by the user.
   * @param text user input file
   * @param parserSyntax parsing instructions obtained from parser source code
   * @param builderSyntax building instructions obtained from builder source code
   * @return input data that can be passed to a generator
   */
  public parse(text: string, parserSyntax: OptimizedParseSyntax, builderSyntax: BuildOps): InputData {
    return this._parse(text, parserSyntax, this._build.bind(this, builderSyntax));
  }

  /**
   * Parses input text based on parser instructions and a builder callback
   * to build input data that can be passed to a generator.
   * @param text user input file
   * @param parserSyntax parsing instructions obtained from parser source code
   * @param buildCallback builder function to call for each matching token
   */
  private _parse(text: string, parserSyntax: OptimizedParseSyntax, buildCallback: BuildCallback) {
    const ast: InputData = {};
    const stackAst = [ast];
    let offset = 0;
    let lastToken = '*';
    if(!parserSyntax[lastToken]) throw new CodeError('root not found', offset);
    buildCallback(lastToken, '', stackAst, text, offset);
    while(offset < text.length) {
      let remainingText = text.slice(offset);
      let matchedToken: {token:string,match:string}|null = null;
      if(parserSyntax[lastToken] === undefined) throw new CodeError('invalid syntax', offset);
      for(const currentToken of parserSyntax[lastToken]) {
        const match = remainingText.match(currentToken.regex);
        if(match != null && match[0].length > 0) {
          buildCallback(currentToken.current, match[0], stackAst, text, offset);
          matchedToken = {token: currentToken.current, match: match[0]};
          break;
        }
      }
      if(matchedToken == null) throw new CodeError('invalid syntax', offset);
      offset += matchedToken.match.length;
      lastToken = matchedToken.token;
    }
    if(parserSyntax.$.length !== 0 && !parserSyntax.$.includes(lastToken)) {
      throw new CodeError('invalid syntax', offset);
    }
    return ast;
  }

  /**
   * Builds input data based on token value and matching text to create parser instructions.
   * @param token name of current token
   * @param match text matching current token regex
   * @param stack stack of input data to modify
   * @param text parser source code
   * @param offset current offset in text
   */
  private _buildParser(token: string, match: string, stack: InputData[], text: string, offset: number) {
    let current: any = stack.at(-1);
    switch(token) {
      case "last_token":
        current["tokens"] = current["tokens"] || [];
        current["tokens"].push({last: [match]});
        stack.push(current["tokens"].at(-1));
        break;
      case "last_token_next":
        current["last"].push(match);
        break;
      case "current_token":
        current["current"] = match;
        break;
      case "regex":
        current["regex"] = unescapeRegexString(match, offset);
        if(current["regex"].test("")) throw new CodeError(
          'regex is forbidden to match an empty string', offset, match,
        );
        stack.pop();
        break;
      case "end_token":
        current["current"] = '$';
        stack.pop();
        break;
    }
  }

  /**
   * Builds input data based on token value and matching text to create builder instructions.
   * @param token name of current token
   * @param match text matching current token regex
   * @param stack stack of input data to modify
   * @param text builder source code
   * @param offset current offset in text
   */
  private _buildBuilder(token: string, match: string, stack: InputData[], text: string, offset: number) {
    let current: any = stack.at(-1);
    switch(token) {
      case "token":
        current[match] = [];
        stack.push(current[match]);
        break;
      case "field_name":
        current.push({field: unescapeString(match, offset)});
        stack.push(current.at(-1));
        break;
      case "sep":
        stack.pop();
        break;
      case "eol":
        stack.pop();
        stack.pop();
        break;
      case "init_null":
        current["op"] = "init";
        current["value"] = null;
        break;
      case "init_true":
        current["op"] = "init";
        current["value"] = true;
        break;
      case "init_false":
        current["op"] = "init";
        current["value"] = false;
        break;
      case "init_number":
        current["op"] = "init";
        current["value"] = parseFloat(match);
        break;
      case "init_string":
        current["op"] = "init";
        current["value"] = unescapeString(match, offset);
        break;
      case "init_array":
        current["op"] = "init";
        current["value"] = [];
        break;
      case "init_map":
        current["op"] = "init";
        current["value"] = {};
        break;
      case "init_line":
        current["op"] = "init_position";
        current["value"] = "line";
        break;
      case "init_col":
        current["op"] = "init_position";
        current["value"] = "col";
        break;
      case "init_offset":
        current["op"] = "init_position";
        current["value"] = "offset";
        break;
      case "assign_string":
        current["op"] = "assign";
        current["value"] = "str";
        break;
      case "assign_lstring":
        current["op"] = "assign";
        current["value"] = "lstr";
        break;
      case "assign_number":
        current["op"] = "assign";
        current["value"] = "nb";
        break;
      case "assign_string_array":
        current["op"] = "assign";
        current["value"] = "str[]";
        break;
      case "assign_lstring_array":
        current["op"] = "assign";
        current["value"] = "lstr[]";
        break;
      case "assign_number_array":
        current["op"] = "assign";
        current["value"] = "nb[]";
        break;
      case "assign_null_array":
        current["op"] = "assign";
        current["value"] = "null[]";
        break;
      case "assign_bool_array":
        current["op"] = "assign";
        current["value"] = "bool[]";
        break;
      case "stack_map":
        current["op"] = "stack";
        current["value"] = "map";
        break;
      case "stack_map_array":
        current["op"] = "stack";
        current["value"] = "map[]";
        break;
      case "stack_pop":
        current.push({field: null, op: "stack_pop", value: match.length});
        stack.push(current.at(-1));
        break;
    }
  }

  /**
   * Builds input data based on token value, matching text and builder instructions.
   * @param builderSyntax builder instructions obtained from builder source code
   * @param token name of current token
   * @param match text matching current token regex
   * @param stack stack of input data to modify
   * @param text input text
   * @param offset current offset in text
   */
  private _build(builderSyntax: BuildOps, token: string, match: string, stack: InputData[], text: string, offset: number) {
    const ops = builderSyntax[token];
    if(!ops) return;
    for(const op of ops) {
      let current: any = stack.at(-1);
      let value: any;
      switch(op.op) {
        case "init":
          setProperty(current, op.field, structuredClone(op.value));
          break;
        case "init_position":
          switch(op.value) {
            case "line":
              setProperty(current, op.field, this._getLine(text, offset).line);
              break;
            case "col":
              setProperty(current, op.field, this._getLine(text, offset).col);
              break;
            case "offset":
              setProperty(current, op.field, offset);
              break;
          }
          break;
        case "assign":
          switch(op.value) {
            case "str":
              setProperty(current, op.field, match);
              break;
            case "str[]":
              if(!Array.isArray(current[op.field])) setProperty(current, op.field, []);
              current[op.field].push(match);
              break;
            case "lstr":
              if(!regexLiteralString.test(match)) throw new CodeError(
                `"${match}" is not a literal string`, offset, match,
              );
              setProperty(current, op.field, unescapeString(match, offset));
              break;
            case "lstr[]":
              if(!regexLiteralString.test(match)) throw new CodeError(
                `"${match}" is not a literal string`, offset, match,
              );
              if(!Array.isArray(current[op.field])) setProperty(current, op.field, []);
              current[op.field].push(unescapeString(match, offset));
              break;
            case "nb":
              value = parseFloat(match);
              if(isNaN(value)) throw new CodeError(
                `"${match}" is not a number`, offset, match
              );
              setProperty(current, op.field, value);
              break;
            case "nb[]":
              value = parseFloat(match);
              if(isNaN(value)) throw new CodeError(
                `"${match}" is not a number`, offset, match
              );
              if(!Array.isArray(current[op.field])) setProperty(current, op.field, []);
              current[op.field].push(value);
              break;
            case "null[]":
              if(!Array.isArray(current[op.field])) setProperty(current, op.field, []);
              current[op.field].push(null);
              break;
            case "bool[]":
              if(!Array.isArray(current[op.field])) setProperty(current, op.field, []);
              current[op.field].push(match === 'true');
              break;
          }
          break;
        case "stack":
          switch(op.value) {
            case "map[]":
              if(!Array.isArray(current[op.field])) setProperty(current, op.field, []);
              current[op.field].push({});
              current = current[op.field].at(-1);
              stack.push(current);
              break;
            case "map":
              if(current[op.field] === undefined || current[op.field] === null || (
                typeof current[op.field].constructor === 'function' &&
                current[op.field].constructor !== Object)
              ) {
                setProperty(current, op.field, {});
              }
              current = current[op.field];
              stack.push(current);
              break;
          }
          break;
        case "stack_pop":
          if(stack.length <= op.value) throw new CodeError(
            `can't pop stack that amount of time (stack: ${stack.length}, pop: ${op.value})`,
            offset, match
          );
          for(let i = 0; i < op.value; i++) stack.pop();
          break;
      }
    }
  }

  /** Calculates the line and column at an offset for a text. */
  private _getLine(text: string, offset: number): Position {
    let line = 1;
    let col = 1;
    for(let i = 0; i < offset; i++) {
      if(text[i] == '\n') {
        line += 1;
        col = 1;
      }
      else col += 1;
    }
    return {offset, line, col};
  }
}
