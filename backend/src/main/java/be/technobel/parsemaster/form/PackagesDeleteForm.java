package be.technobel.parsemaster.form;

import be.technobel.parsemaster.openapi.Examples;
import be.technobel.parsemaster.validation.util.Regex;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.util.LinkedHashSet;

public record PackagesDeleteForm(
  @Schema(example = Examples.PARSERS, maxLength = 50)
  @NotNull(message = "null")
  @Size(max = 50, message = "length>50")
  LinkedHashSet<
    @NotNull(message = "null")
    @Pattern(regexp = Regex.PACKAGE_ID, message = "package_id")
    String
  > id
) {}
