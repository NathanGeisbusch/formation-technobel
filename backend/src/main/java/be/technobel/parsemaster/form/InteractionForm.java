package be.technobel.parsemaster.form;

import io.swagger.v3.oas.annotations.media.Schema;

public record InteractionForm(
  @Schema(example = "true", nullable = true)
  Boolean liked,

  @Schema(example = "true", nullable = true)
  Boolean bookmarked
) {}
