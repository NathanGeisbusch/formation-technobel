package be.technobel.playzone.dal.models.entities

import jakarta.persistence.*
import java.time.LocalDateTime

@Embeddable
class HistoryLog {
	@Column(name = "created_at", nullable = false)
	lateinit var createdAt: LocalDateTime

	@Column(name = "updated_at", nullable = false)
	lateinit var updatedAt: LocalDateTime

	@Column(name = "deleted_at")
	var deletedAt: LocalDateTime? = null

	@JoinColumn(name = "created_by", nullable = false)
	var createdBy: Long = 0

	@JoinColumn(name = "updated_by", nullable = false)
	var updatedBy: Long = 0

	@JoinColumn(name = "deleted_by")
	var deletedBy: Long? = null
}