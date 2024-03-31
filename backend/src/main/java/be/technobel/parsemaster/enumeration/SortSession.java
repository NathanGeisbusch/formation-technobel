package be.technobel.parsemaster.enumeration;

import be.technobel.parsemaster.exception.Exceptions;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public enum SortSession {
  NAME_ASC("name,asc"),
  NAME_DSC("name,dsc"),
  UPDATE_ASC("update,asc"),
  UPDATE_DSC("update,dsc");

  private final String value;

  SortSession(String value) {
    this.value = value;
  }

  @JsonValue
  public String toString() {
    return value;
  }

  @JsonCreator
  public static SortSession fromString(String value) {
    for(final var e : SortSession.values()) if(e.value.equals(value)) return e;
    throw Exceptions.BAD_ENUM.create();
  }

  public static Pageable getPageable(int page, int size, SortSession sort) {
    if(sort == null) sort = SortSession.NAME_ASC;
    return PageRequest.of(page, size, switch(sort) {
      case NAME_ASC -> Sort.by(Sort.Direction.ASC, "relevance")
        .and(Sort.by(Sort.Direction.ASC, "name"));
      case NAME_DSC -> Sort.by(Sort.Direction.ASC, "relevance")
        .and(Sort.by(Sort.Direction.DESC, "name"));
      case UPDATE_ASC -> Sort.by(Sort.Direction.ASC, "updatedAt");
      case UPDATE_DSC -> Sort.by(Sort.Direction.DESC, "updatedAt");
    });
  }
}
