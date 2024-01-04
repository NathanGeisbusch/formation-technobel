package be.technobel.playzone.pl.models.forms

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size

data class ProjectFormUpdate(
	@field:NotNull(message = "required")
	@field:NotBlank(message = "not_blank")
	@field:Size(max = 250, message = "size_250")
	val name: String?,

	@field:NotNull(message = "required")
	@field:NotBlank(message = "not_blank")
	@field:Size(max = 250, message = "size_250")
	val description: String?,

	@field:NotNull(message = "required")
	val isPublic: Boolean?,
)