package be.technobel.playzone.pl.models.forms

import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size

data class SchemaTableForm(
	@field:NotNull(message = "required")
	@field:NotBlank(message = "not_blank")
	@field:Size(max = 250, message = "size_250")
	val id: String?,

	@field:NotNull(message = "required")
	val fact: Boolean?,

	@field:Valid
	@field:NotNull(message = "required")
	val headers: List<SchemaHeaderForm>?,

	@field:Valid
	@field:NotNull(message = "required")
	val coord: CoordForm?,
)

data class SchemaHeaderForm(
	@field:NotNull(message = "required")
	@field:NotBlank(message = "not_blank")
	@field:Size(max = 250, message = "size_250")
	val id: String?,

	@field:NotNull(message = "required")
	val pk: Boolean?,

	@field:Valid
	val fk: SchemaHeaderFkForm?,
)

data class SchemaHeaderFkForm(
	@field:NotNull(message = "required")
	@field:NotBlank(message = "not_blank")
	@field:Size(max = 250, message = "size_250")
	val table: String?,

	@field:NotNull(message = "required")
	@field:NotBlank(message = "not_blank")
	@field:Size(max = 250, message = "size_250")
	val field: String?,
)

data class CoordForm(
	@field:NotNull(message = "required")
	val x: Int?,

	@field:NotNull(message = "required")
	val y: Int?,
)
