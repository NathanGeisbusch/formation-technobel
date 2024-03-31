package be.technobel.parsemaster.form;

import be.technobel.parsemaster.openapi.Examples;
import be.technobel.parsemaster.validation.util.Regex;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RequestPasswordForm(
  @Schema(example = Examples.EMAIL, maxLength = 255, minLength = 1)
  @Size(max = 255, message = "length>255")
  @Pattern(regexp = Regex.EMAIL, message = "email")
  String email
) {}
