package be.technobel.parsemaster.enumeration;

import be.technobel.parsemaster.exception.Exceptions;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum GeneratorSyntax {
  PM_GENERATOR_0_0_1("pm-generator@0.0.1");

  private final String value;

  GeneratorSyntax(String value) {
    this.value = value;
  }

  @JsonValue
  public String toString() {
    return value;
  }

  @JsonCreator
  public static GeneratorSyntax fromString(String value) {
    for(final var e : GeneratorSyntax.values()) if(e.value.equals(value)) return e;
    throw Exceptions.BAD_ENUM.create();
  }
}
