package be.technobel.playzone.pl.models.dto

import be.technobel.playzone.dal.models.entities.DataHeader
import be.technobel.playzone.dal.models.entities.DataTable
import be.technobel.playzone.dal.models.entities.SchemaHeader
import be.technobel.playzone.dal.models.entities.SchemaTable
import be.technobel.playzone.dal.repositories.HistoryLogins

data class SchemaTableDTO (
	val id: String,
	val table: String,
	val fact: Boolean,
	val headers: List<SchemaHeaderDTO>,
	val coord: CoordDTO,
	val log: HistoryLogDTO,
)
data class SchemaHeaderDTO(
	val id: String,
	val name: String,
	val pk: Boolean,
	val fk: SchemaHeaderFkDTO?,
)
data class SchemaHeaderFkDTO(val table: String, val field: String)
data class CoordDTO(val x: Int, val y: Int)

fun SchemaTable.toDTO(
	uidFromBin: (ByteArray) -> String,
	getLogIds: (Long,Long) -> HistoryLogins,
	getTable: (SchemaTable) -> DataTable,
	getHeaders: (SchemaTable) -> List<Pair<SchemaHeader, DataHeader>>,
	fkToUid: (Long, Long) -> Pair<ByteArray, ByteArray>?,
):SchemaTableDTO {
	val table = getTable(this)
	return SchemaTableDTO(
		id = uidFromBin(table.uid),
		table = table.name,
		fact = this.fact,
		headers = getHeaders(this).map { it.first.toDTO(uidFromBin, fkToUid, it.second) },
		coord = CoordDTO(this.coordX, this.coordY),
		log = this.log.toDTO(getLogIds),
	)
}

fun SchemaHeader.toDTO(
	uidFromBin: (ByteArray) -> String,
	fkToUid: (Long, Long) -> Pair<ByteArray, ByteArray>?,
	header: DataHeader
): SchemaHeaderDTO {
	return SchemaHeaderDTO(
		id = uidFromBin(header.uid),
		name = header.name,
		pk = this.isPK,
		fk =
			if(this.fkTable == null || this.fkField == null) null
			else {
				val fk = fkToUid(this.fkTable!!, this.fkField!!)
				if(fk == null) null else SchemaHeaderFkDTO(
					table = uidFromBin(fk.first),
					field = uidFromBin(fk.second),
				)
			},
	)
}
