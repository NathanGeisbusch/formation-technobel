package be.technobel.playzone.dal.models.entities

import jakarta.persistence.*

@Entity
@Table(name = "schema_table")
class SchemaTable : Base() {
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "project_id")
	lateinit var project: Project

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "data_table_id")
	lateinit var dataTable: DataTable

	@Column(name = "fact", nullable = false)
	var fact: Boolean = false

	@Column(name = "coord_x", nullable = false)
	var coordX: Int = 0

	@Column(name = "coord_y", nullable = false)
	var coordY: Int = 0

	@Embedded
	lateinit var log: HistoryLog
}