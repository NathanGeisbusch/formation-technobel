package be.technobel.parsemaster.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record ErrorFieldDTO(
  @Schema(example = "fieldName") String field,
  @Schema(example = "bad_value") String reason
) {}
