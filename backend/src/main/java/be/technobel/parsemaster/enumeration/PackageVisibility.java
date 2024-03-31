package be.technobel.parsemaster.enumeration;

import be.technobel.parsemaster.exception.Exceptions;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum PackageVisibility {
  PUBLIC("public"),
  PRIVATE("private"),
  PROTECTED("protected");

  private final String value;

  PackageVisibility(String value) {
    this.value = value;
  }

  @JsonValue
  public String toString() {
    return value;
  }

  @JsonCreator
  public static PackageVisibility fromString(String value) {
    for(final var e : PackageVisibility.values()) if(e.value.equals(value)) return e;
    throw Exceptions.BAD_ENUM.create();
  }
}
