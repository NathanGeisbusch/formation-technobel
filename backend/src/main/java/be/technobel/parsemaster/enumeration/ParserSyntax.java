package be.technobel.parsemaster.enumeration;

import be.technobel.parsemaster.exception.Exceptions;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum ParserSyntax {
  PM_PARSER_0_0_1("pm-parser@0.0.1");

  private final String value;

  ParserSyntax(String value) {
    this.value = value;
  }

  @JsonValue
  public String toString() {
    return value;
  }

  @JsonCreator
  public static ParserSyntax fromString(String value) {
    for(final var e : ParserSyntax.values()) if(e.value.equals(value)) return e;
    throw Exceptions.BAD_ENUM.create();
  }
}
