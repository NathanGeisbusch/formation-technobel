import {OptimizedParseSyntax, optimizeParseSyntax} from "../pm-parser@0.0.1/syntax";
import {Parser_0_0_1} from "../pm-parser@0.0.1/parser";

export const PARSER = new Parser_0_0_1();

const IGNORE_LINE = /^( *|#.*)\n/;
const FUNCTION = /^:[a-z0-9_]+/;
const GLOBAL_VAR = /^%[a-z0-9_]+/;
const LOCAL_VAR = /^\$[a-z0-9_]+/;
const STR = /^"(?:\\.|[^\\"])*"/;
const INT_POSITIVE = /^([1-9][0-9]*|[0-9])/;
const INT = /^-?([1-9][0-9]*|[0-9])/;
const NB = /^-?([1-9][0-9]*|[0-9])(\.[0-9]*[1-9])?/;
const LIT = /^(\{}|\[]|false|true|null|pi|e)/;
const CONST = /^(false|true|null|pi|e)/;
const NULL_BOOL = /^(false|true|null)/;
const BOOL = /^(false|true)/;
const SORT_ORDER = /^(asc|desc)/
const TIME_UNIT = /^(year|month|day|hour|min|sec|ms)/
const RANGE_INC = /^\.\./;
const RANGE_EXC = /^\.\.</
const EXCLAMATION = /^!/;
const ASSIGN = /^ = /;
const ASSIGN_B = /^= /;
const SPACE = /^ /;
const EOL = /^\n/;

/** Parsing syntax for generator source code. */
export const GENERATOR_SYNTAX: OptimizedParseSyntax = optimizeParseSyntax([
  {last: ["*", "ignore_line"], current: "ignore_line", regex: IGNORE_LINE},

  // fct
  {last: ["*", "ignore_line"], current: "fct", regex: FUNCTION},
  {last: ["fct_ignore_line", "fct_eol", "eol"], current: "fct2", regex: FUNCTION},
  {last: ["fct", "fct2", "fct_param_lv"], current: "fct_", regex: SPACE},
  {last: ["fct_"], current: "fct_param_lv", regex: LOCAL_VAR},
  {last: ["fct", "fct2", "fct_param_lv"], current: "fct_eol", regex: EOL},
  {last: ["fct_ignore_line", "fct_eol", "eol"], current: "fct_ignore_line", regex: IGNORE_LINE},

  // getch $str 0 = $ch
  {last: ["fct_ignore_line", "fct_eol", "eol"], current: "getch", regex: /^getch /},
  {last: ["getch"], current: "getch_from_gv", regex: GLOBAL_VAR},
  {last: ["getch"], current: "getch_from_lv", regex: LOCAL_VAR},
  {last: ["getch"], current: "getch_from_str", regex: STR},
  {last: ["getch_from_gv", "getch_from_lv", "getch_from_str"], current: "getch_from_", regex: SPACE},
  {last: ["getch_from_"], current: "getch_index_gv", regex: GLOBAL_VAR},
  {last: ["getch_from_"], current: "getch_index_lv", regex: LOCAL_VAR},
  {last: ["getch_from_"], current: "getch_index_int", regex: INT_POSITIVE},
  {last: ["getch_index_gv", "getch_index_lv", "getch_index_int"], current: "getch_assign", regex: ASSIGN},
  {last: ["getch_assign"], current: "getch_assign_gv", regex: GLOBAL_VAR},
  {last: ["getch_assign"], current: "getch_assign_lv", regex: LOCAL_VAR},
  {last: ["getch_assign_gv", "getch_assign_lv"], current: "eol", regex: EOL},

  // get %input "config" = $config
  {last: ["fct_ignore_line", "fct_eol", "eol"], current: "get", regex: /^get /},
  {last: ["get"], current: "get_from_gv", regex: GLOBAL_VAR},
  {last: ["get"], current: "get_from_lv", regex: LOCAL_VAR},
  {last: ["get_from_gv", "get_from_lv"], current: "get_from_", regex: SPACE},
  {last: ["get_from_"], current: "get_index_gv", regex: GLOBAL_VAR},
  {last: ["get_from_"], current: "get_index_lv", regex: LOCAL_VAR},
  {last: ["get_from_"], current: "get_index_int", regex: INT_POSITIVE},
  {last: ["get_from_"], current: "get_index_str", regex: STR},
  {last: ["get_index_gv", "get_index_lv", "get_index_int", "get_index_str"], current: "get_assign", regex: ASSIGN},
  {last: ["get_assign"], current: "get_assign_gv", regex: GLOBAL_VAR},
  {last: ["get_assign"], current: "get_assign_lv", regex: LOCAL_VAR},
  {last: ["get_assign_gv", "get_assign_lv"], current: "eol", regex: EOL},

  // has %input "config" = $test
  {last: ["fct_ignore_line", "fct_eol", "eol"], current: "has", regex: /^has /},
  {last: ["has"], current: "has_from_gv", regex: GLOBAL_VAR},
  {last: ["has"], current: "has_from_lv", regex: LOCAL_VAR},
  {last: ["has_from_gv", "has_from_lv"], current: "has_from_", regex: SPACE},
  {last: ["has_from_"], current: "has_index_gv", regex: GLOBAL_VAR},
  {last: ["has_from_"], current: "has_index_lv", regex: LOCAL_VAR},
  {last: ["has_from_"], current: "has_index_int", regex: INT_POSITIVE},
  {last: ["has_from_"], current: "has_index_str", regex: STR},
  {last: ["has_index_gv", "has_index_lv", "has_index_int", "has_index_str"], current: "has_assign", regex: ASSIGN},
  {last: ["has_assign"], current: "has_assign_gv", regex: GLOBAL_VAR},
  {last: ["has_assign"], current: "has_assign_lv", regex: LOCAL_VAR},
  {last: ["has_assign_gv", "has_assign_lv"], current: "eol", regex: EOL},

  // set $value pi
  {last: ["fct_ignore_line", "fct_eol", "eol"], current: "set", regex: /^set /},
  {last: ["set"], current: "set_to_gv", regex: GLOBAL_VAR},
  {last: ["set"], current: "set_to_lv", regex: LOCAL_VAR},
  {last: ["set_to_gv", "set_to_lv"], current: "set_to_", regex: SPACE},
  {last: ["set_to_"], current: "set_val_gv", regex: GLOBAL_VAR},
  {last: ["set_to_"], current: "set_val_lv", regex: LOCAL_VAR},
  {last: ["set_to_"], current: "set_val_nb", regex: NB},
  {last: ["set_to_"], current: "set_val_str", regex: STR},
  {last: ["set_to_"], current: "set_val_lit", regex: LIT},
  {last: ["set_val_gv", "set_val_lv", "set_val_nb", "set_val_str", "set_val_lit"], current: "eol", regex: EOL},

  // fopen "Test.java" = $file
  {last: ["fct_ignore_line", "fct_eol", "eol"], current: "fopen", regex: /^fopen /},
  {last: ["fopen"], current: "fopen_filename_gv", regex: GLOBAL_VAR},
  {last: ["fopen"], current: "fopen_filename_lv", regex: LOCAL_VAR},
  {last: ["fopen"], current: "fopen_filename_str", regex: STR},
  {last: ["fopen_filename_"], current: "fopen_type_gv", regex: GLOBAL_VAR},
  {last: ["fopen_filename_"], current: "fopen_type_lv", regex: LOCAL_VAR},
  {last: ["fopen_filename_"], current: "fopen_type_str", regex: STR},
  {
    last: [
      "fopen_filename_gv", "fopen_filename_lv", "fopen_filename_str",
      "fopen_type_gv", "fopen_type_lv", "fopen_type_str",
    ], current: "fopen_assign", regex: ASSIGN},
  {last: ["fopen_filename_gv", "fopen_filename_lv", "fopen_filename_str"], current: "fopen_filename_", regex: SPACE},
  {last: ["fopen_assign"], current: "fopen_assign_gv", regex: GLOBAL_VAR},
  {last: ["fopen_assign"], current: "fopen_assign_lv", regex: LOCAL_VAR},
  {last: ["fopen_assign_gv", "fopen_assign_lv"], current: "eol", regex: EOL},

  // fwrite $file $result
  {last: ["fct_ignore_line", "fct_eol", "eol"], current: "fwrite", regex: /^fwrite /},
  {last: ["fwrite"], current: "fwrite_to_gv", regex: GLOBAL_VAR},
  {last: ["fwrite"], current: "fwrite_to_lv", regex: LOCAL_VAR},
  {last: ["fwrite_to_gv", "fwrite_to_lv"], current: "fwrite_to_", regex: SPACE},
  {last: ["fwrite_to_"], current: "fwrite_val_gv", regex: GLOBAL_VAR},
  {last: ["fwrite_to_"], current: "fwrite_val_lv", regex: LOCAL_VAR},
  {last: ["fwrite_to_"], current: "fwrite_val_nb", regex: NB},
  {last: ["fwrite_to_"], current: "fwrite_val_str", regex: STR},
  {last: ["fwrite_val_gv", "fwrite_val_lv", "fwrite_val_nb", "fwrite_val_str"], current: "eol", regex: EOL},

  // panic "division by zero" $line $col
  {last: ["fct_ignore_line", "fct_eol", "eol"], current: "panic", regex: /^panic /},
  {last: ["panic"], current: "panic_msg_gv", regex: GLOBAL_VAR},
  {last: ["panic"], current: "panic_msg_lv", regex: LOCAL_VAR},
  {last: ["panic"], current: "panic_msg_str", regex: STR},
  {last: ["panic_msg_gv", "panic_msg_lv", "panic_msg_str"], current: "panic_msg_", regex: SPACE},
  {last: ["panic_msg_"], current: "panic_line_gv", regex: GLOBAL_VAR},
  {last: ["panic_msg_"], current: "panic_line_lv", regex: LOCAL_VAR},
  {last: ["panic_line_gv", "panic_line_lv"], current: "panic_line_", regex: SPACE},
  {last: ["panic_line_"], current: "panic_col_gv", regex: GLOBAL_VAR},
  {last: ["panic_line_"], current: "panic_col_lv", regex: LOCAL_VAR},
  {last: ["panic_msg_gv", "panic_msg_lv", "panic_msg_str", "panic_line_gv", "panic_line_lv", "panic_col_gv", "panic_col_lv"], current: "eol", regex: EOL},

  // add 1 2 = $result
  {last: ["fct_ignore_line", "fct_eol", "eol"], current: "math2", regex: /^(add|sub|mul|div|mod|pow)/},
  {last: ["math2"], current: "math2_", regex: SPACE},
  {last: ["math2_", "math2_sep"], current: "math2_val_gv", regex: GLOBAL_VAR},
  {last: ["math2_", "math2_sep"], current: "math2_val_lv", regex: LOCAL_VAR},
  {last: ["math2_", "math2_sep"], current: "math2_val_nb", regex: NB},
  {last: ["math2_val_gv", "math2_val_lv", "math2_val_nb"], current: "math2_sep", regex: SPACE},
  {last: ["math2_sep"], current: "math2_assign", regex: ASSIGN_B},
  {last: ["math2_assign"], current: "math2_assign_gv", regex: GLOBAL_VAR},
  {last: ["math2_assign"], current: "math2_assign_lv", regex: LOCAL_VAR},
  {last: ["math2_assign_gv", "math2_assign_lv"], current: "eol", regex: EOL},

  // atan2 5 2 = $result
  {last: ["fct_ignore_line", "fct_eol", "eol"], current: "atan2", regex: /^atan2/},
  {last: ["atan2"], current: "atan2_", regex: SPACE},
  {last: ["atan2_"], current: "atan2_val1_gv", regex: GLOBAL_VAR},
  {last: ["atan2_"], current: "atan2_val1_lv", regex: LOCAL_VAR},
  {last: ["atan2_"], current: "atan2_val1_nb", regex: NB},
  {last: ["atan2_val1_gv", "atan2_val1_lv", "atan2_val1_nb"], current: "atan2_sep", regex: SPACE},
  {last: ["atan2_sep"], current: "atan2_val2_gv", regex: GLOBAL_VAR},
  {last: ["atan2_sep"], current: "atan2_val2_lv", regex: LOCAL_VAR},
  {last: ["atan2_sep"], current: "atan2_val2_nb", regex: NB},
  {last: ["atan2_val2_gv", "atan2_val2_lv", "atan2_val2_nb"], current: "atan2_assign", regex: ASSIGN},
  {last: ["atan2_assign"], current: "atan2_assign_gv", regex: GLOBAL_VAR},
  {last: ["atan2_assign"], current: "atan2_assign_lv", regex: LOCAL_VAR},
  {last: ["atan2_assign_gv", "atan2_assign_lv"], current: "eol", regex: EOL},

  // sin 5 = $result
  {last: ["fct_ignore_line", "fct_eol", "eol"], current: "math", regex: /^(sqrt|cbrt|sin|cos|tan|sinh|cosh|tanh|asin|acos|atan|asinh|acosh|atanh|atan2|abs|ceil|floor|trunc|exp|expm1|log|log10|log1p|log2)/},
  {last: ["math"], current: "math_", regex: SPACE},
  {last: ["math_"], current: "math_val_gv", regex: GLOBAL_VAR},
  {last: ["math_"], current: "math_val_lv", regex: LOCAL_VAR},
  {last: ["math_"], current: "math_val_nb", regex: NB},
  {last: ["math_val_gv", "math_val_lv", "math_val_nb"], current: "math_assign", regex: ASSIGN},
  {last: ["math_assign"], current: "math_assign_gv", regex: GLOBAL_VAR},
  {last: ["math_assign"], current: "math_assign_lv", regex: LOCAL_VAR},
  {last: ["math_assign_gv", "math_assign_lv"], current: "eol", regex: EOL},

  // round $price 2 = $result
  {last: ["fct_ignore_line", "fct_eol", "eol"], current: "round", regex: /^round /},
  {last: ["round"], current: "round_val_gv", regex: GLOBAL_VAR},
  {last: ["round"], current: "round_val_lv", regex: LOCAL_VAR},
  {last: ["round"], current: "round_val_nb", regex: NB},
  {last: ["round_val_gv", "round_val_lv", "round_val_nb"], current: "round_val_", regex: SPACE},
  {last: ["round_val_"], current: "round_amount_gv", regex: GLOBAL_VAR},
  {last: ["round_val_"], current: "round_amount_lv", regex: LOCAL_VAR},
  {last: ["round_val_"], current: "round_amount_int", regex: INT},
  {last: ["round_amount_gv", "round_amount_lv", "round_amount_int"], current: "round_amount_", regex: SPACE},
  {last: ["round_val_", "round_amount_"], current: "round_assign", regex: ASSIGN_B},
  {last: ["round_assign"], current: "round_assign_gv", regex: GLOBAL_VAR},
  {last: ["round_assign"], current: "round_assign_lv", regex: LOCAL_VAR},
  {last: ["round_assign_gv", "round_assign_lv"], current: "eol", regex: EOL},

  // hypot 4 9.2 2.21 = $result
  {last: ["fct_ignore_line", "fct_eol", "eol"], current: "hypot", regex: /^hypot /},
  {last: ["hypot", "hypot_sep"], current: "hypot_val_gv", regex: GLOBAL_VAR},
  {last: ["hypot", "hypot_sep"], current: "hypot_val_lv", regex: LOCAL_VAR},
  {last: ["hypot", "hypot_sep"], current: "hypot_val_nb", regex: NB},
  {last: ["hypot_val_gv", "hypot_val_lv", "hypot_val_nb"], current: "hypot_sep", regex: SPACE},
  {last: ["hypot_sep"], current: "hypot_assign", regex: ASSIGN_B},
  {last: ["hypot_assign"], current: "hypot_assign_gv", regex: GLOBAL_VAR},
  {last: ["hypot_assign"], current: "hypot_assign_lv", regex: LOCAL_VAR},
  {last: ["hypot_assign_gv", "hypot_assign_lv"], current: "eol", regex: EOL},

  // max 4 9 5 = $test
  {last: ["fct_ignore_line", "fct_eol", "eol"], current: "nbcmp", regex: /^(min|max|lt|lte|gt|gte)/},
  {last: ["nbcmp"], current: "nbcmp_", regex: SPACE},
  {last: ["nbcmp_", "nbcmp_sep"], current: "nbcmp_val_gv", regex: GLOBAL_VAR},
  {last: ["nbcmp_", "nbcmp_sep"], current: "nbcmp_val_lv", regex: LOCAL_VAR},
  {last: ["nbcmp_", "nbcmp_sep"], current: "nbcmp_val_nb", regex: NB},
  {last: ["nbcmp_val_gv", "nbcmp_val_lv", "nbcmp_val_nb"], current: "nbcmp_sep", regex: SPACE},
  {last: ["nbcmp_sep"], current: "nbcmp_assign", regex: ASSIGN_B},
  {last: ["nbcmp_assign"], current: "nbcmp_assign_gv", regex: GLOBAL_VAR},
  {last: ["nbcmp_assign"], current: "nbcmp_assign_lv", regex: LOCAL_VAR},
  {last: ["nbcmp_assign_gv", "nbcmp_assign_lv"], current: "eol", regex: EOL},

  // eq $val 5 = $test
  {last: ["fct_ignore_line", "fct_eol", "eol"], current: "eq", regex: /^(eq|ne)/},
  {last: ["eq"], current: "eq_", regex: SPACE},
  {last: ["eq_", "eq_sep"], current: "eq_val_gv", regex: GLOBAL_VAR},
  {last: ["eq_", "eq_sep"], current: "eq_val_lv", regex: LOCAL_VAR},
  {last: ["eq_", "eq_sep"], current: "eq_val_nb", regex: NB},
  {last: ["eq_", "eq_sep"], current: "eq_val_str", regex: STR},
  {last: ["eq_", "eq_sep"], current: "eq_val_lit", regex: NULL_BOOL},
  {last: ["eq_val_gv", "eq_val_lv", "eq_val_nb", "eq_val_str", "eq_val_lit"], current: "eq_sep", regex: SPACE},
  {last: ["eq_sep"], current: "eq_assign", regex: ASSIGN_B},
  {last: ["eq_assign"], current: "eq_assign_gv", regex: GLOBAL_VAR},
  {last: ["eq_assign"], current: "eq_assign_lv", regex: LOCAL_VAR},
  {last: ["eq_assign_gv", "eq_assign_lv"], current: "eol", regex: EOL},

  // xor 1 2 3 = $result
  {last: ["fct_ignore_line", "fct_eol", "eol"], current: "xor", regex: /^xor /},
  {last: ["xor", "xor_sep"], current: "xor_val_gv", regex: GLOBAL_VAR},
  {last: ["xor", "xor_sep"], current: "xor_val_lv", regex: LOCAL_VAR},
  {last: ["xor", "xor_sep"], current: "xor_val_int", regex: INT},
  {last: ["xor_val_gv", "xor_val_lv", "xor_val_int"], current: "xor_sep", regex: SPACE},
  {last: ["xor_sep"], current: "xor_assign", regex: ASSIGN_B},
  {last: ["xor_assign"], current: "xor_assign_gv", regex: GLOBAL_VAR},
  {last: ["xor_assign"], current: "xor_assign_lv", regex: LOCAL_VAR},
  {last: ["xor_assign_gv", "xor_assign_lv"], current: "eol", regex: EOL},

  // or true false = $result
  {last: ["fct_ignore_line", "fct_eol", "eol"], current: "or", regex: /^(or|and)/},
  {last: ["or"], current: "or_", regex: SPACE},
  {last: ["or_", "or_sep"], current: "or_val_gv", regex: GLOBAL_VAR},
  {last: ["or_", "or_sep"], current: "or_val_lv", regex: LOCAL_VAR},
  {last: ["or_", "or_sep"], current: "or_val_int", regex: INT},
  {last: ["or_", "or_sep"], current: "or_val_bool", regex: BOOL},
  {last: ["or_val_gv", "or_val_lv", "or_val_int", "or_val_bool"], current: "or_sep", regex: SPACE},
  {last: ["or_sep"], current: "or_assign", regex: ASSIGN_B},
  {last: ["or_assign"], current: "or_assign_gv", regex: GLOBAL_VAR},
  {last: ["or_assign"], current: "or_assign_lv", regex: LOCAL_VAR},
  {last: ["or_assign_gv", "or_assign_lv"], current: "eol", regex: EOL},

  // lshift $val 1 = $result
  {last: ["fct_ignore_line", "fct_eol", "eol"], current: "shift", regex: /^(lshift|rshift)/},
  {last: ["shift"], current: "shift_", regex: SPACE},
  {last: ["shift_"], current: "shift_from_gv", regex: GLOBAL_VAR},
  {last: ["shift_"], current: "shift_from_lv", regex: LOCAL_VAR},
  {last: ["shift_"], current: "shift_from_int", regex: INT_POSITIVE},
  {last: ["shift_from_gv", "shift_from_lv", "shift_from_int"], current: "shift_from_", regex: SPACE},
  {last: ["shift_from_"], current: "shift_index_gv", regex: GLOBAL_VAR},
  {last: ["shift_from_"], current: "shift_index_lv", regex: LOCAL_VAR},
  {last: ["shift_from_"], current: "shift_index_int", regex: INT_POSITIVE},
  {last: ["shift_index_gv", "shift_index_lv", "shift_index_int"], current: "shift_assign", regex: ASSIGN},
  {last: ["shift_assign"], current: "shift_assign_gv", regex: GLOBAL_VAR},
  {last: ["shift_assign"], current: "shift_assign_lv", regex: LOCAL_VAR},
  {last: ["shift_assign_gv", "shift_assign_lv"], current: "eol", regex: EOL},

  // not $val = $result ($val: bool|int) (! ~)
  {last: ["fct_ignore_line", "fct_eol", "eol"], current: "not", regex: /^not /},
  {last: ["not"], current: "not_from_gv", regex: GLOBAL_VAR},
  {last: ["not"], current: "not_from_lv", regex: LOCAL_VAR},
  {last: ["not"], current: "not_from_int", regex: INT},
  {last: ["not_from_gv", "not_from_lv", "not_from_int"], current: "not_assign", regex: ASSIGN},
  {last: ["not_assign"], current: "not_assign_gv", regex: GLOBAL_VAR},
  {last: ["not_assign"], current: "not_assign_lv", regex: LOCAL_VAR},
  {last: ["not_assign_gv", "not_assign_lv"], current: "eol", regex: EOL},

  // nan $val = $result
  {last: ["fct_ignore_line", "fct_eol", "eol"], current: "nan", regex: /^nan /},
  {last: ["nan"], current: "nan_from_gv", regex: GLOBAL_VAR},
  {last: ["nan"], current: "nan_from_lv", regex: LOCAL_VAR},
  {last: ["nan_from_gv", "nan_from_lv"], current: "nan_assign", regex: ASSIGN},
  {last: ["nan_assign"], current: "nan_assign_gv", regex: GLOBAL_VAR},
  {last: ["nan_assign"], current: "nan_assign_lv", regex: LOCAL_VAR},
  {last: ["nan_assign_gv", "nan_assign_lv"], current: "eol", regex: EOL},

  // len $val = $result
  {last: ["fct_ignore_line", "fct_eol", "eol"], current: "len", regex: /^len /},
  {last: ["len"], current: "len_from_gv", regex: GLOBAL_VAR},
  {last: ["len"], current: "len_from_lv", regex: LOCAL_VAR},
  {last: ["len_from_gv", "len_from_lv"], current: "len_assign", regex: ASSIGN},
  {last: ["len_assign"], current: "len_assign_gv", regex: GLOBAL_VAR},
  {last: ["len_assign"], current: "len_assign_lv", regex: LOCAL_VAR},
  {last: ["len_assign_gv", "len_assign_lv"], current: "eol", regex: EOL},

  // cp $map = $result
  {last: ["fct_ignore_line", "fct_eol", "eol"], current: "cp", regex: /^(cp|dcp)/},
  {last: ["cp"], current: "cp_", regex: SPACE},
  {last: ["cp_"], current: "cp_from_gv", regex: GLOBAL_VAR},
  {last: ["cp_"], current: "cp_from_lv", regex: LOCAL_VAR},
  {last: ["cp_from_gv", "cp_from_lv"], current: "cp_assign", regex: ASSIGN},
  {last: ["cp_assign"], current: "cp_assign_gv", regex: GLOBAL_VAR},
  {last: ["cp_assign"], current: "cp_assign_lv", regex: LOCAL_VAR},
  {last: ["cp_assign_gv", "cp_assign_lv"], current: "eol", regex: EOL},

  // _int $val = $result
  {last: ["fct_ignore_line", "fct_eol", "eol"], current: "convert", regex: /^(_int|_nb|_str|_char|_date|_b64|b64_|_b64url|b64url_|json_|_scase|_uscase|_ccase|_uccase|_kcase|_ukcase|_lcase|_ucase|_tcase)/},
  {last: ["convert"], current: "convert_", regex: SPACE},
  {last: ["convert_"], current: "convert_from_gv", regex: GLOBAL_VAR},
  {last: ["convert_"], current: "convert_from_lv", regex: LOCAL_VAR},
  {last: ["convert_from_gv", "convert_from_lv"], current: "convert_assign", regex: ASSIGN},
  {last: ["convert_assign"], current: "convert_assign_gv", regex: GLOBAL_VAR},
  {last: ["convert_assign"], current: "convert_assign_lv", regex: LOCAL_VAR},
  {last: ["convert_assign_gv", "convert_assign_lv"], current: "eol", regex: EOL},

  // _json $map 2 = $json
  // _json $map = $json
  {last: ["fct_ignore_line", "fct_eol", "eol"], current: "json", regex: /^_json /},
  {last: ["json"], current: "json_from_gv", regex: GLOBAL_VAR},
  {last: ["json"], current: "json_from_lv", regex: LOCAL_VAR},
  {last: ["json_from_gv", "json_from_lv"], current: "json_from_", regex: SPACE},
  {last: ["json_from_"], current: "json_indent_int", regex: INT_POSITIVE},
  {last: ["json_from_"], current: "json_indent_tab", regex: /^"\\t"/},
  {last: ["json_indent_int", "json_indent_tab", "json_from_gv", "json_from_lv"], current: "json_assign", regex: ASSIGN},
  {last: ["json_assign"], current: "json_assign_gv", regex: GLOBAL_VAR},
  {last: ["json_assign"], current: "json_assign_lv", regex: LOCAL_VAR},
  {last: ["json_assign_gv", "json_assign_lv"], current: "eol", regex: EOL},

  // rand $min $exclusive_max = $result
  {last: ["fct_ignore_line", "fct_eol", "eol"], current: "rand", regex: /^rand /},
  {last: ["rand"], current: "rand_min_gv", regex: GLOBAL_VAR},
  {last: ["rand"], current: "rand_min_lv", regex: LOCAL_VAR},
  {last: ["rand"], current: "rand_min_int", regex: INT_POSITIVE},
  {last: ["rand_min_gv", "rand_min_lv", "rand_min_int"], current: "rand_min_", regex: SPACE},
  {last: ["rand_min_"], current: "rand_max_gv", regex: GLOBAL_VAR},
  {last: ["rand_min_"], current: "rand_max_lv", regex: LOCAL_VAR},
  {last: ["rand_min_"], current: "rand_max_int", regex: INT_POSITIVE},
  {last: ["rand_max_gv", "rand_max_lv", "rand_max_int"], current: "rand_assign", regex: ASSIGN},
  {last: ["rand_assign"], current: "rand_assign_gv", regex: GLOBAL_VAR},
  {last: ["rand_assign"], current: "rand_assign_lv", regex: LOCAL_VAR},
  {last: ["rand_assign_gv", "rand_assign_lv"], current: "eol", regex: EOL},

  // loop $arr :loop_add
  // loop !$start..<$end!$inc :loop_add $arr
  // loop !$start..<$end!$inc :loop_add $arr = $result
  {last: ["fct_ignore_line", "fct_eol", "eol"], current: "loop", regex: /^loop /},
  {last: ["loop"], current: "loop_from_gv", regex: GLOBAL_VAR},
  {last: ["loop"], current: "loop_from_lv", regex: LOCAL_VAR},
  {last: ["loop"], current: "loop_from_range", regex: EXCLAMATION},
  {last: ["loop_from_range"], current: "loop_from_range_min_int", regex: INT_POSITIVE},
  {last: ["loop_from_range"], current: "loop_from_range_min_gv", regex: GLOBAL_VAR},
  {last: ["loop_from_range"], current: "loop_from_range_min_lv", regex: LOCAL_VAR},
  {last: ["loop_from_range_min_int", "loop_from_range_min_gv", "loop_from_range_min_lv"], current: "loop_from_range_exc", regex: RANGE_EXC},
  {last: ["loop_from_range_min_int", "loop_from_range_min_gv", "loop_from_range_min_lv"], current: "loop_from_range_inc", regex: RANGE_INC},
  {last: ["loop_from_range_inc", "loop_from_range_exc"], current: "loop_from_range_max_int", regex: INT_POSITIVE},
  {last: ["loop_from_range_inc", "loop_from_range_exc"], current: "loop_from_range_max_gv", regex: GLOBAL_VAR},
  {last: ["loop_from_range_inc", "loop_from_range_exc"], current: "loop_from_range_max_lv", regex: LOCAL_VAR},
  {last: ["loop_from_range_max_int", "loop_from_range_max_gv", "loop_from_range_max_lv"], current: "loop_from_range_step_sep", regex: EXCLAMATION},
  {last: ["loop_from_range_step_sep"], current: "loop_from_range_step_int", regex: INT},
  {last: ["loop_from_range_step_sep"], current: "loop_from_range_step_gv", regex: GLOBAL_VAR},
  {last: ["loop_from_range_step_sep"], current: "loop_from_range_step_lv", regex: LOCAL_VAR},
  {last: ["loop_from_gv", "loop_from_lv", "loop_from_range_step_int", "loop_from_range_step_gv", "loop_from_range_step_lv"], current: "loop_from_", regex: SPACE},
  {last: ["loop_from_"], current: "loop_fct", regex: FUNCTION},
  {last: ["loop_fct", "loop_param_gv", "loop_param_lv", "loop_param_nb", "loop_param_str", "loop_param_lit"], current: "loop_fct_", regex: SPACE},
  {last: ["loop_fct_"], current: "loop_param_gv", regex: GLOBAL_VAR},
  {last: ["loop_fct_"], current: "loop_param_lv", regex: LOCAL_VAR},
  {last: ["loop_fct_"], current: "loop_param_nb", regex: NB},
  {last: ["loop_fct_"], current: "loop_param_str", regex: STR},
  {last: ["loop_fct_"], current: "loop_param_lit", regex: LIT},
  {last: ["loop_fct_"], current: "loop_assign", regex: ASSIGN_B},
  {last: ["loop_assign"], current: "loop_assign_gv", regex: GLOBAL_VAR},
  {last: ["loop_assign"], current: "loop_assign_lv", regex: LOCAL_VAR},
  {last: ["loop_fct", "loop_param_gv", "loop_param_lv", "loop_param_nb", "loop_param_str", "loop_param_lit", "loop_assign_gv", "loop_assign_lv"], current: "eol", regex: EOL},

  // switch $op "+":add "-":sub "*":mul "/":div
  {last: ["fct_ignore_line", "fct_eol", "eol"], current: "switch", regex: /^switch /},
  {last: ["switch"], current: "switch_val_gv", regex: GLOBAL_VAR},
  {last: ["switch"], current: "switch_val_lv", regex: LOCAL_VAR},
  {last: ["switch_val_gv", "switch_val_lv", "switch_cmp_fct"], current: "switch_val_", regex: SPACE},
  {last: ["switch_val_"], current: "switch_cmp_nb", regex: NB},
  {last: ["switch_val_"], current: "switch_cmp_str", regex: STR},
  {last: ["switch_val_"], current: "switch_cmp_lit", regex: NULL_BOOL},
  {last: ["switch_cmp_nb", "switch_cmp_str", "switch_cmp_lit"], current: "switch_cmp_fct", regex: FUNCTION},
  {last: ["switch_cmp_fct"], current: "eol", regex: EOL},

  // if $test :fct = $result
  // if $test return $value
  {last: ["fct_ignore_line", "fct_eol", "eol"], current: "if", regex: /^if /},
  {last: ["if"], current: "if_from_gv", regex: GLOBAL_VAR},
  {last: ["if"], current: "if_from_lv", regex: LOCAL_VAR},
  {last: ["if_from_gv", "if_from_lv"], current: "if_from_", regex: SPACE},
  {last: ["if_from_"], current: "if_continue", regex: /^continue/},
  {last: ["if_from_"], current: "if_break", regex: /^break/},
  {last: ["if_break"], current: "if_break_", regex: SPACE},
  {last: ["if_break_"], current: "if_break_gv", regex: GLOBAL_VAR},
  {last: ["if_break_"], current: "if_break_lv", regex: LOCAL_VAR},
  {last: ["if_break_"], current: "if_break_nb", regex: NB},
  {last: ["if_break_"], current: "if_break_str", regex: STR},
  {last: ["if_break_"], current: "if_break_lit", regex: LIT},
  {last: ["if_from_"], current: "if_return", regex: /^return/},
  {last: ["if_return"], current: "if_return_", regex: SPACE},
  {last: ["if_return_"], current: "if_return_gv", regex: GLOBAL_VAR},
  {last: ["if_return_"], current: "if_return_lv", regex: LOCAL_VAR},
  {last: ["if_return_"], current: "if_return_nb", regex: NB},
  {last: ["if_return_"], current: "if_return_str", regex: STR},
  {last: ["if_return_"], current: "if_return_lit", regex: LIT},
  {last: ["if_from_"], current: "if_fct", regex: FUNCTION},
  {last: ["if_fct", "if_param_gv", "if_param_lv", "if_param_nb", "if_param_str", "if_param_lit"], current: "if_fct_", regex: SPACE},
  {last: ["if_fct_"], current: "if_param_gv", regex: GLOBAL_VAR},
  {last: ["if_fct_"], current: "if_param_lv", regex: LOCAL_VAR},
  {last: ["if_fct_"], current: "if_param_nb", regex: NB},
  {last: ["if_fct_"], current: "if_param_str", regex: STR},
  {last: ["if_fct_"], current: "if_param_lit", regex: LIT},
  {last: ["if_fct_"], current: "if_assign", regex: ASSIGN_B},
  {last: ["if_assign"], current: "if_assign_gv", regex: GLOBAL_VAR},
  {last: ["if_assign"], current: "if_assign_lv", regex: LOCAL_VAR},
  {last: [
    "if_continue", "if_fct", "if_assign_gv", "if_assign_lv",
    "if_break", "if_break_gv", "if_break_lv", "if_break_nb", "if_break_str", "if_break_lit",
    "if_return", "if_return_gv", "if_return_lv", "if_return_nb", "if_return_str", "if_return_lit",
    "if_param_gv", "if_param_lv", "if_param_nb", "if_param_str", "if_param_lit"
  ], current: "eol", regex: EOL},

  // call :fct $param = $result
  {last: ["fct_ignore_line", "fct_eol", "eol"], current: "call", regex: /^call /},
  {last: ["call"], current: "call_fct", regex: FUNCTION},
  {last: ["call_fct", "call_param_gv", "call_param_lv", "call_param_nb", "call_param_str", "call_param_lit"], current: "call_fct_", regex: SPACE},
  {last: ["call_fct_"], current: "call_param_gv", regex: GLOBAL_VAR},
  {last: ["call_fct_"], current: "call_param_lv", regex: LOCAL_VAR},
  {last: ["call_fct_"], current: "call_param_nb", regex: NB},
  {last: ["call_fct_"], current: "call_param_str", regex: STR},
  {last: ["call_fct_"], current: "call_param_lit", regex: LIT},
  {last: ["call_fct_"], current: "call_assign", regex: ASSIGN_B},
  {last: ["call_assign"], current: "call_assign_gv", regex: GLOBAL_VAR},
  {last: ["call_assign"], current: "call_assign_lv", regex: LOCAL_VAR},
  {last: [
    "call_fct", "call_param_gv", "call_param_lv", "call_param_nb",
    "call_param_str", "call_param_lit", "call_assign_gv", "call_assign_lv"
  ], current: "eol", regex: EOL},

  // return $val
  {last: ["fct_ignore_line", "fct_eol", "eol"], current: "return", regex: /^return /},
  {last: ["return"], current: "return_gv", regex: GLOBAL_VAR},
  {last: ["return"], current: "return_lv", regex: LOCAL_VAR},
  {last: ["return"], current: "return_nb", regex: NB},
  {last: ["return"], current: "return_str", regex: STR},
  {last: ["return"], current: "return_lit", regex: LIT},
  {last: ["return_gv", "return_lv", "return_nb", "return_str", "return_lit"], current: "eol", regex: EOL},

  // concat "hello, " $name = $output
  {last: ["fct_ignore_line", "fct_eol", "eol"], current: "concat", regex: /^concat/},
  {last: ["concat", "concat_param_gv", "concat_param_lv", "concat_param_nb", "concat_param_str"], current: "concat_", regex: SPACE},
  {last: ["concat_"], current: "concat_param_gv", regex: GLOBAL_VAR},
  {last: ["concat_"], current: "concat_param_lv", regex: LOCAL_VAR},
  {last: ["concat_"], current: "concat_param_nb", regex: NB},
  {last: ["concat_"], current: "concat_param_str", regex: STR},
  {last: ["concat_"], current: "concat_assign", regex: ASSIGN_B},
  {last: ["concat_assign"], current: "concat_assign_gv", regex: GLOBAL_VAR},
  {last: ["concat_assign"], current: "concat_assign_lv", regex: LOCAL_VAR},
  {last: ["concat_assign_gv", "concat_assign_lv"], current: "eol", regex: EOL},

  // mset $map "field1":0 "field2":[]
  {last: ["fct_ignore_line", "fct_eol", "eol"], current: "mset", regex: /^mset /},
  {last: ["mset"], current: "mset_from_gv", regex: GLOBAL_VAR},
  {last: ["mset"], current: "mset_from_lv", regex: LOCAL_VAR},
  {last: ["mset_from_gv", "mset_from_lv", "mset_val_str", "mset_val_nb", "mset_val_str", "mset_val_lit"], current: "mset_from_", regex: SPACE},
  {last: ["mset_from_"], current: "mset_field", regex: STR},
  {last: ["mset_field"], current: "mset_field_", regex: /^:/},
  {last: ["mset_field_"], current: "mset_val_gv", regex: GLOBAL_VAR},
  {last: ["mset_field_"], current: "mset_val_lv", regex: LOCAL_VAR},
  {last: ["mset_field_"], current: "mset_val_nb", regex: NB},
  {last: ["mset_field_"], current: "mset_val_str", regex: STR},
  {last: ["mset_field_"], current: "mset_val_lit", regex: LIT},
  {last: ["mset_val_gv", "mset_val_lv", "mset_val_nb", "mset_val_str", "mset_val_lit"], current: "eol", regex: EOL},

  // split $str "[a-z]" = $result
  // match $str "[a-z]" = $result
  {last: ["fct_ignore_line", "fct_eol", "eol"], current: "split", regex: /^(split|match)/},
  {last: ["split"], current: "split_", regex: SPACE},
  {last: ["split_"], current: "split_from_gv", regex: GLOBAL_VAR},
  {last: ["split_"], current: "split_from_lv", regex: LOCAL_VAR},
  {last: ["split_"], current: "split_from_str", regex: STR},
  {last: ["split_from_gv", "split_from_lv", "split_from_str"], current: "split_from_", regex: SPACE},
  {last: ["split_from_"], current: "split_regex_gv", regex: GLOBAL_VAR},
  {last: ["split_from_"], current: "split_regex_lv", regex: LOCAL_VAR},
  {last: ["split_from_"], current: "split_regex_str", regex: STR},
  {last: ["split_regex_gv", "split_regex_lv", "split_regex_str"], current: "split_assign", regex: ASSIGN},
  {last: ["split_assign"], current: "split_assign_gv", regex: GLOBAL_VAR},
  {last: ["split_assign"], current: "split_assign_lv", regex: LOCAL_VAR},
  {last: ["split_assign_gv", "split_assign_lv"], current: "eol", regex: EOL},

  // replace $str "[a-z]" "x" = $result
  {last: ["fct_ignore_line", "fct_eol", "eol"], current: "replace", regex: /^replace /},
  {last: ["replace"], current: "replace_from_gv", regex: GLOBAL_VAR},
  {last: ["replace"], current: "replace_from_lv", regex: LOCAL_VAR},
  {last: ["replace"], current: "replace_from_str", regex: STR},
  {last: ["replace_from_gv", "replace_from_lv", "replace_from_str"], current: "replace_from_", regex: SPACE},
  {last: ["replace_from_"], current: "replace_regex_gv", regex: GLOBAL_VAR},
  {last: ["replace_from_"], current: "replace_regex_lv", regex: LOCAL_VAR},
  {last: ["replace_from_"], current: "replace_regex_str", regex: STR},
  {last: ["replace_regex_gv", "replace_regex_lv", "replace_regex_str"], current: "replace_regex_", regex: SPACE},
  {last: ["replace_regex_"], current: "replace_to_gv", regex: GLOBAL_VAR},
  {last: ["replace_regex_"], current: "replace_to_lv", regex: LOCAL_VAR},
  {last: ["replace_regex_"], current: "replace_to_str", regex: STR},
  {last: ["replace_to_gv", "replace_to_lv", "replace_to_str"], current: "replace_assign", regex: ASSIGN},
  {last: ["replace_assign"], current: "replace_assign_gv", regex: GLOBAL_VAR},
  {last: ["replace_assign"], current: "replace_assign_lv", regex: LOCAL_VAR},
  {last: ["replace_assign_gv", "replace_assign_lv"], current: "eol", regex: EOL},

  // lpad 2 0
  // rpad 2 ""
  {last: ["fct_ignore_line", "fct_eol", "eol"], current: "pad", regex: /^(lpad|rpad)/},
  {last: ["pad"], current: "pad_", regex: SPACE},
  {last: ["pad_"], current: "pad_from_gv", regex: GLOBAL_VAR},
  {last: ["pad_"], current: "pad_from_lv", regex: LOCAL_VAR},
  {last: ["pad_"], current: "pad_from_nb", regex: NB},
  {last: ["pad_"], current: "pad_from_str", regex: STR},
  {last: ["pad_from_gv", "pad_from_lv", "pad_from_nb", "pad_from_str"], current: "pad_from_", regex: SPACE},
  {last: ["pad_from_"], current: "pad_amount_gv", regex: GLOBAL_VAR},
  {last: ["pad_from_"], current: "pad_amount_lv", regex: LOCAL_VAR},
  {last: ["pad_from_"], current: "pad_amount_int", regex: INT},
  {last: ["pad_amount_gv", "pad_amount_lv", "pad_amount_int"], current: "pad_amount_", regex: SPACE},
  {last: ["pad_amount_"], current: "pad_val_gv", regex: GLOBAL_VAR},
  {last: ["pad_amount_"], current: "pad_val_lv", regex: LOCAL_VAR},
  {last: ["pad_amount_"], current: "pad_val_nb", regex: NB},
  {last: ["pad_amount_"], current: "pad_val_str", regex: STR},
  {last: ["pad_val_gv", "pad_val_lv", "pad_val_nb", "pad_val_str"], current: "pad_assign", regex: ASSIGN},
  {last: ["pad_assign"], current: "pad_assign_gv", regex: GLOBAL_VAR},
  {last: ["pad_assign"], current: "pad_assign_lv", regex: LOCAL_VAR},
  {last: ["pad_assign_gv", "pad_assign_lv"], current: "eol", regex: EOL},

  // slice $str 0 99 = $result
  {last: ["fct_ignore_line", "fct_eol", "eol"], current: "slice", regex: /^slice /},
  {last: ["slice"], current: "slice_from_gv", regex: GLOBAL_VAR},
  {last: ["slice"], current: "slice_from_lv", regex: LOCAL_VAR},
  {last: ["slice"], current: "slice_from_str", regex: STR},
  {last: ["slice_from_gv", "slice_from_lv", "slice_from_str"], current: "slice_from_", regex: SPACE},
  {last: ["slice_from_"], current: "slice_min_gv", regex: GLOBAL_VAR},
  {last: ["slice_from_"], current: "slice_min_lv", regex: LOCAL_VAR},
  {last: ["slice_from_"], current: "slice_min_nb", regex: INT},
  {last: ["slice_min_gv", "slice_min_lv", "slice_min_nb"], current: "slice_min_", regex: SPACE},
  {last: ["slice_min_"], current: "slice_max_gv", regex: GLOBAL_VAR},
  {last: ["slice_min_"], current: "slice_max_lv", regex: LOCAL_VAR},
  {last: ["slice_min_"], current: "slice_max_nb", regex: INT},
  {
    last: [
      "slice_min_gv", "slice_min_lv", "slice_min_nb",
      "slice_max_gv", "slice_max_lv", "slice_max_nb",
    ], current: "slice_assign", regex: ASSIGN
  },
  {last: ["slice_assign"], current: "slice_assign_gv", regex: GLOBAL_VAR},
  {last: ["slice_assign"], current: "slice_assign_lv", regex: LOCAL_VAR},
  {last: ["slice_assign_gv", "slice_assign_lv"], current: "eol", regex: EOL},

  // linit $arr 1 2 3 4 5 $a $b
  {last: ["fct_ignore_line", "fct_eol", "eol"], current: "linit", regex: /^linit /},
  {last: ["linit"], current: "linit_from_gv", regex: GLOBAL_VAR},
  {last: ["linit"], current: "linit_from_lv", regex: LOCAL_VAR},
  {
    last: [
      "linit_from_gv", "linit_from_lv", "linit_param_gv", "linit_param_lv",
      "linit_param_nb", "linit_param_str", "linit_param_lit"
    ], current: "linit_from_", regex: SPACE
  },
  {last: ["linit_from_"], current: "linit_param_gv", regex: GLOBAL_VAR},
  {last: ["linit_from_"], current: "linit_param_lv", regex: LOCAL_VAR},
  {last: ["linit_from_"], current: "linit_param_nb", regex: NB},
  {last: ["linit_from_"], current: "linit_param_str", regex: STR},
  {last: ["linit_from_"], current: "linit_param_lit", regex: CONST},
  {last: ["linit_param_gv", "linit_param_lv", "linit_param_nb", "linit_param_str", "linit_param_lit"], current: "eol", regex: EOL},

  // lfill $arr $len {}
  // lfill $arr 65..90:1
  {last: ["fct_ignore_line", "fct_eol", "eol"], current: "lfill", regex: /^lfill /},
  {last: ["lfill"], current: "lfill_", regex: SPACE},
  {last: ["lfill_"], current: "lfill_from_gv", regex: GLOBAL_VAR},
  {last: ["lfill_"], current: "lfill_from_lv", regex: LOCAL_VAR},
  {last: ["lfill_from_gv", "lfill_from_lv"], current: "lfill_from_", regex: SPACE},
  {last: ["lfill_from_"], current: "lfill_amount_gv", regex: GLOBAL_VAR},
  {last: ["lfill_from_"], current: "lfill_amount_lv", regex: LOCAL_VAR},
  {last: ["lfill_from_"], current: "lfill_amount_int", regex: INT_POSITIVE},
  {last: ["lfill_from_"], current: "lfill_range", regex: EXCLAMATION},
  {last: ["lfill_range"], current: "lfill_range_min_int", regex: INT_POSITIVE},
  {last: ["lfill_range"], current: "lfill_range_min_gv", regex: GLOBAL_VAR},
  {last: ["lfill_range"], current: "lfill_range_min_lv", regex: LOCAL_VAR},
  {last: ["lfill_range_min_int", "lfill_range_min_gv", "lfill_range_min_lv"], current: "lfill_range_exc", regex: RANGE_EXC},
  {last: ["lfill_range_min_int", "lfill_range_min_gv", "lfill_range_min_lv"], current: "lfill_range_inc", regex: RANGE_INC},
  {last: ["lfill_range_inc", "lfill_range_exc"], current: "lfill_range_max_int", regex: INT_POSITIVE},
  {last: ["lfill_range_inc", "lfill_range_exc"], current: "lfill_range_max_gv", regex: GLOBAL_VAR},
  {last: ["lfill_range_inc", "lfill_range_exc"], current: "lfill_range_max_lv", regex: LOCAL_VAR},
  {last: ["lfill_range_max_int", "lfill_range_max_gv", "lfill_range_max_lv"], current: "lfill_range_step_sep", regex: EXCLAMATION},
  {last: ["lfill_range_step_sep"], current: "lfill_range_step_int", regex: INT},
  {last: ["lfill_range_step_sep"], current: "lfill_range_step_gv", regex: GLOBAL_VAR},
  {last: ["lfill_range_step_sep"], current: "lfill_range_step_lv", regex: LOCAL_VAR},
  {last: ["lfill_amount_gv", "lfill_amount_lv", "lfill_amount_int"], current: "lfill_amount_", regex: SPACE},
  {last: ["lfill_amount_"], current: "lfill_val_gv", regex: GLOBAL_VAR},
  {last: ["lfill_amount_"], current: "lfill_val_lv", regex: LOCAL_VAR},
  {last: ["lfill_amount_"], current: "lfill_val_nb", regex: NB},
  {last: ["lfill_amount_"], current: "lfill_val_str", regex: STR},
  {last: ["lfill_amount_"], current: "lfill_val_lit", regex: LIT},
  {
    last: [
      "lfill_val_gv", "lfill_val_lv", "lfill_val_nb", "lfill_val_str", "lfill_val_lit",
      "lfill_range_step_int", "lfill_range_step_gv", "lfill_range_step_lv"
    ], current: "eol", regex: EOL
  },

  // lcat $arr $arr2 = $output
  {last: ["fct_ignore_line", "fct_eol", "eol"], current: "lcat", regex: /^lcat/},
  {last: ["lcat", "lcat_param_gv", "lcat_param_lv"], current: "lcat_", regex: SPACE},
  {last: ["lcat_"], current: "lcat_param_gv", regex: GLOBAL_VAR},
  {last: ["lcat_"], current: "lcat_param_lv", regex: LOCAL_VAR},
  {last: ["lcat_"], current: "lcat_assign", regex: ASSIGN_B},
  {last: ["lcat_assign"], current: "lcat_assign_gv", regex: GLOBAL_VAR},
  {last: ["lcat_assign"], current: "lcat_assign_lv", regex: LOCAL_VAR},
  {last: ["lcat_assign_gv", "lcat_assign_lv"], current: "eol", regex: EOL},

  // lpush $arr "a"
  {last: ["fct_ignore_line", "fct_eol", "eol"], current: "lpush", regex: /^lpush /},
  {last: ["lpush"], current: "lpush_from_gv", regex: GLOBAL_VAR},
  {last: ["lpush"], current: "lpush_from_lv", regex: LOCAL_VAR},
  {last: ["lpush_from_gv", "lpush_from_lv"], current: "lpush_from_", regex: SPACE},
  {last: ["lpush_from_"], current: "lpush_val_gv", regex: GLOBAL_VAR},
  {last: ["lpush_from_"], current: "lpush_val_lv", regex: LOCAL_VAR},
  {last: ["lpush_from_"], current: "lpush_val_nb", regex: NB},
  {last: ["lpush_from_"], current: "lpush_val_str", regex: STR},
  {last: ["lpush_from_"], current: "lpush_val_lit", regex: LIT},
  {last: ["lpush_val_gv", "lpush_val_lv", "lpush_val_nb", "lpush_val_str", "lpush_val_lit"], current: "eol", regex: EOL},

  // lpop $arr
  // lpop $arr $val1
  {last: ["fct_ignore_line", "fct_eol", "eol"], current: "lpop", regex: /^lpop /},
  {last: ["lpop"], current: "lpop_from_gv", regex: GLOBAL_VAR},
  {last: ["lpop"], current: "lpop_from_lv", regex: LOCAL_VAR},
  {last: ["lpop_from_gv", "lpop_from_lv"], current: "lpop_from_", regex: SPACE},
  {last: ["lpop_from_"], current: "lpop_val_gv", regex: GLOBAL_VAR},
  {last: ["lpop_from_"], current: "lpop_val_lv", regex: LOCAL_VAR},
  {last: ["lpop_from_"], current: "lpop_val_nb", regex: NB},
  {last: ["lpop_from_"], current: "lpop_val_str", regex: STR},
  {last: ["lpop_from_"], current: "lpop_val_lit", regex: NULL_BOOL},
  {last: ["lpop_from_gv", "lpop_from_lv", "lpop_val_gv", "lpop_val_lv", "lpop_val_nb", "lpop_val_str", "lpop_val_lit"], current: "eol", regex: EOL},

  // ljoin $arr "" = $result
  {last: ["fct_ignore_line", "fct_eol", "eol"], current: "ljoin", regex: /^ljoin /},
  {last: ["ljoin"], current: "ljoin_from_gv", regex: GLOBAL_VAR},
  {last: ["ljoin"], current: "ljoin_from_lv", regex: LOCAL_VAR},
  {last: ["ljoin_from_gv", "ljoin_from_lv"], current: "ljoin_from_", regex: SPACE},
  {last: ["ljoin_from_"], current: "ljoin_val_gv", regex: GLOBAL_VAR},
  {last: ["ljoin_from_"], current: "ljoin_val_lv", regex: LOCAL_VAR},
  {last: ["ljoin_from_"], current: "ljoin_val_str", regex: STR},
  {last: ["ljoin_val_gv", "ljoin_val_lv", "ljoin_val_str"], current: "ljoin_assign", regex: ASSIGN},
  {last: ["ljoin_assign"], current: "ljoin_assign_gv", regex: GLOBAL_VAR},
  {last: ["ljoin_assign"], current: "ljoin_assign_lv", regex: LOCAL_VAR},
  {last: ["ljoin_assign_gv", "ljoin_assign_lv"], current: "eol", regex: EOL},

  // ldel $arr 0
  {last: ["fct_ignore_line", "fct_eol", "eol"], current: "ldel", regex: /^ldel /},
  {last: ["ldel"], current: "ldel_from_gv", regex: GLOBAL_VAR},
  {last: ["ldel"], current: "ldel_from_lv", regex: LOCAL_VAR},
  {last: ["ldel_from_gv", "ldel_from_lv"], current: "ldel_from_", regex: SPACE},
  {last: ["ldel_from_"], current: "ldel_index_gv", regex: GLOBAL_VAR},
  {last: ["ldel_from_"], current: "ldel_index_lv", regex: LOCAL_VAR},
  {last: ["ldel_from_"], current: "ldel_index_int", regex: INT_POSITIVE},
  {last: ["ldel_index_gv", "ldel_index_lv", "ldel_index_int"], current: "eol", regex: EOL},

  // lset $arr 0 "a"
  // lins $arr 0 "a"
  {last: ["fct_ignore_line", "fct_eol", "eol"], current: "lset", regex: /^(lset|lins)/},
  {last: ["lset"], current: "lset_", regex: SPACE},
  {last: ["lset_"], current: "lset_from_gv", regex: GLOBAL_VAR},
  {last: ["lset_"], current: "lset_from_lv", regex: LOCAL_VAR},
  {last: ["lset_from_gv", "lset_from_lv"], current: "lset_from_", regex: SPACE},
  {last: ["lset_from_"], current: "lset_index_gv", regex: GLOBAL_VAR},
  {last: ["lset_from_"], current: "lset_index_lv", regex: LOCAL_VAR},
  {last: ["lset_from_"], current: "lset_index_int", regex: INT_POSITIVE},
  {last: ["lset_index_gv", "lset_index_lv", "lset_index_int"], current: "lset_index_", regex: SPACE},
  {last: ["lset_index_"], current: "lset_val_gv", regex: GLOBAL_VAR},
  {last: ["lset_index_"], current: "lset_val_lv", regex: LOCAL_VAR},
  {last: ["lset_index_"], current: "lset_val_nb", regex: NB},
  {last: ["lset_index_"], current: "lset_val_str", regex: STR},
  {last: ["lset_index_"], current: "lset_val_lit", regex: LIT},
  {last: ["lset_val_gv", "lset_val_lv", "lset_val_nb", "lset_val_str", "lset_val_lit"], current: "eol", regex: EOL},

  // lrev $arr
  {last: ["fct_ignore_line", "fct_eol", "eol"], current: "lrev", regex: /^lrev /},
  {last: ["lrev"], current: "lrev_from_gv", regex: GLOBAL_VAR},
  {last: ["lrev"], current: "lrev_from_lv", regex: LOCAL_VAR},
  {last: ["lrev_from_gv", "lrev_from_lv"], current: "eol", regex: EOL},

  // lsort $arr asc
  {last: ["fct_ignore_line", "fct_eol", "eol"], current: "lsort", regex: /^lsort /},
  {last: ["lsort"], current: "lsort_from_gv", regex: GLOBAL_VAR},
  {last: ["lsort"], current: "lsort_from_lv", regex: LOCAL_VAR},
  {last: ["lsort_from_gv", "lsort_from_lv"], current: "lsort_from_", regex: SPACE},
  {last: ["lsort_from_"], current: "lsort_order", regex: SORT_ORDER},
  {last: ["lsort_order"], current: "eol", regex: EOL},

  // lsortby $arr "name" desc
  {last: ["fct_ignore_line", "fct_eol", "eol"], current: "lsortby", regex: /^lsortby /},
  {last: ["lsortby"], current: "lsortby_from_gv", regex: GLOBAL_VAR},
  {last: ["lsortby"], current: "lsortby_from_lv", regex: LOCAL_VAR},
  {last: ["lsortby_from_gv", "lsortby_from_lv"], current: "lsortby_from_", regex: SPACE},
  {last: ["lsortby_from_"], current: "lsortby_index_gv", regex: GLOBAL_VAR},
  {last: ["lsortby_from_"], current: "lsortby_index_lv", regex: LOCAL_VAR},
  {last: ["lsortby_from_"], current: "lsortby_index_str", regex: STR},
  {last: ["lsortby_from_"], current: "lsortby_index_int", regex: INT_POSITIVE},
  {last: ["lsortby_index_gv", "lsortby_index_lv", "lsortby_index_str", "lsortby_index_int"], current: "lsortby_index_", regex: SPACE},
  {last: ["lsortby_index_"], current: "lsortby_order", regex: SORT_ORDER},
  {last: ["lsortby_order"], current: "eol", regex: EOL},

  // lfind $arr $val1 = $i
  {last: ["fct_ignore_line", "fct_eol", "eol"], current: "lfind", regex: /^lfind /},
  {last: ["lfind"], current: "lfind_from_gv", regex: GLOBAL_VAR},
  {last: ["lfind"], current: "lfind_from_lv", regex: LOCAL_VAR},
  {last: ["lfind_from_gv", "lfind_from_lv"], current: "lfind_from_", regex: SPACE},
  {last: ["lfind_from_"], current: "lfind_val_gv", regex: GLOBAL_VAR},
  {last: ["lfind_from_"], current: "lfind_val_lv", regex: LOCAL_VAR},
  {last: ["lfind_from_"], current: "lfind_val_nb", regex: NB},
  {last: ["lfind_from_"], current: "lfind_val_str", regex: STR},
  {last: ["lfind_from_"], current: "lfind_val_lit", regex: NULL_BOOL},
  {last: ["lfind_val_gv", "lfind_val_lv", "lfind_val_nb", "lfind_val_str", "lfind_val_lit"], current: "lfind_assign", regex: ASSIGN},
  {last: ["lfind_assign"], current: "lfind_assign_gv", regex: GLOBAL_VAR},
  {last: ["lfind_assign"], current: "lfind_assign_lv", regex: LOCAL_VAR},
  {last: ["lfind_assign_gv", "lfind_assign_lv"], current: "eol", regex: EOL},

  // lfindby $arr "name" $val1 = $i
  {last: ["fct_ignore_line", "fct_eol", "eol"], current: "lfindby", regex: /^lfindby /},
  {last: ["lfindby"], current: "lfindby_from_gv", regex: GLOBAL_VAR},
  {last: ["lfindby"], current: "lfindby_from_lv", regex: LOCAL_VAR},
  {last: ["lfindby_from_gv", "lfindby_from_lv"], current: "lfindby_from_", regex: SPACE},
  {last: ["lfindby_from_"], current: "lfindby_index_gv", regex: GLOBAL_VAR},
  {last: ["lfindby_from_"], current: "lfindby_index_lv", regex: LOCAL_VAR},
  {last: ["lfindby_from_"], current: "lfindby_index_str", regex: STR},
  {last: ["lfindby_from_"], current: "lfindby_index_int", regex: INT_POSITIVE},
  {last: ["lfindby_index_gv", "lfindby_index_lv", "lfindby_index_str", "lfindby_index_int"], current: "lfindby_index_", regex: SPACE},
  {last: ["lfindby_index_"], current: "lfindby_val_gv", regex: GLOBAL_VAR},
  {last: ["lfindby_index_"], current: "lfindby_val_lv", regex: LOCAL_VAR},
  {last: ["lfindby_index_"], current: "lfindby_val_nb", regex: NB},
  {last: ["lfindby_index_"], current: "lfindby_val_str", regex: STR},
  {last: ["lfindby_index_"], current: "lfindby_val_lit", regex: NULL_BOOL},
  {last: ["lfindby_val_gv", "lfindby_val_lv", "lfindby_val_nb", "lfindby_val_str", "lfindby_val_lit"], current: "lfindby_assign", regex: ASSIGN},
  {last: ["lfindby_assign"], current: "lfindby_assign_gv", regex: GLOBAL_VAR},
  {last: ["lfindby_assign"], current: "lfindby_assign_lv", regex: LOCAL_VAR},
  {last: ["lfindby_assign_gv", "lfindby_assign_lv"], current: "eol", regex: EOL},

  // date now = $date
  // date 2020 1 1 = $date
  // date 2020 1 1 12 45 0 0 = $date
  {last: ["fct_ignore_line", "fct_eol", "eol"], current: "date", regex: /^date /},
  {last: ["date"], current: "date_now", regex: /^now/},
  {last: ["date"], current: "date_year_nb", regex: INT},
  {last: ["date"], current: "date_year_gv", regex: GLOBAL_VAR},
  {last: ["date"], current: "date_year_lv", regex: LOCAL_VAR},
  {last: ["date_year_nb", "date_year_gv", "date_year_lv"], current: "date_year_", regex: SPACE},
  {last: ["date_year_"], current: "date_month_nb", regex: /^(1[1-2]|[1-9])/},
  {last: ["date_year_"], current: "date_month_gv", regex: GLOBAL_VAR},
  {last: ["date_year_"], current: "date_month_lv", regex: LOCAL_VAR},
  {last: ["date_month_nb", "date_month_gv", "date_month_lv"], current: "date_month_", regex: SPACE},
  {last: ["date_month_"], current: "date_day_nb", regex: /^([1-2][0-9]|3[0-1]|[1-9])/},
  {last: ["date_month_"], current: "date_day_gv", regex: GLOBAL_VAR},
  {last: ["date_month_"], current: "date_day_lv", regex: LOCAL_VAR},
  {last: ["date_day_nb", "date_day_gv", "date_day_lv"], current: "date_day_", regex: SPACE},
  {last: ["date_day_"], current: "date_hour_nb", regex: /^(2[0-3]|1?[0-9])/},
  {last: ["date_day_"], current: "date_hour_gv", regex: GLOBAL_VAR},
  {last: ["date_day_"], current: "date_hour_lv", regex: LOCAL_VAR},
  {last: ["date_hour_nb", "date_hour_gv", "date_hour_lv"], current: "date_hour_", regex: SPACE},
  {last: ["date_hour_"], current: "date_min_nb", regex: /^[1-5]?[0-9]/},
  {last: ["date_hour_"], current: "date_min_gv", regex: GLOBAL_VAR},
  {last: ["date_hour_"], current: "date_min_lv", regex: LOCAL_VAR},
  {last: ["date_min_nb", "date_min_gv", "date_min_lv"], current: "date_min_", regex: SPACE},
  {last: ["date_min_"], current: "date_sec_nb", regex: /^[1-5]?[0-9]/},
  {last: ["date_min_"], current: "date_sec_gv", regex: GLOBAL_VAR},
  {last: ["date_min_"], current: "date_sec_lv", regex: LOCAL_VAR},
  {last: ["date_sec_nb", "date_sec_gv", "date_sec_lv"], current: "date_sec_", regex: SPACE},
  {last: ["date_sec_"], current: "date_ms_nb", regex: INT_POSITIVE},
  {last: ["date_sec_"], current: "date_ms_gv", regex: GLOBAL_VAR},
  {last: ["date_sec_"], current: "date_ms_lv", regex: LOCAL_VAR},
  {last: ["date_now", "date_day_nb", "date_day_gv", "date_day_lv", "date_ms_nb", "date_ms_gv", "date_ms_lv"], current: "date_assign", regex: ASSIGN},
  {last: ["date_assign"], current: "date_assign_gv", regex: GLOBAL_VAR},
  {last: ["date_assign"], current: "date_assign_lv", regex: LOCAL_VAR},
  {last: ["date_assign_gv", "date_assign_lv"], current: "eol", regex: EOL},

  // dset $date year|month|day|hour|min|sec|ms 2000
  // dadd $date year|month|day|hour|min|sec|ms -5
  {last: ["fct_ignore_line", "fct_eol", "eol"], current: "dset", regex: /^(dset|dadd)/},
  {last: ["dset"], current: "dset_", regex: SPACE},
  {last: ["dset_"], current: "dset_from_gv", regex: GLOBAL_VAR},
  {last: ["dset_"], current: "dset_from_lv", regex: LOCAL_VAR},
  {last: ["dset_from_gv", "dset_from_lv"], current: "dset_from_", regex: SPACE},
  {last: ["dset_from_"], current: "dset_field", regex: TIME_UNIT},
  {last: ["dset_field"], current: "dset_field_", regex: SPACE},
  {last: ["dset_field_"], current: "dset_val_gv", regex: GLOBAL_VAR},
  {last: ["dset_field_"], current: "dset_val_lv", regex: LOCAL_VAR},
  {last: ["dset_field_"], current: "dset_val_nb", regex: INT},
  {last: ["dset_val_gv", "dset_val_lv", "dset_val_nb"], current: "eol", regex: EOL},

  // dget $date year|month|day|hour|min|sec|ms = $result
  {last: ["fct_ignore_line", "fct_eol", "eol"], current: "dget", regex: /^dget /},
  {last: ["dget"], current: "dget_from_gv", regex: GLOBAL_VAR},
  {last: ["dget"], current: "dget_from_lv", regex: LOCAL_VAR},
  {last: ["dget_from_gv", "dget_from_lv"], current: "dget_from_", regex: SPACE},
  {last: ["dget_from_"], current: "dget_field", regex: TIME_UNIT},
  {last: ["dget_field"], current: "dget_assign", regex: ASSIGN},
  {last: ["dget_assign"], current: "dget_assign_gv", regex: GLOBAL_VAR},
  {last: ["dget_assign"], current: "dget_assign_lv", regex: LOCAL_VAR},
  {last: ["dget_assign_gv", "dget_assign_lv"], current: "eol", regex: EOL},

  // dsub $date1 $date2 = $date3
  {last: ["fct_ignore_line", "fct_eol", "eol"], current: "dsub", regex: /^dsub /},
  {last: ["dsub"], current: "dsub_from_gv", regex: GLOBAL_VAR},
  {last: ["dsub"], current: "dsub_from_lv", regex: LOCAL_VAR},
  {last: ["dsub_from_gv", "dsub_from_lv"], current: "dsub_from_", regex: SPACE},
  {last: ["dsub_from_"], current: "dsub_val_gv", regex: GLOBAL_VAR},
  {last: ["dsub_from_"], current: "dsub_val_lv", regex: LOCAL_VAR},
  {last: ["dsub_val_gv", "dsub_val_lv"], current: "dsub_assign", regex: ASSIGN},
  {last: ["dsub_assign"], current: "dsub_assign_gv", regex: GLOBAL_VAR},
  {last: ["dsub_assign"], current: "dsub_assign_lv", regex: LOCAL_VAR},
  {last: ["dsub_assign_gv", "dsub_assign_lv"], current: "eol", regex: EOL},

  // end
  {
    last: [
      "*", "ignore_line", "eol",
      "fct", "fct2", "fct_param_lv", "fct_ignore_line", "fct_eol",
      "get_assign_gv", "get_assign_lv", "has_assign_gv", "has_assign_lv",
      "set_val_gv", "set_val_lv", "set_val_nb", "set_val_str", "set_val_lit",
      "fopen_assign_gv", "fopen_assign_lv",
      "fwrite_val_gv", "fwrite_val_lv", "fwrite_val_nb", "fwrite_val_str",
      "panic_msg_gv", "panic_msg_lv", "panic_msg_str", "panic_line_gv",
      "panic_line_lv", "panic_col_gv", "panic_col_lv",
      "math_assign_gv", "math_assign_lv", "math2_assign_gv", "math2_assign_lv",
      "atan2_assign_gv", "atan2_assign_lv", "hypot_assign_gv", "hypot_assign_lv",
      "round_assign_gv", "round_assign_lv",
      "nbcmp_assign_gv", "nbcmp_assign_lv", "eq_assign_gv", "eq_assign_lv",
      "xor_assign_gv", "xor_assign_lv", "or_assign_gv", "or_assign_lv",
      "shift_assign_gv", "shift_assign_lv", "rand_assign_gv", "rand_assign_lv",
      "not_assign_gv", "not_assign_lv", "nan_assign_gv", "nan_assign_lv",
      "len_assign_gv", "len_assign_lv", "cp_assign_gv", "cp_assign_lv",
      "convert_assign_gv", "convert_assign_lv", "json_assign_gv", "json_assign_lv",
      "loop_fct", "loop_param_gv", "loop_param_lv", "loop_param_nb", "loop_param_str",
      "loop_param_lit", "loop_assign_gv", "loop_assign_lv",
      "switch_cmp_fct", "if_continue", "if_fct", "if_assign_gv", "if_assign_lv",
      "if_break", "if_break_gv", "if_break_lv", "if_break_nb", "if_break_str", "if_break_lit",
      "if_return", "if_return_gv", "if_return_lv", "if_return_nb", "if_return_str", "if_return_lit",
      "if_param_gv", "if_param_lv", "if_param_nb", "if_param_str", "if_param_lit",
      "call_fct", "call_param_gv", "call_param_lv", "call_param_nb",
      "call_param_str", "call_param_lit", "call_assign_gv", "call_assign_lv",
      "return_gv", "return_lv", "return_nb", "return_str", "return_lit",
      "getch_assign_gv", "getch_assign_lv", "concat_assign_gv", "concat_assign_lv",
      "mset_val_gv", "mset_val_lv", "mset_val_nb", "mset_val_str", "mset_val_lit",
      "split_assign_gv", "split_assign_lv", "replace_assign_gv", "replace_assign_lv",
      "pad_assign_gv", "pad_assign_lv",
      "linit_param_gv", "linit_param_lv", "linit_param_nb", "linit_param_str", "linit_param_lit",
      "lfill_val_gv", "lfill_val_lv", "lfill_val_nb", "lfill_val_str", "lfill_val_lit",
      "lfill_range_step_int", "lfill_range_step_gv", "lfill_range_step_lv",
      "lcat_assign_gv", "lcat_assign_lv", "slice_assign_gv", "slice_assign_lv",
      "lpush_val_gv", "lpush_val_lv", "lpush_val_nb", "lpush_val_str", "lpush_val_lit",
      "lpop_from_gv", "lpop_from_lv", "lpop_val_gv", "lpop_val_lv",
      "lpop_val_nb", "lpop_val_str", "lpop_val_lit",
      "ljoin_assign_gv", "ljoin_assign_lv",
      "ldel_index_gv", "ldel_index_lv", "ldel_index_int",
      "lset_val_gv", "lset_val_lv", "lset_val_nb", "lset_val_str", "lset_val_lit",
      "lrev_from_gv", "lrev_from_lv", "lsort_order", "lsortby_order",
      "lfind_assign_gv", "lfind_assign_lv", "lfindby_assign_gv", "lfindby_assign_lv",
      "date_assign_gv", "date_assign_lv", "dset_val_gv", "dset_val_lv", "dset_val_nb",
      "dget_assign_gv", "dget_assign_lv", "dsub_assign_gv", "dsub_assign_lv",
    ], current: "$"
  } as any,
]);

/** Building instructions for generator source code. */
export const GENERATOR_BUILDER = PARSER.parseSrcBuilder(`
fct "fct":map[]+ "name":str= "op":[] "param":[] "offset":offset "line":line "col":col
fct2 - "fct":map[]+ "name":str= "op":[] "param":[] "offset":offset "line":line "col":col
fct_param_lv "param":str[]=
eol -

getch "op":map[]+ "name":"getch" "offset":offset "line":line "col":col
getch_from_gv "from":map+ "type":"gv" "value":str= -
getch_from_lv "from":map+ "type":"lv" "value":str= -
getch_from_str "from":map+ "type":"str" "value":lstr= -
getch_index_gv "index":map+ "type":"gv" "value":str= -
getch_index_lv "index":map+ "type":"lv" "value":str= -
getch_index_int "index":map+ "type":"nb" "value":nb= -
getch_assign_gv "assign":map+ "type":"gv" "value":str= -
getch_assign_lv "assign":map+ "type":"lv" "value":str= -

get "op":map[]+ "name":"get" "offset":offset "line":line "col":col
get_from_gv "from":map+ "type":"gv" "value":str= -
get_from_lv "from":map+ "type":"lv" "value":str= -
get_index_gv "index":map+ "type":"gv" "value":str= -
get_index_lv "index":map+ "type":"lv" "value":str= -
get_index_int "index":map+ "type":"nb" "value":nb= -
get_index_str "index":map+ "type":"str" "value":lstr= -
get_assign_gv "assign":map+ "type":"gv" "value":str= -
get_assign_lv "assign":map+ "type":"lv" "value":str= -

has "op":map[]+ "name":"has" "offset":offset "line":line "col":col
has_from_gv "from":map+ "type":"gv" "value":str= -
has_from_lv "from":map+ "type":"lv" "value":str= -
has_index_gv "index":map+ "type":"gv" "value":str= -
has_index_lv "index":map+ "type":"lv" "value":str= -
has_index_int "index":map+ "type":"nb" "value":nb= -
has_index_str "index":map+ "type":"str" "value":lstr= -
has_assign_gv "assign":map+ "type":"gv" "value":str= -
has_assign_lv "assign":map+ "type":"lv" "value":str= -

set "op":map[]+ "name":"set" "offset":offset "line":line "col":col
set_to_gv "to":map+ "type":"gv" "value":str= -
set_to_lv "to":map+ "type":"lv" "value":str= -
set_val_gv "val":map+ "type":"gv" "value":str= -
set_val_lv "val":map+ "type":"lv" "value":str= -
set_val_nb "val":map+ "type":"nb" "value":nb= -
set_val_str "val":map+ "type":"str" "value":lstr= -
set_val_lit "val":map+ "type":"lit" "value":str= -

fopen "op":map[]+ "name":"fopen" "offset":offset "line":line "col":col
fopen_filename_gv "filename":map+ "type":"gv" "value":str= -
fopen_filename_lv "filename":map+ "type":"lv" "value":str= -
fopen_filename_str "filename":map+ "type":"str" "value":lstr= -
fopen_type_gv "type":map+ "type":"gv" "value":str= -
fopen_type_lv "type":map+ "type":"lv" "value":str= -
fopen_type_str "type":map+ "type":"str" "value":lstr= -
fopen_assign_gv "assign":map+ "type":"gv" "value":str= -
fopen_assign_lv "assign":map+ "type":"lv" "value":str= -

fwrite "op":map[]+ "name":"fwrite" "offset":offset "line":line "col":col
fwrite_to_gv "to":map+ "type":"gv" "value":str= -
fwrite_to_lv "to":map+ "type":"lv" "value":str= -
fwrite_val_gv "val":map+ "type":"gv" "value":str= -
fwrite_val_lv "val":map+ "type":"lv" "value":str= -
fwrite_val_str "val":map+ "type":"str" "value":lstr= -
fwrite_val_nb "val":map+ "type":"nb" "value":nb= -

panic "op":map[]+ "name":"panic" "offset":offset "line":line "col":col
panic_msg_gv "msg":map+ "type":"gv" "value":str= -
panic_msg_lv "msg":map+ "type":"lv" "value":str= -
panic_msg_str "msg":map+ "type":"str" "value":lstr= -
panic_line_gv "src_line":map+ "type":"gv" "value":str= -
panic_line_lv "src_line":map+ "type":"lv" "value":str= -
panic_col_gv "src_col":map+ "type":"gv" "value":str= -
panic_col_lv "src_col":map+ "type":"lv" "value":str= -

math2 "op":map[]+ "name":str= "offset":offset "line":line "col":col
math2_val_gv "val":map[]+ "type":"gv" "value":str= -
math2_val_lv "val":map[]+ "type":"lv" "value":str= -
math2_val_nb "val":map[]+ "type":"nb" "value":nb= -
math2_assign_gv "assign":map+ "type":"gv" "value":str= -
math2_assign_lv "assign":map+ "type":"lv" "value":str= -

atan2 "op":map[]+ "name":str= "offset":offset "line":line "col":col
atan2_val1_gv "val1":map+ "type":"gv" "value":str= -
atan2_val1_lv "val1":map+ "type":"lv" "value":str= -
atan2_val1_nb "val1":map+ "type":"nb" "value":nb= -
atan2_val2_gv "val2":map+ "type":"gv" "value":str= -
atan2_val2_lv "val2":map+ "type":"lv" "value":str= -
atan2_val2_nb "val2":map+ "type":"nb" "value":nb= -
atan2_assign_gv "assign":map+ "type":"gv" "value":str= -
atan2_assign_lv "assign":map+ "type":"lv" "value":str= -

math "op":map[]+ "name":str= "offset":offset "line":line "col":col
math_val_gv "val":map+ "type":"gv" "value":str= -
math_val_lv "val":map+ "type":"lv" "value":str= -
math_val_nb "val":map+ "type":"nb" "value":nb= -
math_assign_gv "assign":map+ "type":"gv" "value":str= -
math_assign_lv "assign":map+ "type":"lv" "value":str= -

round "op":map[]+ "name":"round" "offset":offset "line":line "col":col
round_val_gv "val":map+ "type":"gv" "value":str= -
round_val_lv "val":map+ "type":"lv" "value":str= -
round_val_nb "val":map+ "type":"nb" "value":nb= -
round_amount_gv "amount":map+ "type":"gv" "value":str= -
round_amount_lv "amount":map+ "type":"lv" "value":str= -
round_amount_int "amount":map+ "type":"nb" "value":nb= -
round_assign_gv "assign":map+ "type":"gv" "value":str= -
round_assign_lv "assign":map+ "type":"lv" "value":str= -

hypot "op":map[]+ "name":"hypot" "offset":offset "line":line "col":col
hypot_val_gv "val":map[]+ "type":"gv" "value":str= -
hypot_val_lv "val":map[]+ "type":"lv" "value":str= -
hypot_val_nb "val":map[]+ "type":"nb" "value":nb= -
hypot_assign_gv "assign":map+ "type":"gv" "value":str= -
hypot_assign_lv "assign":map+ "type":"lv" "value":str= -

nbcmp "op":map[]+ "name":str= "offset":offset "line":line "col":col
nbcmp_val_gv "val":map[]+ "type":"gv" "value":str= -
nbcmp_val_lv "val":map[]+ "type":"lv" "value":str= -
nbcmp_val_nb "val":map[]+ "type":"nb" "value":nb= -
nbcmp_assign_gv "assign":map+ "type":"gv" "value":str= -
nbcmp_assign_lv "assign":map+ "type":"lv" "value":str= -

eq "op":map[]+ "name":str= "offset":offset "line":line "col":col
eq_val_gv "val":map[]+ "type":"gv" "value":str= -
eq_val_lv "val":map[]+ "type":"lv" "value":str= -
eq_val_nb "val":map[]+ "type":"nb" "value":nb= -
eq_val_str "val":map[]+ "type":"str" "value":lstr= -
eq_val_lit "val":map[]+ "type":"lit" "value":str= -
eq_assign_gv "assign":map+ "type":"gv" "value":str= -
eq_assign_lv "assign":map+ "type":"lv" "value":str= -

xor "op":map[]+ "name":"xor" "offset":offset "line":line "col":col
xor_val_gv "val":map[]+ "type":"gv" "value":str= -
xor_val_lv "val":map[]+ "type":"lv" "value":str= -
xor_val_int "val":map[]+ "type":"nb" "value":nb= -
xor_assign_gv "assign":map+ "type":"gv" "value":str= -
xor_assign_lv "assign":map+ "type":"lv" "value":str= -

or "op":map[]+ "name":str= "offset":offset "line":line "col":col
or_val_gv "val":map[]+ "type":"gv" "value":str= -
or_val_lv "val":map[]+ "type":"lv" "value":str= -
or_val_int "val":map[]+ "type":"nb" "value":nb= -
or_val_bool "val":map[]+ "type":"bool" "value":str= -
or_assign_gv "assign":map+ "type":"gv" "value":str= -
or_assign_lv "assign":map+ "type":"lv" "value":str= -

shift "op":map[]+ "name":str= "offset":offset "line":line "col":col
shift_from_gv "from":map+ "type":"gv" "value":str= -
shift_from_lv "from":map+ "type":"lv" "value":str= -
shift_from_int "from":map+ "type":"nb" "value":nb= -
shift_index_gv "index":map+ "type":"gv" "value":str= -
shift_index_lv "index":map+ "type":"lv" "value":str= -
shift_index_int "index":map+ "type":"nb" "value":nb= -
shift_assign_gv "assign":map+ "type":"gv" "value":str= -
shift_assign_lv "assign":map+ "type":"lv" "value":str= -

not "op":map[]+ "name":"not" "offset":offset "line":line "col":col
not_from_gv "from":map+ "type":"gv" "value":str= -
not_from_lv "from":map+ "type":"lv" "value":str= -
not_from_int "from":map+ "type":"nb" "value":nb= -
not_assign_gv "assign":map+ "type":"gv" "value":str= -
not_assign_lv "assign":map+ "type":"lv" "value":str= -

nan "op":map[]+ "name":"nan" "offset":offset "line":line "col":col
nan_from_gv "from":map+ "type":"gv" "value":str= -
nan_from_lv "from":map+ "type":"lv" "value":str= -
nan_assign_gv "assign":map+ "type":"gv" "value":str= -
nan_assign_lv "assign":map+ "type":"lv" "value":str= -

len "op":map[]+ "name":"len" "offset":offset "line":line "col":col
len_from_gv "from":map+ "type":"gv" "value":str= -
len_from_lv "from":map+ "type":"lv" "value":str= -
len_assign_gv "assign":map+ "type":"gv" "value":str= -
len_assign_lv "assign":map+ "type":"lv" "value":str= -

cp "op":map[]+ "name":str= "offset":offset "line":line "col":col
cp_from_gv "from":map+ "type":"gv" "value":str= -
cp_from_lv "from":map+ "type":"lv" "value":str= -
cp_assign_gv "assign":map+ "type":"gv" "value":str= -
cp_assign_lv "assign":map+ "type":"lv" "value":str= -

convert "op":map[]+ "name":str= "offset":offset "line":line "col":col
convert_from_gv "from":map+ "type":"gv" "value":str= -
convert_from_lv "from":map+ "type":"lv" "value":str= -
convert_assign_gv "assign":map+ "type":"gv" "value":str= -
convert_assign_lv "assign":map+ "type":"lv" "value":str= -

json "op":map[]+ "name":"_json" "indent":null "offset":offset "line":line "col":col
json_from_gv "from":map+ "type":"gv" "value":str= -
json_from_lv "from":map+ "type":"lv" "value":str= -
json_indent_int "indent":map+ "type":"nb" "value":nb= -
json_indent_tab "indent":map+ "type":"str" "value":lstr= -
json_assign_gv "assign":map+ "type":"gv" "value":str= -
json_assign_lv "assign":map+ "type":"lv" "value":str= -

rand "op":map[]+ "name":"rand" "offset":offset "line":line "col":col
rand_min_gv "min":map+ "type":"gv" "value":str= -
rand_min_lv "min":map+ "type":"lv" "value":str= -
rand_min_int "min":map+ "type":"nb" "value":nb= -
rand_max_gv "max":map+ "type":"gv" "value":str= -
rand_max_lv "max":map+ "type":"lv" "value":str= -
rand_max_int "max":map+ "type":"nb" "value":nb= -
rand_assign_gv "assign":map+ "type":"gv" "value":str= -
rand_assign_lv "assign":map+ "type":"lv" "value":str= -

loop "op":map[]+ "name":"loop" "param":[] "offset":offset "line":line "col":col
loop_from_gv "from":map+ "type":"gv" "value":str= -
loop_from_lv "from":map+ "type":"lv" "value":str= -
loop_from_range "from":map+ "type":"range"
loop_from_range_inc "inclusive":true
loop_from_range_exc "inclusive":false
loop_from_range_min_int "min":map+ "type":"nb" "value":nb= -
loop_from_range_min_gv "min":map+ "type":"gv" "value":str= -
loop_from_range_min_lv "min":map+ "type":"lv" "value":str= -
loop_from_range_max_int "max":map+ "type":"nb" "value":nb= -
loop_from_range_max_gv "max":map+ "type":"gv" "value":str= -
loop_from_range_max_lv "max":map+ "type":"lv" "value":str= -
loop_from_range_step_int "step":map+ "type":"nb" "value":nb= --
loop_from_range_step_gv "step":map+ "type":"gv" "value":str= --
loop_from_range_step_lv "step":map+ "type":"lv" "value":str= --
loop_fct "fct":str=
loop_param_gv "param":map[]+ "type":"gv" "value":str= -
loop_param_lv "param":map[]+ "type":"lv" "value":str= -
loop_param_nb "param":map[]+ "type":"nb" "value":nb= -
loop_param_str "param":map[]+ "type":"str" "value":lstr= -
loop_param_lit "param":map[]+ "type":"lit" "value":str= -
loop_assign_gv "assign":map+ "type":"gv" "value":str= -
loop_assign_lv "assign":map+ "type":"lv" "value":str= -

switch "op":map[]+ "name":"switch" "offset":offset "line":line "col":col
switch_val_gv "val":map+ "type":"gv" "value":str= -
switch_val_lv "val":map+ "type":"lv" "value":str= -
switch_cmp_nb "cmp":map[]+ "value":map+ "type":"nb" "value":nb= -
switch_cmp_str "cmp":map[]+ "value":map+ "type":"str" "value":lstr= -
switch_cmp_lit "cmp":map[]+ "value":map+ "type":"lit" "value":str= -
switch_cmp_fct "fct":str= -

if "op":map[]+ "name":"if" "param":[] "offset":offset "line":line "col":col
if_from_gv "from":map+ "type":"gv" "value":str= -
if_from_lv "from":map+ "type":"lv" "value":str= -
if_continue "action":"continue"
if_break "action":"break"
if_break_gv "return":map+ "type":"gv" "value":str= -
if_break_lv "return":map+ "type":"lv" "value":str= -
if_break_nb "return":map+ "type":"nb" "value":nb= -
if_break_str "return":map+ "type":"str" "value":lstr= -
if_break_lit "return":map+ "type":"lit" "value":str= -
if_return "action":"return"
if_return_gv "return":map+ "type":"gv" "value":str= -
if_return_lv "return":map+ "type":"lv" "value":str= -
if_return_nb "return":map+ "type":"nb" "value":nb= -
if_return_str "return":map+ "type":"str" "value":lstr= -
if_return_lit "return":map+ "type":"lit" "value":str= -
if_fct "action":"call" "fct":str=
if_param_gv "param":map[]+ "type":"gv" "value":str= -
if_param_lv "param":map[]+ "type":"lv" "value":str= -
if_param_nb "param":map[]+ "type":"nb" "value":nb= -
if_param_str "param":map[]+ "type":"str" "value":lstr= -
if_param_lit "param":map[]+ "type":"lit" "value":str= -
if_assign_gv "assign":map+ "type":"gv" "value":str= -
if_assign_lv "assign":map+ "type":"lv" "value":str= -

call "op":map[]+ "name":"call" "param":[] "offset":offset "line":line "col":col
call_fct "fct":str=
call_param_gv "param":map[]+ "type":"gv" "value":str= -
call_param_lv "param":map[]+ "type":"lv" "value":str= -
call_param_nb "param":map[]+ "type":"nb" "value":nb= -
call_param_str "param":map[]+ "type":"str" "value":lstr= -
call_param_lit "param":map[]+ "type":"lit" "value":str= -
call_assign_gv "assign":map+ "type":"gv" "value":str= -
call_assign_lv "assign":map+ "type":"lv" "value":str= -

return "op":map[]+ "name":"return" "offset":offset "line":line "col":col
return_gv "val":map+ "type":"gv" "value":str= -
return_lv "val":map+ "type":"lv" "value":str= -
return_nb "val":map+ "type":"nb" "value":nb= -
return_str "val":map+ "type":"str" "value":lstr= -
return_lit "val":map+ "type":"lit" "value":str= -

concat "op":map[]+ "name":"concat" "offset":offset "line":line "col":col
concat_param_gv "param":map[]+ "type":"gv" "value":str= -
concat_param_lv "param":map[]+ "type":"lv" "value":str= -
concat_param_nb "param":map[]+ "type":"nb" "value":nb= -
concat_param_str "param":map[]+ "type":"str" "value":lstr= -
concat_assign_gv "assign":map+ "type":"gv" "value":str= -
concat_assign_lv "assign":map+ "type":"lv" "value":str= -

mset "op":map[]+ "name":"mset" "offset":offset "line":line "col":col
mset_from_gv "from":map+ "type":"gv" "value":str= -
mset_from_lv "from":map+ "type":"lv" "value":str= -
mset_field "fields":map[]+ "name":lstr= "value":map+
mset_val_gv "type":"gv" "value":str= --
mset_val_lv "type":"lv" "value":str= --
mset_val_str "type":"str" "value":lstr= --
mset_val_nb "type":"nb" "value":nb= --
mset_val_lit "type":"lit" "value":str= --

split "op":map[]+ "name":str= "offset":offset "line":line "col":col
split_from_gv "from":map+ "type":"gv" "value":str= -
split_from_lv "from":map+ "type":"lv" "value":str= -
split_from_str "from":map+ "type":"str" "value":lstr= -
split_regex_gv "regex":map+ "type":"gv" "value":str= -
split_regex_lv "regex":map+ "type":"lv" "value":str= -
split_regex_str "regex":map+ "type":"str" "value":lstr= -
split_assign_gv "assign":map+ "type":"gv" "value":str= -
split_assign_lv "assign":map+ "type":"lv" "value":str= -

replace "op":map[]+ "name":"replace" "offset":offset "line":line "col":col
replace_from_gv "from":map+ "type":"gv" "value":str= -
replace_from_lv "from":map+ "type":"lv" "value":str= -
replace_from_str "from":map+ "type":"str" "value":lstr= -
replace_regex_gv "regex":map+ "type":"gv" "value":str= -
replace_regex_lv "regex":map+ "type":"lv" "value":str= -
replace_regex_str "regex":map+ "type":"str" "value":lstr= -
replace_to_gv "to":map+ "type":"gv" "value":str= -
replace_to_lv "to":map+ "type":"lv" "value":str= -
replace_to_str "to":map+ "type":"str" "value":lstr= -
replace_assign_gv "assign":map+ "type":"gv" "value":str= -
replace_assign_lv "assign":map+ "type":"lv" "value":str= -

pad "op":map[]+ "name":str= "offset":offset "line":line "col":col
pad_from_gv "from":map+ "type":"gv" "value":str= -
pad_from_lv "from":map+ "type":"lv" "value":str= -
pad_from_nb "from":map+ "type":"nb" "value":nb= -
pad_from_str "from":map+ "type":"str" "value":lstr= -
pad_amount_gv "amount":map+ "type":"gv" "value":str= -
pad_amount_lv "amount":map+ "type":"lv" "value":str= -
pad_amount_int "amount":map+ "type":"nb" "value":nb= -
pad_val_gv "val":map+ "type":"gv" "value":str= -
pad_val_lv "val":map+ "type":"lv" "value":str= -
pad_val_nb "val":map+ "type":"nb" "value":nb= -
pad_val_str "val":map+ "type":"str" "value":lstr= -
pad_assign_gv "assign":map+ "type":"gv" "value":str= -
pad_assign_lv "assign":map+ "type":"lv" "value":str= -

slice "op":map[]+ "name":"slice" "offset":offset "line":line "col":col
slice_from_gv "from":map+ "type":"gv" "value":str= -
slice_from_lv "from":map+ "type":"lv" "value":str= -
slice_from_str "from":map+ "type":"str" "value":lstr= -
slice_min_gv "min":map+ "type":"gv" "value":str= -
slice_min_lv "min":map+ "type":"lv" "value":str= -
slice_min_nb "min":map+ "type":"nb" "value":nb= -
slice_max_gv "max":map+ "type":"gv" "value":str= -
slice_max_lv "max":map+ "type":"lv" "value":str= -
slice_max_nb "max":map+ "type":"nb" "value":nb= -
slice_assign_gv "assign":map+ "type":"gv" "value":str= -
slice_assign_lv "assign":map+ "type":"lv" "value":str= -

linit "op":map[]+ "name":"linit" "offset":offset "line":line "col":col
linit_from_gv "from":map+ "type":"gv" "value":str= -
linit_from_lv "from":map+ "type":"lv" "value":str= -
linit_param_gv "param":map[]+ "type":"gv" "value":str= -
linit_param_lv "param":map[]+ "type":"lv" "value":str= -
linit_param_nb "param":map[]+ "type":"nb" "value":nb= -
linit_param_str "param":map[]+ "type":"str" "value":lstr= -
linit_param_lit "param":map[]+ "type":"lit" "value":str= -

lfill "op":map[]+ "name":"lfill" "offset":offset "line":line "col":col
lfill_from_gv "from":map+ "type":"gv" "value":str= -
lfill_from_lv "from":map+ "type":"lv" "value":str= -
lfill_amount_gv "amount":map+ "type":"gv" "value":str= -
lfill_amount_lv "amount":map+ "type":"lv" "value":str= -
lfill_amount_int "amount":map+ "type":"nb" "value":nb= -
lfill_range "amount":map+ "type":"range"
lfill_range_inc "inclusive":true
lfill_range_exc "inclusive":false
lfill_range_min_int "min":map+ "type":"nb" "value":nb= -
lfill_range_min_gv "min":map+ "type":"gv" "value":str= -
lfill_range_min_lv "min":map+ "type":"lv" "value":str= -
lfill_range_max_int "max":map+ "type":"nb" "value":nb= -
lfill_range_max_gv "max":map+ "type":"gv" "value":str= -
lfill_range_max_lv "max":map+ "type":"lv" "value":str= -
lfill_range_step_int "step":map+ "type":"nb" "value":nb= --
lfill_range_step_gv "step":map+ "type":"gv" "value":str= --
lfill_range_step_lv "step":map+ "type":"lv" "value":str= --
lfill_val_gv "val":map+ "type":"gv" "value":str= -
lfill_val_lv "val":map+ "type":"lv" "value":str= -
lfill_val_nb "val":map+ "type":"nb" "value":nb= -
lfill_val_str "val":map+ "type":"str" "value":lstr= -
lfill_val_lit "val":map+ "type":"lit" "value":str= -

lcat "op":map[]+ "name":"lcat" "offset":offset "line":line "col":col
lcat_param_gv "param":map[]+ "type":"gv" "value":str= -
lcat_param_lv "param":map[]+ "type":"lv" "value":str= -
lcat_assign_gv "assign":map+ "type":"gv" "value":str= -
lcat_assign_lv "assign":map+ "type":"lv" "value":str= -

lpush "op":map[]+ "name":"lpush" "offset":offset "line":line "col":col
lpush_from_gv "from":map+ "type":"gv" "value":str= -
lpush_from_lv "from":map+ "type":"lv" "value":str= -
lpush_val_gv "val":map+ "type":"gv" "value":str= -
lpush_val_lv "val":map+ "type":"lv" "value":str= -
lpush_val_nb "val":map+ "type":"nb" "value":nb= -
lpush_val_str "val":map+ "type":"str" "value":lstr= -
lpush_val_lit "val":map+ "type":"lit" "value":str= -

lpop "op":map[]+ "name":"lpop" "offset":offset "line":line "col":col
lpop_from_gv "from":map+ "type":"gv" "value":str= -
lpop_from_lv "from":map+ "type":"lv" "value":str= -
lpop_val_gv "val":map+ "type":"gv" "value":str= -
lpop_val_lv "val":map+ "type":"lv" "value":str= -
lpop_val_nb "val":map+ "type":"nb" "value":nb= -
lpop_val_str "val":map+ "type":"str" "value":lstr= -
lpop_val_lit "val":map+ "type":"lit" "value":str= -

ljoin "op":map[]+ "name":"ljoin" "offset":offset "line":line "col":col
ljoin_from_gv "from":map+ "type":"gv" "value":str= -
ljoin_from_lv "from":map+ "type":"lv" "value":str= -
ljoin_val_gv "val":map+ "type":"gv" "value":str= -
ljoin_val_lv "val":map+ "type":"lv" "value":str= -
ljoin_val_str "val":map+ "type":"str" "value":lstr= -
ljoin_assign_gv "assign":map+ "type":"gv" "value":str= -
ljoin_assign_lv "assign":map+ "type":"lv" "value":str= -

ldel "op":map[]+ "name":"ldel" "offset":offset "line":line "col":col
ldel_from_gv "from":map+ "type":"gv" "value":str= -
ldel_from_lv "from":map+ "type":"lv" "value":str= -
ldel_index_gv "index":map+ "type":"gv" "value":str= -
ldel_index_lv "index":map+ "type":"lv" "value":str= -
ldel_index_int "index":map+ "type":"nb" "value":nb= -

lset "op":map[]+ "name":str= "offset":offset "line":line "col":col
lset_from_gv "from":map+ "type":"gv" "value":str= -
lset_from_lv "from":map+ "type":"lv" "value":str= -
lset_index_gv "index":map+ "type":"gv" "value":str= -
lset_index_lv "index":map+ "type":"lv" "value":str= -
lset_index_int "index":map+ "type":"nb" "value":nb= -
lset_val_gv "val":map+ "type":"gv" "value":str= -
lset_val_lv "val":map+ "type":"lv" "value":str= -
lset_val_nb "val":map+ "type":"nb" "value":nb= -
lset_val_str "val":map+ "type":"str" "value":lstr= -
lset_val_lit "val":map+ "type":"lit" "value":str= -

lrev "op":map[]+ "name":"lrev" "offset":offset "line":line "col":col
lrev_from_gv "from":map+ "type":"gv" "value":str= -
lrev_from_lv "from":map+ "type":"lv" "value":str= -

lsort "op":map[]+ "name":"lsort" "offset":offset "line":line "col":col
lsort_from_gv "from":map+ "type":"gv" "value":str= -
lsort_from_lv "from":map+ "type":"lv" "value":str= -
lsort_order "order":str=

lsortby "op":map[]+ "name":"lsortby" "offset":offset "line":line "col":col
lsortby_from_gv "from":map+ "type":"gv" "value":str= -
lsortby_from_lv "from":map+ "type":"lv" "value":str= -
lsortby_order "order":str=
lsortby_index_gv "index":map+ "type":"gv" "value":str= -
lsortby_index_lv "index":map+ "type":"lv" "value":str= -
lsortby_index_int "index":map+ "type":"nb" "value":nb= -
lsortby_index_str "index":map+ "type":"str" "value":lstr= -

lfind "op":map[]+ "name":"lfind" "offset":offset "line":line "col":col
lfind_from_gv "from":map+ "type":"gv" "value":str= -
lfind_from_lv "from":map+ "type":"lv" "value":str= -
lfind_val_gv "val":map+ "type":"gv" "value":str= -
lfind_val_lv "val":map+ "type":"lv" "value":str= -
lfind_val_nb "val":map+ "type":"nb" "value":nb= -
lfind_val_str "val":map+ "type":"str" "value":lstr= -
lfind_val_lit "val":map+ "type":"lit" "value":str= -
lfind_assign_gv "assign":map+ "type":"gv" "value":str= -
lfind_assign_lv "assign":map+ "type":"lv" "value":str= -

lfindby "op":map[]+ "name":"lfindby" "offset":offset "line":line "col":col
lfindby_from_gv "from":map+ "type":"gv" "value":str= -
lfindby_from_lv "from":map+ "type":"lv" "value":str= -
lfindby_index_gv "index":map+ "type":"gv" "value":str= -
lfindby_index_lv "index":map+ "type":"lv" "value":str= -
lfindby_index_int "index":map+ "type":"nb" "value":nb= -
lfindby_index_str "index":map+ "type":"str" "value":lstr= -
lfindby_val_gv "val":map+ "type":"gv" "value":str= -
lfindby_val_lv "val":map+ "type":"lv" "value":str= -
lfindby_val_nb "val":map+ "type":"nb" "value":nb= -
lfindby_val_str "val":map+ "type":"str" "value":lstr= -
lfindby_val_lit "val":map+ "type":"lit" "value":str= -
lfindby_assign_gv "assign":map+ "type":"gv" "value":str= -
lfindby_assign_lv "assign":map+ "type":"lv" "value":str= -

date "op":map[]+ "name":"date" "offset":offset "line":line "col":col
date_now "now":true
date_year_nb "year":map+ "type":"nb" "value":nb= -
date_year_gv "year":map+ "type":"gv" "value":str= -
date_year_lv "year":map+ "type":"lv" "value":str= -
date_month_nb "month":map+ "type":"nb" "value":nb= -
date_month_gv "month":map+ "type":"gv" "value":str= -
date_month_lv "month":map+ "type":"lv" "value":str= -
date_day_nb "day":map+ "type":"nb" "value":nb= -
date_day_gv "day":map+ "type":"gv" "value":str= -
date_day_lv "day":map+ "type":"lv" "value":str= -
date_hour_nb "hour":map+ "type":"nb" "value":nb= -
date_hour_gv "hour":map+ "type":"gv" "value":str= -
date_hour_lv "hour":map+ "type":"lv" "value":str= -
date_min_nb "min":map+ "type":"nb" "value":nb= -
date_min_gv "min":map+ "type":"gv" "value":str= -
date_min_lv "min":map+ "type":"lv" "value":str= -
date_sec_nb "sec":map+ "type":"nb" "value":nb= -
date_sec_gv "sec":map+ "type":"gv" "value":str= -
date_sec_lv "sec":map+ "type":"lv" "value":str= -
date_ms_nb "ms":map+ "type":"nb" "value":nb= -
date_ms_gv "ms":map+ "type":"gv" "value":str= -
date_ms_lv "ms":map+ "type":"lv" "value":str= -
date_assign_gv "assign":map+ "type":"gv" "value":str= -
date_assign_lv "assign":map+ "type":"lv" "value":str= -

dset "op":map[]+ "name":str= "offset":offset "line":line "col":col
dset_from_gv "from":map+ "type":"gv" "value":str= -
dset_from_lv "from":map+ "type":"lv" "value":str= -
dset_field "field":str=
dset_val_gv "val":map+ "type":"gv" "value":str= -
dset_val_lv "val":map+ "type":"lv" "value":str= -
dset_val_nb "val":map+ "type":"nb" "value":nb= -

dget "op":map[]+ "name":"dget" "offset":offset "line":line "col":col
dget_from_gv "from":map+ "type":"gv" "value":str= -
dget_from_lv "from":map+ "type":"lv" "value":str= -
dget_field "field":str=
dget_assign_gv "assign":map+ "type":"gv" "value":str= -
dget_assign_lv "assign":map+ "type":"lv" "value":str= -

dsub "op":map[]+ "name":"dsub" "offset":offset "line":line "col":col
dsub_from_gv "from":map+ "type":"gv" "value":str= -
dsub_from_lv "from":map+ "type":"lv" "value":str= -
dsub_val_gv "val":map+ "type":"gv" "value":str= -
dsub_val_lv "val":map+ "type":"lv" "value":str= -
dsub_assign_gv "assign":map+ "type":"gv" "value":str= -
dsub_assign_lv "assign":map+ "type":"lv" "value":str= -
`);
