package be.technobel.playzone.pl.models.dto

import be.technobel.playzone.dal.models.entities.*
import be.technobel.playzone.dal.repositories.HistoryLogins
import java.time.LocalDateTime

data class HistoryLogDTO (
	val createdAt: LocalDateTime,
	val updatedAt: LocalDateTime,
	val createdBy: String,
	val updatedBy: String,
)

fun HistoryLog.toDTO(getLogIds: (Long,Long) -> HistoryLogins): HistoryLogDTO {
	val (first, second) = getLogIds(this.createdBy, this.updatedBy)
	return HistoryLogDTO(
		createdAt = this.createdAt,
		updatedAt = this.updatedAt,
		createdBy = first,
		updatedBy = second,
	)
}
