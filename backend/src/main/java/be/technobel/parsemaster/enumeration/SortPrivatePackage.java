package be.technobel.parsemaster.enumeration;

import be.technobel.parsemaster.exception.Exceptions;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public enum SortPrivatePackage {
  NAME_ASC("name,asc"),
  NAME_DSC("name,dsc"),
  VERSION_ASC("version,asc"),
  VERSION_DSC("version,dsc"),
  UPDATE_ASC("update,asc"),
  UPDATE_DSC("update,dsc");

  private final String value;

  SortPrivatePackage(String value) {
    this.value = value;
  }

  @JsonValue
  public String toString() {
    return value;
  }

  @JsonCreator
  public static SortPrivatePackage fromString(String value) {
    for(final var e : SortPrivatePackage.values()) if(e.value.equals(value)) return e;
    throw Exceptions.BAD_ENUM.create();
  }

  public static Pageable getPageable(int page, int size, SortPrivatePackage sort) {
    if(sort == null) sort = SortPrivatePackage.NAME_ASC;
    return PageRequest.of(page, size, switch(sort) {
      case NAME_ASC -> Sort.by(Sort.Direction.DESC, "relevance")
        .and(Sort.by(Sort.Direction.ASC, "info.name"));
      case NAME_DSC -> Sort.by(Sort.Direction.DESC, "relevance")
        .and(Sort.by(Sort.Direction.DESC, "info.name"));
      case VERSION_ASC -> Sort.by(Sort.Direction.ASC, "version");
      case VERSION_DSC -> Sort.by(Sort.Direction.DESC, "version");
      case UPDATE_ASC -> Sort.by(Sort.Direction.ASC, "updatedAt");
      case UPDATE_DSC -> Sort.by(Sort.Direction.DESC, "updatedAt");
    });
  }
}
