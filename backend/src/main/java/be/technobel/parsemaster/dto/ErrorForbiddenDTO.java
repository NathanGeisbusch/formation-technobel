package be.technobel.parsemaster.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record ErrorForbiddenDTO(
  @Schema(example = "403") int status,
  @Schema(example = "/auth/sign-in") String path,
  @Schema(example = "forbidden") String error
) {}
