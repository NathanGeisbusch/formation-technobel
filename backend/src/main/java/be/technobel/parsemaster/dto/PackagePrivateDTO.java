package be.technobel.parsemaster.dto;

import be.technobel.parsemaster.enumeration.GeneratorSyntax;
import be.technobel.parsemaster.enumeration.PackageVisibility;
import be.technobel.parsemaster.enumeration.ParserSyntax;
import be.technobel.parsemaster.openapi.Examples;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

public record PackagePrivateDTO(
  @Schema(example = Examples.GENERATOR_NAME) String name,
  @Schema(example = Examples.VERSION) String version,
  @Schema(example = Examples.PSEUDONYM) String author,
  @Schema(example = Examples.DESCRIPTION) String description,
  @Schema(example = Examples.DATETIME) LocalDateTime updatedAt,
  @Schema(example = Examples.VISIBILITY) PackageVisibility visibility,
  @Schema(example = Examples.PARSER_SYNTAX) ParserSyntax parserSyntax,
  @Schema(example = Examples.GENERATOR_SYNTAX, nullable = true) GeneratorSyntax generatorSyntax
) {}
