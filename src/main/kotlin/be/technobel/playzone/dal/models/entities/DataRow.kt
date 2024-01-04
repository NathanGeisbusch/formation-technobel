package be.technobel.playzone.dal.models.entities

import jakarta.persistence.*

@Entity
@Table(name = "data_row")
class DataRow : Base() {
	@Column(name="uid", nullable = false, unique = true)
	lateinit var uid: ByteArray

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "data_table_id")
	lateinit var dataTable: DataTable

	@Embedded
	lateinit var log: HistoryLog
}