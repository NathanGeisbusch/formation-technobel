export const docParser: string = `
# Parser documentation

## Description

A parser iterates a text and splits it in multiple pieces.
At each iteration, the parser checks the remaining text
against each user-defined regex until it matches.
The match and the associated token will be passed to the builder
to then be processed.

## Syntax

    [last_token] (, [last_token]) > [current_token] = [regex]
    [last_token] (, [last_token]) > $

A token is a lowercase word composed of alphanumeric characters (a-z, 0-9, _).

- last_token: Represents the token(s) preceding the current token.
Multiple tokens can be provided, there are separated by a colon.
The parser needs a root token represented by an asterisk "*".
- current_token: Represent a token that can be matched.
- regex: The regular expression used to match a piece of text.
- end token: An optional indication that the input text must end with one of the last_token.
It is represented by a dollar "$".

## Example

For example:

    *, eol > class = "class "
    class > class_name = "[a-zA-Z0-9\\\\-_]+"
    class_name > eol = "\\n"
    *, eol, class_name > $

matches the text:

    class Object
    class Test
    class MyClass

## Regex examples

    Positive integer: "([1-9][0-9]+|[0-9])"
    Positive number:  "([1-9][0-9]+|[0-9])(\\\\.[0-9]*[1-9])?"
    Number:           "-?([1-9][0-9]*|[0-9])(\\\\.[0-9]*[1-9])?"
    Literal string:   "\\"(?:\\\\\\\\.|[^\\\\\\\\\\"])*\\""
    Ignore line:      "( *|#.*)\\n"
`;
