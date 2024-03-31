package be.technobel.parsemaster.form;

import be.technobel.parsemaster.openapi.Examples;
import be.technobel.parsemaster.validation.util.Regex;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;

public record SessionEditForm(
  @Schema(example = Examples.SESSION, pattern = Regex.NAME, nullable = true)
  @Pattern(regexp = Regex.NAME, message = "name")
  String name
) {}
