package be.technobel.playzone.dal.models.entities

import jakarta.persistence.*

@Entity
@Table(name = "data_header")
class DataHeader : Base() {
	@Column(name="uid", nullable = false, unique = true)
	lateinit var uid: ByteArray

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "data_table_id")
	lateinit var dataTable: DataTable

	@Column(name = "name", nullable = false)
	lateinit var name: String
}