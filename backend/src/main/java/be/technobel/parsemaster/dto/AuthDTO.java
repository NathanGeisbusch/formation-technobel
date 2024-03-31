package be.technobel.parsemaster.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import be.technobel.parsemaster.openapi.Examples;

public record AuthDTO(
  @Schema(example = Examples.BEARER) String token
) {}
