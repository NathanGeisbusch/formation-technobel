package be.technobel.parsemaster.form;

import be.technobel.parsemaster.openapi.Examples;
import be.technobel.parsemaster.validation.util.Regex;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterForm(
  @Schema(example = Examples.EMAIL, maxLength = 255, minLength = 1)
  @NotNull(message = "null")
  @Size(max = 255, message = "length>255")
  @Pattern(regexp = Regex.EMAIL, message = "email")
  String email,

  @Schema(example = Examples.PSEUDONYM, maxLength = 64, minLength = 1)
  @NotNull(message = "null")
  @Pattern(regexp = Regex.NAME, message = "name")
  String pseudonym,

  @Schema(maxLength = 255, minLength = 1)
  @Size(max = 255, message = "length>255")
  @NotBlank(message = "blank")
  String password
) {}
