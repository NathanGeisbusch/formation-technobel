export const docGenerator: string = `
# Generator documentation

## Description

A generator is a set of functions and instructions to generate files
from input data extracted for a text provided by the user.

## Types

- **String**: "text", "escaped\\\\"text\n"
- **Integer**: 5, -7.24
- **Boolean**: true, false
- **Constant**: pi, e
- **No value**: null
- **Map**: {}
- **List**: []
- **Range**: !0..<10!1, !10..0!-1
- **Global variable**: %input, %my_global_var
- **Local variable**: $param1, $value
- **Function**: :fct, :fct $param1 $param2
- And other specials types: **Date**, **File**.

## Example

    :main
    fopen "result.txt" = $file
    get %input "values" = $values
    loop $values :loop_values $file

    :loop_values $i $values $file
    get $values $i = $value
    fwrite $file $value

Every generator code must have a :main function (entry point).

A function and variable name can only contain lowercase ascii letters (a-z),
digits (0-9), and underscores (_).

To create a file, we use the instruction "fopen",
which takes a path by parameter and returns a pointer to the created file.

To write in a file, we use the instruction "fwrite",
which takes the file pointer and a string variables/literal by parameter.

Input data is stored in the global variable %input.
It is a map whose values can be obtained with instruction "get",
which takes a variable and an index by parameter,
and returns the value in a variable.

A global variable exists during the entire lifetime of the program
and is accessible everywhere in the code.
A local variable exists and is accessible only in the scope of a function.

Loops iterates a list variable or a range, and call a function for each value.
The called function must declared at least two parameters (one for the index and one for the array),
which are passed implicitly when called from a loop.
Parameters are declared/passed after the function name.

## Instructions

### File

    fopen "Test.java" = $file
    fwrite $file $result
    panic "division by zero" $line $col

### Variable

    set $value "val"
    set $value 1.23
    set $value true
    set $value pi
    set $value {}
    set $value []

### Text

    getch $str 0 = $char_code
    split $str "[a-z]" = $result
    match $str "[a-z]" = $result
    replace $str "[a-z]" "x" = $result
    replace $str "^ " "" = $trim_start
    replace $str "\\n$" "" = $trim_end
    lpad 2 0
    rpad 2 ""
    concat "Title:\\n" $line "\\n\\nField:\\n" $value = $output

### Collection

    has %input "config" = $test
    get %input "config" = $config
    mset $map "field1":0 "field2":[]
    len $list = $result
    len $str = $result
    linit $arr 1 2 3 4 5 $a $b
    lfill $arr $arr_len {}
    lfill $arr !65..90!1
    lcat $arr1 $arr2 = $result
    lpush $arr "a"
    lpop $arr
    lpop $arr $val1
    ldel $arr 0
    lset $arr 0 "a"
    lins $arr 0 "a"
    lrev $arr
    lsort $bars asc
    lsortby $arr "name" desc
    lfind $arr $val1 = $i
    lfindby $arr "name" $val1 = $i
    ljoin $arr "" = $result
    slice $str 4 = $result
    slice $arr 0 99 = $result
    cp $map = $result
    dcp $map = $deep_copy

### Math

    add 1 2 3 = $result
    sub 1 2 3 = $result
    mul 1 2 3 = $result
    div 1 2 3 = $result
    mod 1 2 3 = $result
    pow 1 2 3 = $result
    sqrt 5 = $result
    cbrt 5 = $result
    sin 5 = $result
    cos 5 = $result
    tan 5 = $result
    sinh 5 = $result
    cosh 5 = $result
    tanh 5 = $result
    asin 5 = $result
    acos 5 = $result
    atan 5 = $result
    asinh 5 = $result
    acosh 5 = $result
    atanh 5 = $result
    atan2 5 2 = $result
    abs 5 = $result
    ceil 5 = $result
    floor 5 = $result
    trunc 5 = $result
    round 5 = $result
    round $price 2 = $result
    exp 5 = $result
    expm1 5 = $result
    log 5 = $result
    log10 5 = $result
    log1p 5 = $result
    log2 5 = $result
    hypot 4 9.2 2.21 = $result
    rand $min $exclusive_max = $result

### Bitwise

    xor 1 2 3 = $result
    and true true false = $result
    and 1 2 3 = $result
    or true true false = $result
    or 1 2 3 = $result
    lshift $val 1 = $result
    rshift $val 1 = $result
    not $bool = $result
    not $int = $result

### Comparison

    min $val 9 5 = $test
    max $val 9 5 = $test
    lt $val 9 5 = $test
    lte $val 9 5 = $test
    gt $val 9 5 = $test
    gte $val 9 5 = $test
    eq $val 9 5 "a" null = $test
    ne $val 5 5 "a" null = $test

### Control flow

    call :fct $param1 "param2" = $result
    if $test :fct $param1 = $result
    if $test continue
    if $test break
    if $test break 5
    if $test return
    if $test return 5
    switch $op "+":add "-":sub "*":mul "/":div
    loop $arr :loop_add
    loop !$start..<$end!$inc :loop_add $arr
    loop !$start..<$end!$inc :loop_add $arr = $result
    return $value

### Conversion

    _int $val = $result
    _nb $val = $result
    _str $val = $result
    _char $val = $result
    _date $val = $result
    _list $val = $result
    _b64 $val = $result
    b64_ $val = $result
    _b64url $val = $result
    b64url_ $val = $result
    json_ $val = $result
    _json $map 2 = $json
    _json $map = $json
    _scase $val = $result
    _uscase $val = $result
    _ccase $val = $result
    _uccase $val = $result
    _kcase $val = $result
    _ukcase $val = $result
    _lcase $val = $result
    _ucase $val = $result
    _tcase $val = $result
    nan $val = $result

### Date

    date now = $date
    date 2020 1 1 = $date
    date 2020 1 1 12 45 0 0 = $date
    dset $date year|month|day|hour|min|sec|ms 2000
    dadd $date year|month|day|hour|min|sec|ms -5
    dget $date year|month|day|hour|min|sec|ms = $result
    dsub $date1 $date2 = $date3

`;
