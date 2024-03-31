import {Generator, ResultFile} from "../generator";
import {docGenerator} from "./doc.generator";
import {GENERATOR_BUILDER, GENERATOR_SYNTAX, PARSER} from "./syntax";
import {
  BoolVar,
  FuncParam,
  FuncParams,
  GeneratorFunc,
  NbVar,
  OptimizedGeneratorFunctions,
  optimizeGeneratorFunctions,
  RangeVar,
  RefVar,
  staticAnalysis,
  StrVar
} from "./types";
import {
  GeneratorError,
  hasProperty,
  InputData,
  Position,
  setProperty,
  toCamelCase,
  toKebabCase,
  toSnakeCase,
  toTitleCase,
  toUpperCamelCase
} from "../utils";
import {fromBase64Url, toBase64Url} from "../../../utils/base64";

type Range = {min: number, max: number, step: number};
type Var = MapVar|string|number|boolean|Date|(MapVar|string|number|boolean|null|Date)[]|null;
type MapVar = {[p:string]: Var};
type ReturnVar = {type: 'return'|'continue'|'break', value: Var|undefined};
type VariableTypes = 'nb'|'nb[]'|'bool'|'bool[]'|'str'|'str[]'|'date'|'list'|'map'|'file'|'null'|'undefined';
type VariableType = (
  {field: string, type: 'nb', value: number} |
  {field: string, type: 'bool', value: boolean} |
  {field: string, type: 'str', value: string} |
  {field: string, type: 'date', value: Date} |
  {field: string, type: 'list', value: Array<Var>} |
  {field: string, type: 'map', value: MapVar} |
  {field: string, type: 'file', value: ResultFile} |
  {field: string, type: 'null', value: null} |
  {field: string, type: 'undefined', value: undefined}
);
type CallStack = (
  {type: 'call', fct: GeneratorFunc, local: MapVar, args: Var[], index: number} |
  {type: 'loop', fct: GeneratorFunc, local: MapVar, args: Var[], index: number, range: Range}
)[];
type State = {
  files: ResultFile[],
  global: MapVar,
  stack: CallStack,
}

export class Generator_0_0_1 implements Generator {
  private _running = false;

  get running(): boolean {
    return this._running;
  }

  public stop() {
    this._running = false;
  }

  public get docGenerator(): string {
    return docGenerator;
  }

  public parseSrcGenerator(text: string): OptimizedGeneratorFunctions {
    const data = PARSER.parse(text, GENERATOR_SYNTAX, GENERATOR_BUILDER) as any;
    const functions = optimizeGeneratorFunctions(data.fct || []);
    staticAnalysis(functions);
    return functions;
  }

  public generate(inputData: InputData, functions: OptimizedGeneratorFunctions): ResultFile[] {
    try {
      this._running = true;
      const state: State = {files: [], global: {'%input': inputData}, stack: []};
      this.call(functions, state, ':main', []);
      return state.files;
    }
    catch(ex) {
      throw ex;
    }
    finally {
      this._running = false;
    }
  }

  /**
   * Calls a generator function.
   * @param functions a map of all the generator functions
   * @param state the current state of the generation process
   * @param fctName the name of the function to call
   * @param args the parameter values to pass to the function
   */
  private call(functions: OptimizedGeneratorFunctions, state: State, fctName: string, args: Var[]): ReturnVar {
    // Push function to call stack
    state.stack.push({type: 'call', fct: functions[fctName], local: {}, args, index: 0});
    const fct = state.stack.at(-1)!;
    // Assigning parameter values
    const params = fct.fct.param;
    for(let i = 0; i < params.length; ++i) fct.local[params[i]] = args[i];
    // Iterate ops
    for(const op of fct.fct.op) {
      if(!this.running) throw new GeneratorError('generator stopped');
      switch(op.name) {
        case '_ccase': {
          const from = this.getRefVar(state, op.from);
          if(from.type === 'str') this.setRefVar(state, op.assign, toCamelCase(from.value));
          else this.throwUnexpectedType(op, from, 'str');
          break;
        }
        case '_kcase': {
          const from = this.getRefVar(state, op.from);
          if(from.type === 'str') this.setRefVar(state, op.assign, toKebabCase(from.value));
          else this.throwUnexpectedType(op, from, 'str');
          break;
        }
        case '_scase': {
          const from = this.getRefVar(state, op.from);
          if(from.type === 'str') this.setRefVar(state, op.assign, toSnakeCase(from.value));
          else this.throwUnexpectedType(op, from, 'str');
          break;
        }
        case '_tcase': {
          const from = this.getRefVar(state, op.from);
          if(from.type === 'str') this.setRefVar(state, op.assign, toTitleCase(from.value));
          else this.throwUnexpectedType(op, from, 'str');
          break;
        }
        case '_lcase': {
          const from = this.getRefVar(state, op.from);
          if(from.type === 'str') this.setRefVar(state, op.assign, from.value.toLowerCase());
          else this.throwUnexpectedType(op, from, 'str');
          break;
        }
        case '_ucase': {
          const from = this.getRefVar(state, op.from);
          if(from.type === 'str') this.setRefVar(state, op.assign, from.value.toUpperCase());
          else this.throwUnexpectedType(op, from, 'str');
          break;
        }
        case '_uccase': {
          const from = this.getRefVar(state, op.from);
          if(from.type === 'str') this.setRefVar(state, op.assign, toUpperCamelCase(from.value));
          else this.throwUnexpectedType(op, from, 'str');
          break;
        }
        case '_ukcase': {
          const from = this.getRefVar(state, op.from);
          if(from.type === 'str') this.setRefVar(state, op.assign, toKebabCase(from.value).toUpperCase());
          else this.throwUnexpectedType(op, from, 'str');
          break;
        }
        case '_uscase': {
          const from = this.getRefVar(state, op.from);
          if(from.type === 'str') this.setRefVar(state, op.assign, toSnakeCase(from.value).toUpperCase());
          else this.throwUnexpectedType(op, from, 'str');
          break;
        }
        case '_b64': {
          const from = this.getRefVar(state, op.from);
          try {
            if(from.type === 'str') this.setRefVar(state, op.assign, btoa(from.value));
            else this.throwUnexpectedType(op, from, 'str');
          }
          catch(ex) {
            this.throwError(op, 'base64 conversion failed')
          }
          break;
        }
        case 'b64_': {
          const from = this.getRefVar(state, op.from);
          try {
            if(from.type === 'str') this.setRefVar(state, op.assign, atob(from.value));
            else this.throwUnexpectedType(op, from, 'str');
          }
          catch(ex) {
            this.throwError(op, 'base64 conversion failed')
          }
          break;
        }
        case '_b64url': {
          const from = this.getRefVar(state, op.from);
          try {
            if(from.type === 'str') this.setRefVar(state, op.assign, toBase64Url(from.value));
            else this.throwUnexpectedType(op, from, 'str');
          }
          catch(ex) {
            this.throwError(op, 'base64url conversion failed')
          }
          break;
        }
        case 'b64url_': {
          const from = this.getRefVar(state, op.from);
          try {
            if(from.type === 'str') this.setRefVar(state, op.assign, fromBase64Url(from.value));
            else this.throwUnexpectedType(op, from, 'str');
          }
          catch(ex) {
            this.throwError(op, 'base64url conversion failed')
          }
          break;
        }
        case '_json': {
          const from = this.getRefVar(state, op.from);
          const indent = !op.indent ? undefined :
            op.indent.type === 'nb' || op.indent.type === 'str' ?
              op.indent.value : undefined;
          if(from.type === 'undefined' || from.type === 'file') {
            this.throwUnexpectedType(op, from, ['map','list','str','nb','bool','null','date']);
          }
          else this.setRefVar(state, op.assign, JSON.stringify(from.value,
            (_, val) => state.files.includes(val) ? undefined : val
          , indent));
          break;
        }
        case 'json_': {
          const from = this.getRefVar(state, op.from);
          try {
            if(from.type === 'str') this.setRefVar(state, op.assign, JSON.parse(from.value));
            else this.throwUnexpectedType(op, from, 'str');
          }
          catch(ex) {
            this.throwError(op, 'json conversion failed')
          }
          break;
        }
        case '_char': {
          const from = this.getRefVar(state, op.from);
          if(from.type === 'nb') this.setRefVar(state, op.assign, String.fromCodePoint(from.value));
          else if(from.type === 'list') {
            this.setRefVar(
              state, op.assign,
              String.fromCodePoint(...this.getNbArray(op, state, from.field, from.value))
            );
          }
          else this.throwUnexpectedType(op, from, ['nb', 'nb[]']);
          break;
        }
        case '_date': {
          const from = this.getRefVar(state, op.from);
          if(from.type === 'nb' || from.type === 'str') {
            const date = new Date(from.value);
            if(isNaN(date.getFullYear())) this.throwError(op, `invalid date: "${from.value}"`)
            this.setRefVar(state, op.assign, date);
          }
          else this.throwUnexpectedType(op, from, ['nb', 'str']);
          break;
        }
        case '_int': {
          const from = this.getRefVar(state, op.from);
          if(from.type === 'str') this.setRefVar(state, op.assign, parseInt(from.value));
          else if(from.type === 'nb') this.setRefVar(state, op.assign, Math.floor(from.value));
          else if(from.type === 'bool') this.setRefVar(state, op.assign, +from.value);
          else if(from.type === 'date') this.setRefVar(state, op.assign, +from.value);
          else this.throwUnexpectedType(op, from, ['str', 'nb', 'bool', 'date']);
          break;
        }
        case '_nb': {
          const from = this.getRefVar(state, op.from);
          if(from.type === 'str') this.setRefVar(state, op.assign, parseFloat(from.value));
          else if(from.type === 'nb') this.setRefVar(state, op.assign, from.value);
          else if(from.type === 'bool') this.setRefVar(state, op.assign, +from.value);
          else if(from.type === 'date') this.setRefVar(state, op.assign, +from.value);
          else this.throwUnexpectedType(op, from, ['str', 'nb', 'bool', 'date']);
          break;
        }
        case '_str': {
          const from = this.getRefVar(state, op.from);
          if(from.type === 'str') this.setRefVar(state, op.assign, from.value);
          else if(from.type === 'nb') this.setRefVar(state, op.assign, from.value.toString());
          else if(from.type === 'bool') this.setRefVar(state, op.assign, from.value.toString());
          else if(from.type === 'date') this.setRefVar(state, op.assign, from.value.toJSON());
          else this.throwUnexpectedType(op, from, ['str', 'nb', 'bool', 'date']);
          break;
        }
        case 'date': {
          if(op.now) {
            this.setRefVar(state, op.assign, new Date());
          } else {
            if(op.hour === undefined) {
              const year = this.getRefAndNb(op, state, op.year);
              this.setRefVar(state, op.assign, new Date(
                year,
                this.getRefAndNb(op, state, op.month),
                this.getRefAndNb(op, state, op.day),
              )).setFullYear(year);
            } else {
              const year = this.getRefAndNb(op, state, op.year);
              this.setRefVar(state, op.assign, new Date(
                year,
                this.getRefAndNb(op, state, op.month),
                this.getRefAndNb(op, state, op.day),
                this.getRefAndNb(op, state, op.hour),
                this.getRefAndNb(op, state, op.min!),
                this.getRefAndNb(op, state, op.sec!),
                this.getRefAndNb(op, state, op.ms!),
              )).setFullYear(year);
            }
          }
          break;
        }
        case 'dget': {
          const from = this.getRefVar(state, op.from);
          if(from.type === 'date') {
            const date = from.value;
            switch(op.field) {
              case 'year':
                this.setRefVar(state, op.assign, date.getFullYear());
                break;
              case 'month':
                this.setRefVar(state, op.assign, date.getMonth()+1);
                break;
              case 'day':
                this.setRefVar(state, op.assign, date.getDate());
                break;
              case 'hour':
                this.setRefVar(state, op.assign, date.getHours());
                break;
              case 'min':
                this.setRefVar(state, op.assign, date.getMinutes());
                break;
              case 'sec':
                this.setRefVar(state, op.assign, date.getSeconds());
                break;
              case 'ms':
                this.setRefVar(state, op.assign, date.getMilliseconds());
                break;
            }
          }
          else this.throwUnexpectedType(op, from, 'date');
          break;
        }
        case 'dset': {
          const from = this.getRefVar(state, op.from);
          if(from.type === 'date') {
            const date = from.value;
            const val = this.getRefAndNb(op, state, op.val);
            switch(op.field) {
              case 'year': date.setFullYear(val); break;
              case 'month': date.setMonth(val-1); break;
              case 'day': date.setDate(val); break;
              case 'hour': date.setHours(val); break;
              case 'min': date.setMinutes(val); break;
              case 'sec': date.setSeconds(val); break;
              case 'ms': date.setMilliseconds(val); break;
            }
          }
          else this.throwUnexpectedType(op, from, 'date');
          break;
        }
        case 'dadd': {
          const from = this.getRefVar(state, op.from);
          if(from.type === 'date') {
            const date = from.value;
            const val = this.getRefAndNb(op, state, op.val);
            switch(op.field) {
              case 'year': date.setFullYear(date.getFullYear()+val); break;
              case 'month': date.setMonth(date.getMonth()+val); break;
              case 'day': date.setDate(date.getDate()+val); break;
              case 'hour': date.setHours(date.getHours()+val); break;
              case 'min': date.setMinutes(date.getMinutes()+val); break;
              case 'sec': date.setSeconds(date.getSeconds()+val); break;
              case 'ms': date.setMilliseconds(date.getMilliseconds()+val); break;
            }
          }
          else this.throwUnexpectedType(op, from, 'date');
          break;
        }
        case 'dsub': {
          const from = this.getRefVar(state, op.from);
          const val = this.getRefVar(state, op.val);
          if(from.type === 'date') {
            if(val.type === 'date') {
              const zero = new Date(0,0,0,0,0,0,0).setFullYear(0);
              this.setRefVar(state, op.assign, new Date(zero+(+from.value-+val.value)));
            }
            else this.throwUnexpectedType(op, val, 'date');
          }
          else this.throwUnexpectedType(op, from, 'date');
          break;
        }
        case 'abs': {
          const val = this.getRefAndNb(op, state, op.val);
          this.setRefVar(state, op.assign, Math.abs(val));
          break;
        }
        case 'ceil': {
          const val = this.getRefAndNb(op, state, op.val);
          this.setRefVar(state, op.assign, Math.ceil(val));
          break;
        }
        case 'floor': {
          const val = this.getRefAndNb(op, state, op.val);
          this.setRefVar(state, op.assign, Math.floor(val));
          break;
        }
        case 'trunc': {
          const val = this.getRefAndNb(op, state, op.val);
          this.setRefVar(state, op.assign, Math.trunc(val));
          break;
        }
        case 'round': {
          const val = this.getRefAndNb(op, state, op.val);
          if(op.amount === undefined) this.setRefVar(state, op.assign, Math.round(val));
          else {
            const amount = this.getRefAndNb(op, state, op.amount);
            this.setRefVar(state, op.assign, val.toFixed(amount));
          }
          break;
        }
        case 'sin': {
          const val = this.getRefAndNb(op, state, op.val);
          this.setRefVar(state, op.assign, Math.sin(val));
          break;
        }
        case 'cos': {
          const val = this.getRefAndNb(op, state, op.val);
          this.setRefVar(state, op.assign, Math.cos(val));
          break;
        }
        case 'tan': {
          const val = this.getRefAndNb(op, state, op.val);
          this.setRefVar(state, op.assign, Math.tan(val));
          break;
        }
        case 'sinh': {
          const val = this.getRefAndNb(op, state, op.val);
          this.setRefVar(state, op.assign, Math.sinh(val));
          break;
        }
        case 'cosh': {
          const val = this.getRefAndNb(op, state, op.val);
          this.setRefVar(state, op.assign, Math.cosh(val));
          break;
        }
        case 'tanh': {
          const val = this.getRefAndNb(op, state, op.val);
          this.setRefVar(state, op.assign, Math.tanh(val));
          break;
        }
        case 'acos': {
          const val = this.getRefAndNb(op, state, op.val);
          this.setRefVar(state, op.assign, Math.acos(val));
          break;
        }
        case 'acosh': {
          const val = this.getRefAndNb(op, state, op.val);
          this.setRefVar(state, op.assign, Math.acosh(val));
          break;
        }
        case 'asin': {
          const val = this.getRefAndNb(op, state, op.val);
          this.setRefVar(state, op.assign, Math.asin(val));
          break;
        }
        case 'asinh': {
          const val = this.getRefAndNb(op, state, op.val);
          this.setRefVar(state, op.assign, Math.asinh(val));
          break;
        }
        case 'atan': {
          const val = this.getRefAndNb(op, state, op.val);
          this.setRefVar(state, op.assign, Math.atan(val));
          break;
        }
        case 'atan2': {
          const val1 = this.getRefAndNb(op, state, op.val1);
          const val2 = this.getRefAndNb(op, state, op.val2);
          this.setRefVar(state, op.assign, Math.atan2(val1, val2));
          break;
        }
        case 'atanh': {
          const val = this.getRefAndNb(op, state, op.val);
          this.setRefVar(state, op.assign, Math.atanh(val));
          break;
        }
        case 'exp': {
          const val = this.getRefAndNb(op, state, op.val);
          this.setRefVar(state, op.assign, Math.exp(val));
          break;
        }
        case 'expm1': {
          const val = this.getRefAndNb(op, state, op.val);
          this.setRefVar(state, op.assign, Math.expm1(val));
          break;
        }
        case 'log': {
          const val = this.getRefAndNb(op, state, op.val);
          this.setRefVar(state, op.assign, Math.log(val));
          break;
        }
        case 'log10': {
          const val = this.getRefAndNb(op, state, op.val);
          this.setRefVar(state, op.assign, Math.log10(val));
          break;
        }
        case 'log1p': {
          const val = this.getRefAndNb(op, state, op.val);
          this.setRefVar(state, op.assign, Math.log1p(val));
          break;
        }
        case 'log2': {
          const val = this.getRefAndNb(op, state, op.val);
          this.setRefVar(state, op.assign, Math.log2(val));
          break;
        }
        case 'sqrt': {
          const val = this.getRefAndNb(op, state, op.val);
          this.setRefVar(state, op.assign, Math.sqrt(val));
          break;
        }
        case 'cbrt': {
          const val = this.getRefAndNb(op, state, op.val);
          this.setRefVar(state, op.assign, Math.cbrt(val));
          break;
        }
        case 'hypot': {
          if(op.val.length === 0) this.setRefVar(state, op.assign, 0);
          else {
            const values = this.getRefAndNbArray(op, state, op.val);
            this.setRefVar(state, op.assign, Math.hypot(...values));
          }
          break;
        }
        case 'add': {
          const result = op.val.length === 0 ? 0 :
            this.getRefAndNbArray(op, state, op.val).reduce((acc,val) => acc + val);
          this.setRefVar(state, op.assign, result);
          break;
        }
        case 'sub': {
          const result = op.val.length === 0 ? 0 :
            this.getRefAndNbArray(op, state, op.val).reduce((acc,val) => acc - val);
          this.setRefVar(state, op.assign, result);
          break;
        }
        case 'mul': {
          const result = op.val.length === 0 ? 0 :
            this.getRefAndNbArray(op, state, op.val).reduce((acc,val) => acc * val);
          this.setRefVar(state, op.assign, result);
          break;
        }
        case 'div': {
          const result = op.val.length === 0 ? 0 :
            this.getRefAndNbArray(op, state, op.val).reduce((acc,val) => {
              if(val === 0) this.throwError(op, 'division by zero');
              return acc / val;
            });
          this.setRefVar(state, op.assign, result);
          break;
        }
        case 'pow': {
          const result = op.val.length === 0 ? 0 :
            this.getRefAndNbArray(op, state, op.val).reduce((acc,val) => Math.pow(acc, val));
          this.setRefVar(state, op.assign, result);
          break;
        }
        case 'mod': {
          const result = op.val.length === 0 ? 0 :
            this.getRefAndNbArray(op, state, op.val).reduce((acc,val) => acc % val);
          this.setRefVar(state, op.assign, result);
          break;
        }
        case 'xor': {
          const result = op.val.length === 0 ? 0 :
            this.getRefAndNbArray(op, state, op.val).reduce((acc,val) => acc ^ val);
          this.setRefVar(state, op.assign, result);
          break;
        }
        case 'and': {
          if(op.val.length === 0) this.setRefVar(state, op.assign, true);
          else if(op.val[0].type === 'bool') this.setRefVar(
            state, op.assign, this.getRefAndBoolArray(op, state, op.val as any).reduce((acc, val) => acc && val)
          );
          else if(op.val[0].type === 'nb') this.setRefVar(
            state, op.assign, this.getRefAndNbArray(op, state, op.val as any).reduce((acc, val) => acc & val)
          );
          else this.throwUnexpectedType(op, null, ['nb[]', 'bool[]']);
          break;
        }
        case 'or': {
          if(op.val.length === 0) this.setRefVar(state, op.assign, true);
          else if(op.val[0].type === 'bool') this.setRefVar(
            state, op.assign, this.getRefAndBoolArray(op, state, op.val as any).reduce((acc, val) => acc || val)
          );
          else if(op.val[0].type === 'nb') this.setRefVar(
            state, op.assign, this.getRefAndNbArray(op, state, op.val as any).reduce((acc, val) => acc | val)
          );
          else this.throwUnexpectedType(op, null, ['nb[]', 'bool[]']);
          break;
        }
        case 'eq': {
          const value = this.equalsVars(op, state, op.val, false);
          this.setRefVar(state, op.assign, value);
          break;
        }
        case 'ne': {
          const value = this.equalsVars(op, state, op.val, true);
          this.setRefVar(state, op.assign, value);
          break;
        }
        case 'lt': {
          if(op.val.length === 0) this.setRefVar(state, op.assign, true);
          else {
            const values = this.getRefAndNbArray(op, state, op.val);
            const result = this.equalsVarsLoop(values, (val1, val2) => val1 < val2);
            this.setRefVar(state, op.assign, result);
          }
          break;
        }
        case 'lte': {
          if(op.val.length === 0) this.setRefVar(state, op.assign, true);
          else {
            const values = this.getRefAndNbArray(op, state, op.val);
            const result = this.equalsVarsLoop(values, (val1, val2) => val1 <= val2);
            this.setRefVar(state, op.assign, result);
          }
          break;
        }
        case 'gt': {
          if(op.val.length === 0) this.setRefVar(state, op.assign, true);
          else {
            const values = this.getRefAndNbArray(op, state, op.val);
            const result = this.equalsVarsLoop(values, (val1, val2) => val1 > val2);
            this.setRefVar(state, op.assign, result);
          }
          break;
        }
        case 'gte': {
          if(op.val.length === 0) this.setRefVar(state, op.assign, true);
          else {
            const values = this.getRefAndNbArray(op, state, op.val);
            const result = this.equalsVarsLoop(values, (val1, val2) => val1 >= val2);
            this.setRefVar(state, op.assign, result);
          }
          break;
        }
        case 'min': {
          if(op.val.length === 0) this.setRefVar(state, op.assign, 0);
          else {
            const values = this.getRefAndNbArray(op, state, op.val);
            this.setRefVar(state, op.assign, Math.min(...values));
          }
          break;
        }
        case 'max': {
          if(op.val.length === 0) this.setRefVar(state, op.assign, 0);
          else {
            const values = this.getRefAndNbArray(op, state, op.val);
            this.setRefVar(state, op.assign, Math.max(...values));
          }
          break;
        }
        case 'not': {
          if(op.from.type === 'nb') {
            this.setRefVar(state, op.assign, ~op.from.value);
          } else {
            const from = this.getRefVar(state, op.from);
            if(from.type === 'nb') this.setRefVar(state, op.assign, ~from.value);
            if(from.type === 'bool') this.setRefVar(state, op.assign, !from.value);
            else this.throwUnexpectedType(op, from, ['nb', 'bool']);
          }
          break;
        }
        case 'nan': {
          const from = this.getRefAndNb(op, state, op.from);
          this.setRefVar(state, op.assign, isNaN(from));
          break;
        }
        case 'lshift': {
          const from = Math.trunc(this.getRefAndNb(op, state, op.from));
          const index = this.getRefAndNb(op, state, op.index);
          const result = index === 0 ? from : Math.floor(from * Math.pow(2, index));
          this.setRefVar(state, op.assign, result);
          break;
        }
        case 'rshift': {
          const from = Math.trunc(this.getRefAndNb(op, state, op.from));
          const index = this.getRefAndNb(op, state, op.index);
          const result = index === 0 ? from : Math.floor(from * Math.pow(2, -index));
          this.setRefVar(state, op.assign, result);
          break;
        }
        case 'rand': {
          const min = Math.trunc(this.getRefAndNb(op, state, op.min));
          const max = Math.trunc(this.getRefAndNb(op, state, op.max));
          this.setRefVar(state, op.assign, Math.floor(Math.random()*(max-min))+min);
          break;
        }
        case 'replace': {
          const from = this.getRefAndStr(op, state, op.from);
          const regex = this.getRefAndStr(op, state, op.regex);
          const to = this.getRefAndStr(op, state, op.to);
          try {
            this.setRefVar(state, op.assign, from.replace(new RegExp(regex, 'ug'), to));
          }
          catch(ex) {
            this.throwError(op, 'bad regex');
          }
          break;
        }
        case 'lpad': {
          const from = this.getRefAndNbAndStr(op, state, op.from);
          const amount = this.getRefAndNb(op, state, op.amount);
          const val = this.getRefAndNbAndStr(op, state, op.val);
          this.setRefVar(state, op.assign, from.toString().padStart(amount, val.toString()));
          break;
        }
        case 'rpad': {
          const from = this.getRefAndNbAndStr(op, state, op.from);
          const amount = this.getRefAndNb(op, state, op.amount);
          const val = this.getRefAndNbAndStr(op, state, op.val);
          this.setRefVar(state, op.assign, from.toString().padEnd(amount, val.toString()));
          break;
        }
        case 'match': {
          const from = this.getRefAndStr(op, state, op.from);
          const regex = this.getRefAndStr(op, state, op.regex);
          try {
            this.setRefVar(state, op.assign, from.match(new RegExp(regex, 'ug')));
          }
          catch(ex) {
            this.throwError(op, 'bad regex');
          }
          break;
        }
        case 'split': {
          const from = this.getRefAndStr(op, state, op.from);
          const regex = this.getRefAndStr(op, state, op.regex);
          try {
            this.setRefVar(state, op.assign, from.split(new RegExp(regex, 'ug')));
          }
          catch(ex) {
            this.throwError(op, 'bad regex');
          }
          break;
        }
        case 'concat': {
          if(op.param.length === 0) this.setRefVar(state, op.assign, '');
          else {
            const text = op.param.map(value => this.getRefAndNbAndStr(op, state, value)).join('');
            this.setRefVar(state, op.assign, text);
          }
          break;
        }
        case 'getch': {
          const from = this.getRefAndStr(op, state, op.from);
          const index = this.getRefAndNb(op, state, op.index);
          this.setRefVar(state, op.assign, from.codePointAt(index));
          break;
        }
        case 'has': {
          const from = this.getRefVar(state, op.from);
          if(from.type === 'map') {
            const index = this.getRefAndNbAndStr(op, state, op.index);
            const value = this.getVar(state, from.value, index.toString());
            this.setRefVar(state, op.assign, value.value !== undefined);
          }
          else if(from.type === 'list') {
            const index = this.getRefAndNb(op, state, op.index as RefVar|NbVar);
            const value = this.getVarInArray(state, from.value, index);
            this.setRefVar(state, op.assign, value.value !== undefined);
          }
          else this.throwUnexpectedType(op, from, ['map', 'list']);
          break;
        }
        case 'get': {
          const from = this.getRefVar(state, op.from);
          if(from.type === 'map') {
            const index = this.getRefAndNbAndStr(op, state, op.index);
            const value = this.getVar(state, from.value, index.toString());
            if(value.value === undefined) this.throwNotExists(op, from.field, index);
            this.setRefVar(state, op.assign, value.value);
          }
          else if(from.type === 'list') {
            const index = this.getRefAndNb(op, state, op.index as RefVar|NbVar);
            const value = this.getVarInArray(state, from.value, index);
            if(value.value === undefined) this.throwNotExists(op, from.field, index);
            this.setRefVar(state, op.assign, value.value);
          }
          else this.throwUnexpectedType(op, from, ['map', 'list']);
          break;
        }
        case 'set': {
          const value = this.getFuncParam(op, state, op.val);
          this.setRefVar(state, op.to, value);
          break;
        }
        case 'mset': {
          const from = this.getRefVar(state, op.from);
          if(from.type === 'map') {
            for(const field of op.fields) {
              const assign: RefVar = {type: field.name.startsWith('$') ? 'lv' : 'gv', value: field.name};
              const value = this.getFuncParam(op, state, field.value);
              this.setRefVar(state, assign, value);
            }
          }
          else this.throwUnexpectedType(op, from, 'map');
          break;
        }
        case 'cp': {
          const from = this.getRefVar(state, op.from);
          if(from.type === 'date') this.setRefVar(state, op.assign, new Date(from.value));
          else if(from.type === 'list') this.setRefVar(state, op.assign, from.value.slice());
          else if(from.type === 'map') this.setRefVar(state, op.assign, {...from.value});
          else this.setRefVar(state, op.assign, from.value);
          break;
        }
        case 'dcp': {
          const from = this.getRefVar(state, op.from);
          if(from.type === 'file') this.setRefVar(state, op.assign, from.value);
          else this.setRefVar(state, op.assign, structuredClone(from.value));
          break;
        }
        case 'len': {
          const from = this.getRefVar(state, op.from);
          if(from.type === 'list' || from.type === 'str') {
            this.setRefVar(state, op.assign, from.value.length);
          }
          else this.throwUnexpectedType(op, from, ['list', 'str']);
          break;
        }
        case 'lcat': {
          if(op.param.length === 0) this.setRefVar(state, op.assign, '');
          else {
            const text = op.param.map(value => {
              if(value.type === 'gv' || value.type === 'lv') {
                const type = this.getRefVar(state, value);
                if(type.type === 'list') return type.value;
                this.throwUnexpectedType(op, type, 'list');
              }
              return;
            }).flat();
            this.setRefVar(state, op.assign, text);
          }
          break;
        }
        case 'ldel': {
          const from = this.getRefVar(state, op.from);
          if(from.type === 'list') {
            const index = this.getRefAndNb(op, state, op.index);
            from.value.splice(index, 1);
          }
          else this.throwUnexpectedType(op, from, 'list');
          break;
        }
        case 'lins': {
          const from = this.getRefVar(state, op.from);
          if(from.type === 'list') {
            const index = this.getRefAndNb(op, state, op.index);
            const value = this.getFuncParam(op, state, op.val);
            from.value.splice(index, 0, value);
          }
          else this.throwUnexpectedType(op, from, 'list');
          break;
        }
        case 'lpush': {
          const from = this.getRefVar(state, op.from);
          if(from.type === 'list') {
            const value = this.getFuncParam(op, state, op.val);
            from.value.push(value);
          }
          else this.throwUnexpectedType(op, from, 'list');
          break;
        }
        case 'lpop': {
          const from = this.getRefVar(state, op.from);
          if(from.type === 'list') {
            if(op.val === undefined) from.value.pop();
            else {
              const value = this.getFuncParam(op, state, op.val);
              const index = from.value.indexOf(value);
              if(index === -1) this.throwError(op, 'value not found');
              else from.value.splice(index, 1);
            }
          }
          else this.throwUnexpectedType(op, from, 'list');
          break;
        }
        case 'lrev': {
          const from = this.getRefVar(state, op.from);
          if(from.type === 'list') from.value.reverse();
          else this.throwUnexpectedType(op, from, 'list');
          break;
        }
        case 'lset': {
          const from = this.getRefVar(state, op.from);
          if(from.type === 'list') {
            const index = this.getRefAndNb(op, state, op.index);
            from.value[index] = this.getFuncParam(op, state, op.val);
          }
          else this.throwUnexpectedType(op, from, 'list');
          break;
        }
        case 'linit': {
          const values = op.param.map(p => this.getFuncParam(op, state, p))
          this.setRefVar(state, op.from, values);
          break;
        }
        case 'lfill': {
          if(op.range) {
            const range = this.getValidRange(op, state, op.range);
            if(range.step < 0) {
              const amount = range.min - range.max;
              const list = new Array(amount);
              for(let i = range.min; i >= range.max; i += range.step) list.push(i);
            } else {
              const amount = range.max - range.min;
              const list = new Array(amount);
              for(let i = range.min; i < range.max; i += range.step) list.push(i);
            }
          } else {
            const value = this.getFuncParam(op, state, op.val);
            const amount = this.getRefAndNb(op, state, op.amount);
            const list = new Array(amount);
            for(let i = 0; i < amount; ++i) list.push(structuredClone(value));
            this.setRefVar(state, op.from, list);
          }
          break;
        }
        case 'lfind': {
          const from = this.getRefVar(state, op.from);
          if(from.type === 'list') {
            const value = this.getFuncParam(op, state, op.val);
            this.setRefVar(state, op.assign, from.value.indexOf(value));
          }
          else this.throwUnexpectedType(op, from, 'list');
          break;
        }
        case 'lfindby': {
          const from = this.getRefVar(state, op.from);
          if(from.type === 'list') {
            const value = this.getFuncParam(op, state, op.val);
            const index = this.getRefAndNbAndStr(op, state, op.index);
            const found = from.value.findIndex((v) => {
              const type = this.getType(state, v, from.field);
              if(type.type === 'map') {
                return type.value[index] === value;
              } else if(type.type === 'list' && typeof index === 'number') {
                return type.value[index] === value;
              }
              else return false;
            });
            this.setRefVar(state, op.assign, found);
          }
          else this.throwUnexpectedType(op, from, 'list');
          break;
        }
        case 'lsort': {
          const from = this.getRefVar(state, op.from);
          const order = op.order === 'asc' ? 1 : -1;
          if(from.type === 'list') {
            from.value.sort((a, b) => this.compareVars(state, op, from.field, order, a, b));
          }
          else this.throwUnexpectedType(op, from, 'list');
          break;
        }
        case 'lsortby': {
          const from = this.getRefVar(state, op.from);
          const order = op.order === 'asc' ? 1 : -1;
          if(from.type === 'list') {
            const index = this.getRefAndNbAndStr(op, state, op.index);
            from.value.sort((a,b) => {
              const typeA = this.getType(state, a, from.field);
              const typeB = this.getType(state, a, from.field);
              if(typeA.type === 'map' && typeB.type === 'map') {
                return this.compareVars(
                  state, op, from.field, order,
                  typeA.value[index], typeB.value[index]
                );
              } else if(typeA.type === 'list' && typeB.type === 'list' && typeof index === 'number') {
                return this.compareVars(
                  state, op, from.field, order,
                  typeA.value[index], typeB.value[index]
                );
              }
              else return this.compareVars(state, op, from.field, order, a, b);
            });
          }
          else this.throwUnexpectedType(op, from, 'list');
          break;
        }
        case 'ljoin': {
          const from = this.getRefVar(state, op.from);
          if(from.type === 'list') {
            const value = this.getRefAndStr(op, state, op.val);
            this.setRefVar(state, op.assign, from.value.join(value));
          }
          else this.throwUnexpectedType(op, from, 'list');
          break;
        }
        case 'slice': {
          const min = this.getRefAndNb(op, state, op.min);
          const max = op.max === undefined ? undefined : this.getRefAndNb(op, state, op.max);
          if(op.from.type === 'str') {
            this.setRefVar(state, op.assign, op.from.value.slice(min, max));
          } else {
            const from = this.getRefVar(state, op.from);
            if(from.type === 'str' || from.type === 'list') {
              this.setRefVar(state, op.assign, from.value.slice(min, max));
            }
            else this.throwUnexpectedType(op, from, ['list', 'str']);
          }
          break;
        }
        case 'fopen': {
          const path = this.getRefAndStr(op, state, op.filename);
          const file: ResultFile|undefined = state.files.find(f => f.path === path);
          if(file === undefined) {
            const file: ResultFile = {path, content: []};
            this.setRefVar(state, op.assign, file);
            state.files.push(file);
          }
          else this.setRefVar(state, op.assign, file);
          break;
        }
        case 'fwrite': {
          const to = this.getRefVar(state, op.to);
          if(to.type === 'file') {
            const value = this.getRefAndNbAndStr(op, state, op.val).toString();
            to.value.content.push(value);
          }
          else this.throwUnexpectedType(op, to, 'file');
          break;
        }
        case 'call': {
          const params = op.param.map(p => this.getFuncParam(op, state, p));
          const result = this.call(functions, state, op.fct, params);
          if(result.type === 'return') {
            const value = result.value === undefined ? null : result.value;
            if(op.assign !== undefined) this.setRefVar(state, op.assign, value);
          } else {
            state.stack.pop();
            return result;
          }
          break;
        }
        case 'switch': {
          const val = this.getRefVar(state, op.val);
          if(val.value === undefined) this.throwNotExists(op, val.field);
          const value = val.value;
          for(const cmp of op.cmp) {
            const cmpValue = this.getFuncParam(op, state, cmp.value);
            if(value === cmpValue) {
              const result = this.call(functions, state, cmp.fct, []);
              if(result.type !== 'return') {
                state.stack.pop();
                return result;
              }
              break;
            }
          }
          break;
        }
        case 'if': {
          const from = this.getFuncParam(op, state, op.from);
          if(from !== null && from !== false) {
            switch(op.action) {
              case 'continue': {
                state.stack.pop();
                return {type: 'continue', value: undefined};
              }
              case 'break': {
                const value = op.return === undefined ?
                  undefined : this.getFuncParam(op, state, op.return);
                state.stack.pop();
                return {type: 'break', value};
              }
              case 'return': {
                const value = op.return === undefined ?
                  undefined : this.getFuncParam(op, state, op.return);
                state.stack.pop();
                return {type: 'return', value};
              }
              case 'call': {
                const params = op.param.map(p => this.getFuncParam(op, state, p));
                const result = this.call(functions, state, op.fct, params);
                if(result.type === 'return') {
                  const value = result.value === undefined ? null : result.value;
                  if(op.assign !== undefined) this.setRefVar(state, op.assign, value);
                } else {
                  state.stack.pop();
                  return result;
                }
                break;
              }
            }
          }
          break;
        }
        case 'loop': {
          let collection: any[]|string|null = null;
          let range: Range = {min: 0, max: 0, step: 1};
          if(op.from.type === 'range') range = this.getValidRange(op, state, op.from);
          else {
            const from = this.getRefVar(state, op.from);
            if(from.type === 'list' || from.type === 'str') {
              collection = from.value;
              range.max = collection.length;
            }
            else this.throwUnexpectedType(op, from, ['list', 'str']);
          }
          const params = [0, collection, ...op.param.map(p => this.getFuncParam(op, state, p))];
          state.stack.push({type: 'loop', fct: functions[fctName], local: {}, args, index: 0, range});
          const boundCheck = range.step < 0 ?
            (i: number, max: number) => i >= max :
            (i: number, max: number) => i < max;
          for(let i = range.min; boundCheck(i, range.max); i += range.step) {
            params[0] = i;
            const result = this.call(functions, state, op.fct, params);
            if(result.type === 'continue') continue;
            if(result.type !== 'return' || result.value !== undefined) {
              const value = result.value === undefined ? null : result.value;
              if(op.assign !== undefined) this.setRefVar(state, op.assign, value);
              break;
            }
          }
          state.stack.pop();
          break;
        }
        case 'return': {
          const result: ReturnVar = {type: 'return', value: this.getFuncParam(op, state, op.val)};
          state.stack.pop();
          return result;
        }
        case 'panic': {
          const result = [];
          if(op.src_line !== undefined) {
            result.push(`line ${op.src_line}`);
            if(op.src_col !== undefined) {
              result.push(`col ${op.src_col}`);
            }
            result.push(': ');
          }
          result.push(this.getRefAndStr(op, state, op.msg));
          throw new GeneratorError(result.join());
        }
      }
    }
    // Pop function from call stack
    state.stack.pop();
    return {type: 'return', value: undefined};
  }

  /**
   * Sets a value to reference variable.
   * @param state current state of the generation process
   * @param refVar description of a reference variable to assign the value
   * @param value value to set
   * @returns the value
   */
  private setRefVar<T>(state: State, refVar: RefVar, value: T): T {
    const store = refVar.type === 'gv' ? state.global : state.stack.at(-1)!.local;
    const field = refVar.value;
    setProperty(store, field, value);
    return value;
  }

  /**
   * Gets type of a reference variable.
   * @param state current state of the generation process
   * @param refVar description of a reference variable
   * @returns a description of the variable type
   */
  private getRefVar(state: State, refVar: RefVar): VariableType {
    const store = refVar.type === 'gv' ? state.global : state.stack.at(-1)!.local;
    const field = refVar.value;
    return this.getVar(state, store, field);
  }

  /**
   * Gets type of a value.
   * @param state current state of the generation process
   * @param value value whose type is to be checked
   * @param field variable name
   * @returns a description of the variable type
   */
  private getType(state: State, value: Var, field: string): VariableType {
    if(value === undefined) return {field, type: 'undefined', value: undefined};
    if(value === null) return {field, type: 'null', value};
    if(value.constructor === Number) return {field, type: 'nb', value: value};
    if(value.constructor === String) return {field, type: 'str', value: value};
    if(value.constructor === Boolean) return {field, type: 'bool', value: value};
    if(value.constructor === Date) return {field, type: 'date', value};
    if(value.constructor === Array) return {field, type: 'list', value};
    if(state.files.includes(value as any)) return {field, type: 'file', value: value as any};
    return {field, type: 'map', value: value as any};
  }

  /**
   * Gets type of a variable from a map for given field.
   * @param state current state of the generation process
   * @param store map variable containing the value
   * @param field index to retrieve the value from
   * @returns a description of the variable type
   */
  private getVar(state: State, store: MapVar, field: string): VariableType {
    if(!hasProperty(store, field)) return {field, type: 'undefined', value: undefined};
    const value = store[field];
    return this.getType(state, value, field);
  }

  /**
   * Gets type of a variable from an array for given field.
   * @param state current state of the generation process
   * @param store array variable containing the value
   * @param index index to retrieve the value from
   * @returns a description of the variable type
   */
  private getVarInArray(state: State, store: Var[], index: number): VariableType {
    const field = index.toString();
    return this.getVar(state, store as any, field);
  }

  /**
   * Compares two values.
   * @param state current state of the generation process
   * @param position position in the source code
   * @param field variable name
   * @param order 1 if ascending, -1 if descending
   * @param value1 first value
   * @param value2 second value
   * @returns negative value if value1 is lower than value2,
   * positive value if value1 is greater than value2, else 0
   */
  private compareVars(state: State, position: Position, field: string, order: 1|-1, value1: Var, value2: Var): number {
    if(value1 === undefined) this.throwNotExists(position, field);
    if(value2 === undefined) this.throwNotExists(position, field);
    if(value1 === null || value1 === false) return -1;
    if(value2 === null || value2 === false) return 1;
    if(state.files.includes(value1 as any) || Array.isArray(value1)) return -1;
    if(value1.constructor === Object || typeof value1.constructor !== 'function') return -1;
    if(state.files.includes(value2 as any) || Array.isArray(value2)) return 1;
    if(value2.constructor === Object || typeof value2.constructor !== 'function') return 1;
    if(value1 === true) return -1;
    if(value2 === true) return 1;
    if(value1.constructor === Date) return value2.constructor === Date ? +value1-+value2 : -1;
    if(value2.constructor === Date) return value1.constructor === Date ? +value1-+value2 : 1;
    return String(value1).localeCompare(String(value2))*order;
  }

  /**
   * Checks if two values are equal.
   * @param value1 first value
   * @param value2 second value
   * @returns true if value1 equals value2, else false
   */
  private equalsTypes(value1: Var|Var[]|ResultFile|undefined, value2: Var|Var[]|ResultFile|undefined): boolean {
    if(value1 === undefined || value2 === undefined) return value1 === value2;
    if(value1 === null || value2 === null) return value1 === value2;
    if(value1.constructor === Date || value2.constructor === Date) {
      return value1.constructor === Date && value2.constructor === Date && +value1 === +value2;
    }
    return value1 === value2;
  }

  /**
   * Checks if all values are equal.
   * @param values
   * @param predicate
   * @returns true if all values are equal, else false
   */
  private equalsVarsLoop<T>(values: T[], predicate: (val1: T, val2: T) => boolean): boolean {
    if(values.length < 2) return true;
    let equals = true, lastValue = values[0];
    for(let i = 1; i < values.length; ++i) {
      const current = values[i];
      if(!predicate(lastValue, current)) {
        equals = false;
        break;
      }
      lastValue = current;
    }
    return equals;
  }

  /**
   * Checks if all values are equal.
   * @param state current state of the generation process
   * @param position position in the source code
   * @param values
   * @param not whether checking if not equals instead
   * @returns true if all values are equal, else false
   */
  private equalsVars(position: Position, state: State, values: FuncParams, not: boolean): boolean {
    if(values.length < 2) return true;
    else {
      return this.equalsVarsLoop(
        values.map(value => this.getFuncParam(position, state, value)), not ?
          (lastValue, current) => !this.equalsTypes(lastValue, current) :
          (lastValue, current) => this.equalsTypes(lastValue, current)
      );
    }
  }

  /**
   * Checks if all values are numbers and returns them as a number array.
   * @param state current state of the generation process
   * @param position position in the source code
   * @param field variable name
   * @param values
   */
  private getNbArray(position: Position, state: State, field: string, values: Var[]): number[] {
    return values.map(value => {
      const type = this.getType(state, value, field);
      if(type.type !== 'nb') this.throwUnexpectedType(position, type, 'nb');
      return type.value as number;
    });
  }

  /**
   * Checks if all values are numbers and returns them as a number array.
   * @param state current state of the generation process
   * @param position position in the source code
   * @param values
   */
  private getRefAndNbArray(position: Position, state: State, values: (RefVar|NbVar)[]): number[] {
    return values.map(value => {
      if(value.type === 'nb') return value.value;
      if(value.type === 'gv' || value.type === 'lv') {
        const type = this.getRefVar(state, value);
        if(type.type === 'nb') return type.value;
        this.throwUnexpectedType(position, type, 'nb');
      }
      return 0;
    });
  }

  /**
   * Checks if all values are booleans and returns them as a boolean array.
   * @param state current state of the generation process
   * @param position position in the source code
   * @param values
   */
  private getRefAndBoolArray(position: Position, state: State, values: (RefVar|BoolVar)[]): boolean[] {
    return values.map(value => {
      if(value.type === 'bool') return value.value === 'true';
      if(value.type === 'gv' || value.type === 'lv') {
        const type = this.getRefVar(state, value);
        if(type.type === 'bool') return type.value;
        this.throwUnexpectedType(position, type, 'bool');
      }
      return true;
    });
  }

  /**
   * Checks if value is a number and returns it.
   * @param state current state of the generation process
   * @param position position in the source code
   * @param value
   */
  private getRefAndNb(position: Position, state: State, value: RefVar|NbVar): number {
    if(value.type === 'nb') return value.value;
    if(value.type === 'gv' || value.type === 'lv') {
      const type = this.getRefVar(state, value);
      if(type.type === 'nb') return type.value;
      this.throwUnexpectedType(position, type, 'nb');
    }
    return 0;
  }

  /**
   * Checks if value is a string and returns it.
   * @param state current state of the generation process
   * @param position position in the source code
   * @param value
   */
  private getRefAndStr(position: Position, state: State, value: RefVar|StrVar): string {
    if(value.type === 'str') return value.value;
    if(value.type === 'gv' || value.type === 'lv') {
      const type = this.getRefVar(state, value);
      if(type.type === 'str') return type.value;
      this.throwUnexpectedType(position, type, 'str');
    }
    return '';
  }

  /**
   * Checks if value is a number or a string and returns it.
   * @param state current state of the generation process
   * @param position position in the source code
   * @param value
   */
  private getRefAndNbAndStr(position: Position, state: State, value: RefVar|NbVar|StrVar): string|number {
    if(value.type === 'nb' || value.type === 'str') return value.value;
    if(value.type === 'gv' || value.type === 'lv') {
      const type = this.getRefVar(state, value);
      if(type.type === 'nb' || type.type === 'str') return type.value;
      this.throwUnexpectedType(position, type, ['nb', 'str']);
    }
    return '';
  }

  /**
   * Checks if the range is a valid and returns it.
   * @param state current state of the generation process
   * @param position position in the source code
   * @param value
   */
  private getValidRange(position: Position, state: State, value: RangeVar): Range {
    const step = this.getRefAndNb(position, state, value.step);
    if(step === 0) this.throwError(position, 'invalid range (step = 0)');
    const bound = value.inclusive ? 0 : 1;
    const min = this.getRefAndNb(position, state, value.min);
    if(step > 0) {
      const max = this.getRefAndNb(position, state, value.max)-bound;
      if(min >= max) this.throwError(position, 'invalid range (min >= max)');
      if(step > max-min) this.throwError(position, 'invalid range (step > max-min)');
      return {min, max, step};
    } else {
      const max = this.getRefAndNb(position, state, value.max)+bound;
      if(max >= min) this.throwError(position, 'invalid range (max >= min)');
      if(step > min-max) this.throwError(position, 'invalid range (step > min-max)');
      return {min, max, step};
    }
  }

  /**
   * Extracts value from a function parameter description.
   * @param state current state of the generation process
   * @param position position in the source code
   * @param param
   */
  private getFuncParam(position: Position, state: State, param: FuncParam): Var {
    if(param.type === 'nb' || param.type === 'str') return param.value;
    if(param.type === 'lit') switch(param.value) {
      case 'true': return true;
      case 'false': return false;
      case 'null': return null;
      case 'pi': return Math.PI;
      case 'e': return Math.E;
      case '{}': return {};
      case '[]': return [];
    }
    if(param.type === 'gv' || param.type === 'lv') {
      const type = this.getRefVar(state, param);
      if(type.value === undefined) this.throwNotExists(position, type.field);
      return type.value as any;
    }
    return null;
  }

  private throwUnexpectedType(position: Position, actualType: VariableType|null, expectedTypes: VariableTypes|VariableTypes[]) {
    const expectedType = Array.isArray(expectedTypes) ? expectedTypes.join('|') : expectedTypes;
    const msg = actualType ?
      `Expected type "${expectedType}" but got "${actualType.type}"` :
      `Expected type "${expectedType}"`;
    this.throwError(position, msg);
  }

  private throwNotExists(position: Position, variableName: string, index?: string|number) {
    if(index === undefined) this.throwError(position, `"${variableName} doesn't exists`)
    else this.throwError(position, `field "${index}" doesn't exists in "${variableName}"`)
  }

  private throwError(position: Position, message: string) {
    const msg = `line ${position.line} col ${position.col}: ${message}`;
    throw new GeneratorError(msg);
  }
}
