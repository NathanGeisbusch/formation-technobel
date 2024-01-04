package be.technobel.playzone.dal.models.entities

import jakarta.persistence.Column
import jakarta.persistence.Embedded
import jakarta.persistence.Entity
import jakarta.persistence.Table

@Entity
@Table(name = "project")
class Project : Base() {
	@Column(name="uid", nullable = false, unique = true)
	lateinit var uid: ByteArray

	@Column(name = "name", nullable = false)
	lateinit var name: String

	@Column(name = "description", nullable = false)
	lateinit var description: String

	@Column(name = "is_public", nullable = false)
	var isPublic: Boolean = false

	@Embedded
	lateinit var log: HistoryLog
}