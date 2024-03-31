package be.technobel.parsemaster.enumeration;

import be.technobel.parsemaster.exception.Exceptions;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import java.util.Set;

public enum UserRole {
  USER("user");

  private final String value;

  UserRole(String value) {
    this.value = value;
    this.authorities = Set.of(new SimpleGrantedAuthority(getAuthority()));
  }

  @JsonValue
  public String toString() {
    return value;
  }

  @JsonCreator
  public static UserRole fromString(String value) {
    for(final var e : UserRole.values()) if(e.value.equals(value)) return e;
    throw Exceptions.BAD_ENUM.create();
  }

  public final Set<SimpleGrantedAuthority> authorities;

  public String getAuthority() {
    return "ROLE_"+this.name();
  }
}
