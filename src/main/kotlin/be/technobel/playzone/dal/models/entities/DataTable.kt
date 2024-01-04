package be.technobel.playzone.dal.models.entities

import jakarta.persistence.*

@Entity
@Table(name = "data_table")
class DataTable : Base() {
	@Column(name="uid", nullable = false, unique = true)
	lateinit var uid: ByteArray

	@Column(name = "name", nullable = false)
	lateinit var name: String

	@Embedded
	lateinit var log: HistoryLog
}