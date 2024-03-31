package be.technobel.parsemaster.dto;

import be.technobel.parsemaster.enumeration.PackageVisibility;
import be.technobel.parsemaster.enumeration.ParserSyntax;
import be.technobel.parsemaster.openapi.Examples;
import io.swagger.v3.oas.annotations.media.Schema;

public record ParserEditDTO(
  @Schema(example = Examples.PARSER_NAME) String name,
  @Schema(example = Examples.VERSION) String version,
  @Schema(example = Examples.DESCRIPTION) String description,
  @Schema(example = Examples.PARSER_SYNTAX) ParserSyntax syntax,
  @Schema(example = Examples.VISIBILITY) PackageVisibility visibility,
  String password
) {}
