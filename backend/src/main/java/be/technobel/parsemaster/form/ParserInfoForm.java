package be.technobel.parsemaster.form;

import be.technobel.parsemaster.enumeration.PackageVisibility;
import be.technobel.parsemaster.enumeration.ParserSyntax;
import be.technobel.parsemaster.openapi.Examples;
import be.technobel.parsemaster.validation.constraint.NotBlank;
import be.technobel.parsemaster.validation.util.Regex;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ParserInfoForm(
  @Schema(example = Examples.PARSER_NAME, pattern = Regex.NAME, nullable = true)
  @Pattern(regexp = Regex.NAME, message = "name")
  String name,

  @Schema(example = Examples.DESCRIPTION, maxLength = 255, minLength = 1, nullable = true)
  @Size(max = 255, message = "length>255")
  @NotBlank(field = "description")
  String description,

  @Schema(example = Examples.PARSER_SYNTAX, nullable = true)
  ParserSyntax syntax,

  @Schema(example = Examples.VISIBILITY, nullable = true)
  PackageVisibility visibility,

  @Schema(maxLength = 255, minLength = 1, nullable = true)
  @Size(max = 255, message = "length>255")
  @NotBlank(field = "password")
  String password
) {}
