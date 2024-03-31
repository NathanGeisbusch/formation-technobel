package be.technobel.parsemaster.dto;

import be.technobel.parsemaster.openapi.Examples;
import io.swagger.v3.oas.annotations.media.Schema;

public record AccountDTO(
  @Schema(example = Examples.PSEUDONYM) String pseudonym,
  @Schema(example = Examples.EMAIL) String email
) {}
