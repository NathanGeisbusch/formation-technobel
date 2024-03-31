package be.technobel.parsemaster.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record CreatedDTO(
  @Schema(example = "1") String id
) {}
