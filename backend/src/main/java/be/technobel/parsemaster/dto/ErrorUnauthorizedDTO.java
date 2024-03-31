package be.technobel.parsemaster.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record ErrorUnauthorizedDTO(
  @Schema(example = "401") int status,
  @Schema(example = "/auth/sign-in") String path,
  @Schema(example = "unauthorized") String error
) {}
