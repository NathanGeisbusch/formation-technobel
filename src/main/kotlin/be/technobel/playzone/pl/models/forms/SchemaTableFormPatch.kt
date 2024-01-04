package be.technobel.playzone.pl.models.forms

import jakarta.validation.Valid

data class SchemaTableFormPatch(
	val fact: Boolean?,

	@field:Valid
	val coord: CoordForm?,
)

data class SchemaHeaderFormPatch(
	val pk: Boolean?,

	@field:Valid
	val fk: SchemaHeaderFkForm?,
)
