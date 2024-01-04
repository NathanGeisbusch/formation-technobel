package be.technobel.playzone.pl.models.dto

import be.technobel.playzone.dal.models.entities.DataHeader
import be.technobel.playzone.dal.models.entities.DataTable
import be.technobel.playzone.dal.repositories.HistoryLogins

data class DataTableDTO(
	val id: String,
	val table: String,
	val headers: List<DataHeaderDTO>,
	val log: HistoryLogDTO,
)
data class DataHeaderDTO(
	val id: String,
	val name: String,
)

fun DataTable.toDTO(
	uidFromBin: (ByteArray) -> String,
	getLogIds: (Long,Long) -> HistoryLogins,
	getHeaders: (DataTable) -> List<DataHeader>,
) = DataTableDTO(
	id = uidFromBin(this.uid),
	table = this.name,
	headers = getHeaders(this).map { it.toDTO(uidFromBin) },
	log = this.log.toDTO(getLogIds),
)

fun DataHeader.toDTO(uidFromBin: (ByteArray) -> String) = DataHeaderDTO(
	id = uidFromBin(this.uid),
	name = this.name,
)
