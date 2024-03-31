package be.technobel.parsemaster.form;

import be.technobel.parsemaster.openapi.Examples;
import be.technobel.parsemaster.validation.util.Regex;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record SessionCreateForm(
  @Schema(example = Examples.SESSION, pattern = Regex.NAME)
  @NotNull(message = "null")
  @Pattern(regexp = Regex.NAME, message = "name")
  String name,

  @Schema(example = Examples.GENERATOR, pattern = Regex.PACKAGE_ID)
  @NotNull(message = "null")
  @Pattern(regexp = Regex.PACKAGE_ID, message = "package_id")
  String from
) {}
