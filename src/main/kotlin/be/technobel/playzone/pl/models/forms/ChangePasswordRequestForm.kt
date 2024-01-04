package be.technobel.playzone.pl.models.forms

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

data class ChangePasswordRequestForm (
	@field:NotNull(message = "required")
	@field:NotBlank(message = "not_blank")
	val email: String? = null,
)