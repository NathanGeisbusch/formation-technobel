package be.technobel.parsemaster.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

public record ErrorFormDTO(
  @Schema(example = "400") int status,
  @Schema(example = "/auth/sign-in") String path,
  List<ErrorFieldDTO> errors
) {}
