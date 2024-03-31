import {CodeError, CodeErrorLine} from "../utils";

export type RefVar = {type: 'gv'|'lv', value: string};
export type StrVar = {type: 'str', value: string};
export type NbVar = {type: 'nb', value: number};
export type LitVar = {type: 'lit', value: '{}'|'[]'|'false'|'true'|'null'|'pi'|'e'}
export type NullBoolVar = {type: 'lit', value: 'false'|'true'|'null'}
export type BoolVar = {type: 'bool', value: 'false'|'true'}
export type RangeVar = {type: 'range', inclusive: 'false'|'true', min: RefVar|NbVar, max: RefVar|NbVar, step: RefVar|NbVar}
export type FuncParam = RefVar|NbVar|StrVar|LitVar;
export type FuncParams = FuncParam[];
export type Position = {offset: number, line: number, col: number};

export type GeneratorFunc = Position & {
  name: string, param: string[],
  op: (Position & (
    {name: 'getch', from: RefVar|StrVar, index: RefVar|NbVar, assign: RefVar} |
    {name: 'get'|'has', from: RefVar, index: RefVar|StrVar|NbVar, assign: RefVar} |
    {name: 'set', to: RefVar, val: FuncParam} |
    {name: 'fopen', filename: RefVar|StrVar, type?: RefVar|StrVar, assign: RefVar} |
    {name: 'fwrite', to: RefVar, val: RefVar|StrVar|NbVar} |
    {name: 'panic', msg: RefVar|StrVar, src_line?: RefVar, src_col?: RefVar} |
    {name: 'add'|'sub'|'mul'|'div'|'mod'|'pow', val: (RefVar|NbVar)[], assign: RefVar} |
    {
      name: 'sqrt'|'cbrt'|'sin'|'cos'|'tan'|'sinh'|'cosh'|'tanh'|'asin'|'acos'|'atan'|'asinh'|'acosh'
        |'atanh'|'abs'|'ceil'|'floor'|'trunc'|'round'|'exp'|'expm1'|'log'|'log10'|'log1p'|'log2',
      val: RefVar|NbVar, amount: undefined, assign: RefVar
    } |
    {name: 'round', val: RefVar|NbVar, amount: RefVar|NbVar, assign: RefVar} |
    {name: 'atan2', val1: RefVar|NbVar, val2: RefVar|NbVar, assign: RefVar} |
    {name: 'hypot', val: (RefVar|NbVar)[], assign: RefVar} |
    {name: 'min'|'max'|'lt'|'lte'|'gt'|'gte', val: (RefVar|NbVar)[], assign: RefVar} |
    {name: 'eq'|'ne', val: (FuncParam)[], assign: RefVar} |
    {name: 'xor', val: (RefVar|NbVar)[], assign: RefVar} |
    {name: 'or'|'and', val: (RefVar|NbVar|BoolVar)[], assign: RefVar} |
    {name: 'lshift'|'rshift', from: RefVar|NbVar, index: RefVar|NbVar, assign: RefVar} |
    {name: 'not', from: RefVar|NbVar, assign: RefVar} |
    {name: 'nan'|'len'|'cp'|'dcp', from: RefVar, assign: RefVar} |
    {
      name: '_int'|'_nb'|'_str'|'_char'|'_date'|'_b64'|'b64_'|'_b64url'|'b64url_'|'json_'|
        '_scase'|'_uscase'|'_ccase'|'_uccase'|'_kcase'|'_ukcase'|'_lcase'|'_ucase'|'_tcase',
      from: RefVar, assign: RefVar
    } |
    {name: '_json', from: RefVar, indent: NbVar|StrVar|null, assign: RefVar} |
    {name: 'rand', min: RefVar|NbVar, max: RefVar|NbVar, assign: RefVar} |
    {name: 'loop', from: RefVar|RangeVar, fct: string, param: FuncParams, assign?: RefVar} |
    {name: 'switch', val: RefVar, cmp: {value: NbVar|StrVar|NullBoolVar, fct: string}[]} |
    {name: 'if', from: RefVar, action: 'continue'} |
    {name: 'if', from: RefVar, action: 'break', return?: FuncParam} |
    {name: 'if', from: RefVar, action: 'return', return?: FuncParam} |
    {name: 'if', from: RefVar, action: 'call', fct: string, param: FuncParams, assign?: RefVar} |
    {name: 'call', fct: string, param: FuncParams, assign?: RefVar} |
    {name: 'return', val: FuncParam} |
    {name: 'concat', param: (RefVar|StrVar|NbVar)[], assign: RefVar} |
    {name: 'mset', from: RefVar, fields: {name: string, value: FuncParam}[]} |
    {name: 'split'|'match', from: RefVar|StrVar, regex: RefVar|StrVar, assign: RefVar} |
    {name: 'replace', from: RefVar|StrVar, regex: RefVar|StrVar, to: RefVar|StrVar, assign: RefVar} |
    {name: 'lpad'|'rpad', from: RefVar|NbVar|StrVar, amount: RefVar|NbVar, val: RefVar|NbVar|StrVar, assign: RefVar} |
    {name: 'slice', from: RefVar|StrVar, min: RefVar|NbVar, max?: RefVar|NbVar, assign: RefVar} |
    {name: 'linit', from: RefVar, param: (FuncParam)[]} |
    {name: 'lfill', from: RefVar, range: undefined, amount: RefVar|NbVar, val: FuncParam} |
    {name: 'lfill', from: RefVar, range: RangeVar} |
    {name: 'lcat', param: RefVar[], assign: RefVar} |
    {name: 'lpush', from: RefVar, val: FuncParam} |
    {name: 'lpop', from: RefVar, val?: FuncParam} |
    {name: 'ljoin', from: RefVar, val: RefVar|StrVar, assign: RefVar} |
    {name: 'ldel', from: RefVar, index: RefVar|NbVar} |
    {name: 'lset'|'lins', from: RefVar, index: RefVar|NbVar, val: FuncParam} |
    {name: 'lrev', from: RefVar} |
    {name: 'lsort', from: RefVar, order: 'asc'|'desc'} |
    {name: 'lsortby', from: RefVar, order: 'asc'|'desc', index: RefVar|NbVar|StrVar} |
    {name: 'lfind', from: RefVar, val: FuncParam, assign: RefVar} |
    {name: 'lfindby', from: RefVar, val: FuncParam, index: RefVar|NbVar|StrVar, assign: RefVar} |
    {name: 'date', now: true, assign: RefVar} |
    {
      name: 'date', now: undefined, assign: RefVar,
      year: RefVar|NbVar, month: RefVar|NbVar, day: RefVar|NbVar,
      hour?: RefVar|NbVar, min?: RefVar|NbVar, sec?: RefVar|NbVar, ms?: RefVar|NbVar,
    } |
    {name: 'dset'|'dadd', from: RefVar, field: 'year'|'month'|'day'|'hour'|'min'|'sec'|'ms', val: RefVar|NbVar} |
    {name: 'dget', from: RefVar, field: 'year'|'month'|'day'|'hour'|'min'|'sec'|'ms', assign: RefVar} |
    {name: 'dsub', from: RefVar, val: RefVar, assign: RefVar}
  ))[],
}

export type OptimizedGeneratorFunctions = {[fct: string]: GeneratorFunc}

/** Converts a list of functions to a map indexed on name in order to improve performance. */
export function optimizeGeneratorFunctions(functions: GeneratorFunc[]): OptimizedGeneratorFunctions {
  const result: OptimizedGeneratorFunctions = {};
  for(const fct of functions) {
    if(result[fct.name]) throw new CodeError('function already exists', fct.offset, fct.name);
    result[fct.name] = fct as any;
  }
  if(!result[':main']) throw new CodeError('main function not found', 0);
  return result;
}

/** Check for language errors like the presence of invalid or recursive calls */
type FuncStackElement = {type: 'call'|'loop', fct: GeneratorFunc}
export function staticAnalysis(functions: OptimizedGeneratorFunctions) {
  checkRecursive(functions, [{type: 'call', fct: functions[':main']}]);
}
function checkRecursive(functions: OptimizedGeneratorFunctions, fctStack: FuncStackElement[]) {
  if(fctStack.length === 0) return;
  if(fctStack.length > 50) throw Error();
  const fct = fctStack.at(-1)!.fct;
  for(const op of fct.op) {
    switch(op.name) {
      case "call":
        throwIfInvalidCall(functions, fctStack, {
          name: op.fct, offset: op.offset, type: 'call', params: op.param,
        });
        fctStack.push({type: 'call', fct: functions[op.fct]});
        checkRecursive(functions, fctStack);
        break;
      case "if":
        if(op.action === 'call') {
          throwIfInvalidCall(functions, fctStack, {
            name: op.fct, offset: op.offset, type: 'call', params: op.param,
          });
          fctStack.push({type: 'call', fct: functions[op.fct]});
          checkRecursive(functions, fctStack);
        } else if(op.action === 'break' || op.action === 'continue') {
          let loopExists = false;
          for(let i = fctStack.length-1; i >= 0; --i) {
            if(fctStack[i].type === 'loop') {
              loopExists = true;
              break;
            }
          }
          if(!loopExists) {
            const trace = fctStack.map(f => f.fct.name).join(' -> ');
            throw new CodeError(
              `no loop in the function call trace: ${trace}`, op.offset, op.name
            );
          }
        }
        break;
      case "switch":
        for(const cmp of op.cmp) {
          throwIfInvalidCall(functions, fctStack, {
            name: cmp.fct, offset: op.offset, type: 'call', params: [],
          });
          fctStack.push({type: 'call', fct: functions[cmp.fct]});
          checkRecursive(functions, fctStack);
        }
        break;
      case "loop":
        throwIfInvalidCall(functions, fctStack, {
          name: op.fct, offset: op.offset, type: 'loop', params: op.param,
        });
        fctStack.push({type: 'loop', fct: functions[op.fct]});
        checkRecursive(functions, fctStack);
        break;
      case "return":
        if(fct.op.indexOf(op) !== fct.op.length-1) throw new CodeError(
          'return must be the last instruction of the function', op.offset, op.name
        );
        break;
    }
  }
  fctStack.pop();
}
type FuncCall = {name: string, offset: number, type: 'call'|'loop', params: FuncParams}
function throwIfInvalidCall(functions: OptimizedGeneratorFunctions, fctStack: FuncStackElement[], fctCall: FuncCall) {
  const fctName = fctCall.name;
  const fct = functions[fctName];
  if(!fct) throw new CodeError('this function doesn\'t exists', fctCall.offset, CodeErrorLine);
  if(fctStack.some(f => f.fct.name === fctName)) {
    throw new CodeError('recursive call is not allowed', fctCall.offset, CodeErrorLine);
  }
  if(fctCall.type === 'call' && fct.param.length !== fctCall.params.length) {
    throw new CodeError('the number of parameters is invalid', fctCall.offset, CodeErrorLine);
  }
  else if(fctCall.type === 'loop' && fct.param.length-2 !== fctCall.params.length) {
    throw new CodeError('the number of parameters is invalid', fctCall.offset, CodeErrorLine);
  }
}
