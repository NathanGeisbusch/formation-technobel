package be.technobel.parsemaster.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record ErrorServerDTO(
  @Schema(example = "500") int status,
  @Schema(example = "/auth/change-password") String path,
  @Schema(example = "email_could_not_be_sent") String error
) {}
