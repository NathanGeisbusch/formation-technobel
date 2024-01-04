package be.technobel.playzone.pl.models.dto

import be.technobel.playzone.dal.models.entities.ProjectView
import be.technobel.playzone.dal.models.enums.ChartType
import be.technobel.playzone.dal.repositories.HistoryLogins

data class ProjectViewDTO(
	val id: String,
	val chart: ChartType?,
	val label: ProjectViewLabelDTO,
	val data: ProjectViewDataDTO?,
	val log: HistoryLogDTO,
)

fun ProjectView.toDTO(
	uidFromBin: (ByteArray) -> String,
	getLogIds: (Long,Long) -> HistoryLogins,
) = ProjectViewDTO(
	id = uidFromBin(uid),
	chart = chartType,
	label = ProjectViewLabelDTO(uidFromBin(labelTableUid), uidFromBin(labelFieldUid)),
	data =
		if(dataTableUid == null || dataFieldUid == null) null
		else ProjectViewDataDTO(uidFromBin(dataTableUid!!), uidFromBin(dataFieldUid!!), value, pkValue),
	log = log.toDTO(getLogIds),
)

data class ProjectViewLabelDTO(val table: String, val field: String)
data class ProjectViewDataDTO(val table: String, val field: String, val value: String?, val pkValue: String?)
