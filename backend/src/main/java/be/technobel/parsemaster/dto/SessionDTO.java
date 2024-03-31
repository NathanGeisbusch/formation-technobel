package be.technobel.parsemaster.dto;

import be.technobel.parsemaster.enumeration.GeneratorSyntax;
import be.technobel.parsemaster.enumeration.ParserSyntax;
import be.technobel.parsemaster.openapi.Examples;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

public record SessionDTO(
  @Schema(example = Examples.SESSION) String name,
  @Schema(example = Examples.GENERATOR) String generator,
  @Schema(example = Examples.DATETIME) LocalDateTime updatedAt,
  @Schema(example = Examples.PARSER_SYNTAX) ParserSyntax parserSyntax,
  @Schema(example = Examples.GENERATOR_SYNTAX, nullable = true) GeneratorSyntax generatorSyntax
) {}