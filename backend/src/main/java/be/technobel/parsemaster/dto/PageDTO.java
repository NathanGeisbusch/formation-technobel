package be.technobel.parsemaster.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.data.domain.Page;

import java.util.List;

public record PageDTO<T>(
  @Schema(example = "30") long elements,
  @Schema(example = "1") int pages,
  @Schema(example = "0") int page,
  @Schema(example = "10") int size,
  List<T> data
) {
  public PageDTO(Page<T> page) {
    this(
      page.getTotalElements(),
      page.getTotalPages(),
      page.getNumber(),
      page.getSize(),
      page.getContent()
    );
  }
}
