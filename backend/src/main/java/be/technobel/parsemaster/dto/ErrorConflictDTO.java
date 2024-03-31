package be.technobel.parsemaster.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record ErrorConflictDTO(
  @Schema(example = "409") int status,
  @Schema(example = "/auth/sign-in") String path,
  @Schema(example = "constraint_failed") String error
) {}
