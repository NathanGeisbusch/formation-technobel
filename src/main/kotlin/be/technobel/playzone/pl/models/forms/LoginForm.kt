package be.technobel.playzone.pl.models.forms

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

data class LoginForm (
	@field:NotNull(message = "required")
	@field:NotBlank(message = "not_blank")
	val login: String? = null,

	@field:NotNull(message = "required")
	@field:NotBlank(message = "not_blank")
	val password: String? = null,
)