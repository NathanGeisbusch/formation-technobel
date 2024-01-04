package be.technobel.playzone.pl.models.dto

import be.technobel.playzone.dal.models.entities.DataRow
import be.technobel.playzone.dal.repositories.HistoryLogins

data class DataRowDTO(
	val id: String,
	val values: Map<String, String>,
	val log: HistoryLogDTO,
)

fun DataRow.toDTO(
	uidFromBin: (ByteArray) -> String,
	getLogIds: (Long,Long) -> HistoryLogins,
	getFields: (DataRow) -> Map<String, String>,
) = DataRowDTO(
	id = uidFromBin(this.uid),
	values = getFields(this),
	log = this.log.toDTO(getLogIds),
)