package be.technobel.playzone.pl.models.forms

import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size

data class DataTableFormCreate(
	@field:NotNull(message = "required")
	@field:NotBlank(message = "not_blank")
	@field:Size(max = 250, message = "size_250")
	val table: String?,

	@field:Valid
	@field:NotNull(message = "required")
	val headers: List<DataHeaderFormCreate>?,
)

data class DataHeaderFormCreate(
	@field:NotNull(message = "required")
	@field:NotBlank(message = "not_blank")
	@field:Size(max = 250, message = "size_250")
	val name: String?,
)

data class DataTableFormUpdate(
	@field:NotNull(message = "required")
	@field:NotBlank(message = "not_blank")
	@field:Size(max = 250, message = "size_250")
	val table: String?,

	@field:Valid
	@field:NotNull(message = "required")
	val headers: List<DataHeaderFormUpdate>?,
)

data class DataHeaderFormUpdate(
	@field:NotNull(message = "required")
	@field:NotBlank(message = "not_blank")
	@field:Size(max = 250, message = "size_250")
	val id: String?,

	@field:NotNull(message = "required")
	@field:NotBlank(message = "not_blank")
	@field:Size(max = 250, message = "size_250")
	val name: String?,
)
