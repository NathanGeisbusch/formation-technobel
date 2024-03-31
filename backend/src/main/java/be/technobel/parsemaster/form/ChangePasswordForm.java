package be.technobel.parsemaster.form;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChangePasswordForm(
  @Schema(maxLength = 255, minLength = 1)
  @Size(max = 255, message = "length>255")
  @NotBlank(message = "blank")
  String password
) {}
