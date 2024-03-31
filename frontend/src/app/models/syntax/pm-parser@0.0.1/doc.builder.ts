export const docBuilder: string = `
# Builder documentation

## Description

A builder constructs a data tree by following the instructions
associated with the matched token.

## Syntax

    [token] ("[field]":[op]) (-)

A token represent a matched piece of text.
It is a lowercase word composed of alphanumeric characters (a-z, 0-9, _).

Multiple operations can be associated with a token.
- init operations assign literal values to a field.
  - "field":5
  - "field":"text"
  - "field":[]
  - "field":{}
  - "field":false "field":true "field":null
  - "field":offset "field":line "field":col
    - position in the input text file.
- assign operations assign the matched text to a field.
  - "field":nb= "field":nb[]=
  - "field":str= "field":str[]=
  - "field":lstr= "field":lstr[]=
    - "lstr" means "literal string", escapes and assigns the string.
  - "field":bool[]= "field":null[]=
- stack operations push a map field to stack, or pop the stack.
  - "field":map+
    - push the map field to stack.
  - "field":map[]+
    - creates a new map, push it to the field array, also push it to the stack.
  - \\-\\-\\-
    - each character "-" pops the stack one time ("---" pops the stack three times).

## Example

    class_name "class-list":map[]+ "name":str=
    eol -
`;
