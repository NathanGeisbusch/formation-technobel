package be.technobel.parsemaster.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record ErrorNotFoundDTO(
  @Schema(example = "404") int status,
  @Schema(example = "/auth/sign-in") String path,
  @Schema(example = "user_not_found") String error
) {}
