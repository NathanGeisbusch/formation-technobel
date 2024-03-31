package be.technobel.parsemaster.form;

import be.technobel.parsemaster.openapi.Examples;
import be.technobel.parsemaster.validation.constraint.NotBlank;
import be.technobel.parsemaster.validation.util.Regex;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record PackageCreateForm(
  @Schema(example = Examples.PARSER_NAME, pattern = Regex.NAME)
  @NotNull(message = "null")
  @Pattern(regexp = Regex.NAME, message = "name")
  String name,

  @Schema(example = Examples.PARSER, pattern = Regex.PACKAGE_ID, nullable = true)
  @Pattern(regexp = Regex.PACKAGE_ID, message = "package_id")
  String from,

  @Schema(maxLength = 255, minLength = 1, nullable = true)
  @Size(max = 255, message = "length>255")
  @NotBlank(field = "password")
  String password
) {}
