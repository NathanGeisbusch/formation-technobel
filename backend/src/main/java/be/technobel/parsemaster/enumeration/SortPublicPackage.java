package be.technobel.parsemaster.enumeration;

import be.technobel.parsemaster.exception.Exceptions;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public enum SortPublicPackage {
  RELEVANCE("relevance"),
  POPULARITY("popularity"),
  UPDATE("update");

  private final String value;

  SortPublicPackage(String value) {
    this.value = value;
  }

  @JsonValue
  public String toString() {
    return value;
  }

  @JsonCreator
  public static SortPublicPackage fromString(String value) {
    for(final var e : SortPublicPackage.values()) if(e.value.equals(value)) return e;
    throw Exceptions.BAD_ENUM.create();
  }

  public static Pageable getPageable(int page, int size, SortPublicPackage sort) {
    if(sort == null) sort = SortPublicPackage.RELEVANCE;
    return PageRequest.of(page, size, switch(sort) {
      case RELEVANCE -> Sort.by(Sort.Direction.DESC, "relevance")
        .and(Sort.by(Sort.Direction.ASC, "info.name"));
      case POPULARITY -> Sort.by(Sort.Direction.DESC, "likes")
        .and(Sort.by(Sort.Direction.ASC, "dislikes"));
      case UPDATE -> Sort.by(Sort.Direction.DESC, "updatedAt");
    });
  }
}
