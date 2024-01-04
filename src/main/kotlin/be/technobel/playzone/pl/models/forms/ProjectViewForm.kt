package be.technobel.playzone.pl.models.forms

import be.technobel.playzone.dal.models.enums.ChartType
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size

data class ProjectViewForm(
	val chart: ChartType?,

	@field:NotNull(message = "required")
	@field:Valid
	val label: ProjectViewLabelForm?,

	@field:Valid
	val data: ProjectViewDataForm?,
)
data class ProjectViewLabelForm(
	@field:NotNull(message = "required")
	@field:NotBlank(message = "not_blank")
	@field:Size(max = 250, message = "size_250")
	val table: String?,

	@field:NotNull(message = "required")
	@field:NotBlank(message = "not_blank")
	@field:Size(max = 250, message = "size_250")
	val field: String?,
)
data class ProjectViewDataForm(
	@field:NotNull(message = "required")
	@field:NotBlank(message = "not_blank")
	@field:Size(max = 250, message = "size_250")
	val table: String?,

	@field:NotNull(message = "required")
	@field:NotBlank(message = "not_blank")
	@field:Size(max = 250, message = "size_250")
	val field: String?,

	@field:Size(max = 250, message = "size_250")
	val value: String?,

	@field:Size(max = 250, message = "size_250")
	val pkValue: String?,
)

data class ChartRequest(
	val chart: ChartType?,
	val label: TableHeaderUid,
	val data: TableHeaderUid?,
	val value: String?,
	val pkValue: String?,
)

class TableHeaderUid(
	val tableUid: ByteArray,
	val headerUid: ByteArray,
)

fun ProjectViewForm.toChartRequest(binFromUid: (String) -> ByteArray): ChartRequest = ChartRequest(
	chart = chart,
	label = TableHeaderUid(binFromUid(label!!.table!!), binFromUid(label.field!!)),
	data =
		if(data == null) null
		else TableHeaderUid(binFromUid(data.table!!), binFromUid(data.field!!)),
	value = data?.value,
	pkValue = data?.pkValue,
)
