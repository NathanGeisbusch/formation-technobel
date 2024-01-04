package be.technobel.playzone.pl.models.forms

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

data class ChangePasswordForm (
	@field:NotNull(message = "required")
	@field:NotBlank(message = "not_blank")
	val password: String? = null,
)