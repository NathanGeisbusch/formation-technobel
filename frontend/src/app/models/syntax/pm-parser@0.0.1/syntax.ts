/** Parsing syntax information based on current token. */
export type ParseSyntax = {
  last: string[];
  current: string;
  regex: RegExp;
}

/** Parsing syntax information based on last token. */
export type OptimizedParseSyntax = {
  [last: string]: {current: string, regex: RegExp}[]
} & {
  $: string[]
}

/** Converts a list of ParseSyntax to a map indexed on last token in order to improve performance. */
export function optimizeParseSyntax(syntax: ParseSyntax[]): OptimizedParseSyntax {
  const result: OptimizedParseSyntax = {$: []};
  for(const s of syntax) {
    if(s.current === '$') result.$.push(...s.last);
    else for(const l of s.last) {
      if(result.hasOwnProperty(l)) result[l].push(s);
      else result[l] = [s];
    }
  }
  return result;
}

/** Parsing syntax for parser source code. */
export const PARSE_SYNTAX: OptimizedParseSyntax = optimizeParseSyntax([
  {last: ["*", "empty_line", "comment_line", "eol"], current: "empty_line", regex: /^ *\n/},
  {last: ["*", "empty_line", "comment_line", "eol"], current: "comment_line", regex: /^#.*\n/},
  {last: ["*", "empty_line", "comment_line", "eol"], current: "last_token", regex: /^(\*|[a-z0-9_]+)/},
  {last: ["last_token_sep"], current: "last_token_next", regex: /^(\*|[a-z0-9_]+)/},
  {last: ["last_token", "last_token_next"], current: "last_token_sep", regex: /^, /},
  {last: ["last_token", "last_token_next"], current: "last_token_", regex: /^ > /},
  {last: ["last_token_"], current: "current_token", regex: /^[a-z0-9_]+/},
  {last: ["last_token_"], current: "end_token", regex: /^\$/},
  {last: ["current_token"], current: "current_token_", regex: /^ = /},
  {last: ["current_token_"], current: "regex", regex: /^"(?:\\.|[^\\"])*"/},
  {last: ["regex", "end_token"], current: "eol", regex: /^\n/},
  {last: ["*", "empty_line", "comment_line", "eol", "regex", "end_token"], current: "$"} as any,
]);

/** Parsing syntax for builder source code. */
export const BUILD_SYNTAX: OptimizedParseSyntax = optimizeParseSyntax([
  {last: ["*", "empty_line", "comment_line", "eol"], current: "empty_line", regex: /^ *\n/},
  {last: ["*", "empty_line", "comment_line", "eol"], current: "comment_line", regex: /^#.*\n/},
  {last: ["*", "empty_line", "comment_line", "eol"], current: "token", regex: /^(\*|[a-z0-9_]+)/},
  {last: ["token"], current: "token_sep", regex: /^ /},
  {last: ["token_sep", "sep"], current: "field_name", regex: /^"(?:\\.|[^\\"])*"/},
  {last: ["field_name"], current: "field_sep", regex: /^:/},
  {last: ["field_sep"], current: "init_line", regex: /^line/},
  {last: ["field_sep"], current: "init_col", regex: /^col/},
  {last: ["field_sep"], current: "init_offset", regex: /^offset/},
  {last: ["field_sep"], current: "init_null", regex: /^null/},
  {last: ["field_sep"], current: "init_true", regex: /^true/},
  {last: ["field_sep"], current: "init_false", regex: /^false/},
  {last: ["field_sep"], current: "init_number", regex: /^[1-9][0-9]*(\\.[0-9]+)*/},
  {last: ["field_sep"], current: "init_string", regex: /^"(?:\\.|[^\\"])*"/},
  {last: ["field_sep"], current: "init_array", regex: /^\[]/},
  {last: ["field_sep"], current: "init_map", regex: /^{}/},
  {last: ["field_sep"], current: "assign_string", regex: /^str=/},
  {last: ["field_sep"], current: "assign_lstring", regex: /^lstr=/},
  {last: ["field_sep"], current: "assign_number", regex: /^nb=/},
  {last: ["field_sep"], current: "assign_string_array", regex: /^str\[]=/},
  {last: ["field_sep"], current: "assign_lstring_array", regex: /^lstr\[]=/},
  {last: ["field_sep"], current: "assign_number_array", regex: /^nb\[]=/},
  {last: ["field_sep"], current: "assign_null_array", regex: /^opt\[]:null/},
  {last: ["field_sep"], current: "assign_bool_array", regex: /^bool\[]:true/},
  {last: ["field_sep"], current: "assign_bool_array", regex: /^bool\[]:false/},
  {last: ["field_sep"], current: "stack_map", regex: /^map\+/},
  {last: ["field_sep"], current: "stack_map_array", regex: /^map\[]\+/},
  {last: ["token_sep"], current: "stack_pop", regex: /^-+/},
  {last: ["sep"], current: "stack_pop", regex: /^-+/},
  {
    last: [
      "init_null", "init_true", "init_false", "init_number",
      "init_string", "init_array", "init_map",
      "init_col", "init_line", "init_offset",
      "assign_string", "assign_lstring", "assign_number",
      "assign_string_array", "assign_lstring_array", "assign_number_array",
      "assign_null_array", "assign_bool_array",
      "stack_map", "stack_map_array", "stack_pop",
    ], current: "sep", regex: /^ /
  },
  {
    last: [
      "init_null", "init_true", "init_false", "init_number",
      "init_string", "init_array", "init_map",
      "init_col", "init_line", "init_offset",
      "assign_string", "assign_lstring", "assign_number",
      "assign_string_array", "assign_lstring_array", "assign_number_array",
      "assign_null_array", "assign_bool_array",
      "stack_map", "stack_map_array", "stack_pop",
    ], current: "eol", regex: /^(\n|$)/
  },
  {
    last: [
      "*", "empty_line", "comment_line", "eol",
      "init_null", "init_true", "init_false", "init_number",
      "init_string", "init_array", "init_map",
      "init_col", "init_line", "init_offset",
      "assign_string", "assign_lstring", "assign_number",
      "assign_string_array", "assign_lstring_array", "assign_number_array",
      "assign_null_array", "assign_bool_array",
      "stack_map", "stack_map_array", "stack_pop",
    ], current: "$"
  } as any,
]);

/** Building instructions extracted from source code. */
export type BuildOps = {
  [token: string]: (
    {
      field: string,
      op: 'init',
      value: null|boolean|number|string|object|Array<any>
    } | {
      field: string,
      op: 'init_position',
      value: 'line'|'col'|'offset'
    } | {
      field: string,
      op: 'assign',
      value: 'str'|'lstr'|'nb'|'str[]'|'lstr[]'|'nb[]'|'null[]'|'bool[]'
    } | {
      field: string,
      op: 'stack',
      value: 'map'|'map[]'
    } | {
      field: null,
      op: 'stack_pop',
      value: number
    }
  )[],
}
