package be.technobel.playzone.pl.models.forms

import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size

typealias DataRowForm = Map<String, String>

data class DataRowFormUpdates(
	@field:NotNull(message = "required")
	@field:NotBlank(message = "not_blank")
	@field:Size(max = 250, message = "size_250")
	val id: String?,

	@field:Valid
	@field:NotNull(message = "required")
	val values: Map<String, String>?,
)
