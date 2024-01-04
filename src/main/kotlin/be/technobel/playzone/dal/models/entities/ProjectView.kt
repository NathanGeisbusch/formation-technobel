package be.technobel.playzone.dal.models.entities

import be.technobel.playzone.dal.models.enums.ChartType
import jakarta.persistence.*

@Entity
@Table(name = "project_view")
class ProjectView : Base() {
	@Column(name="uid", nullable = false, unique = true)
	lateinit var uid: ByteArray

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "project_id")
	lateinit var project: Project

	@Column(name = "chart_type")
	@Enumerated(value = EnumType.STRING)
	var chartType: ChartType? = null

	@Column(name = "label_table_uid", nullable = false)
	lateinit var labelTableUid: ByteArray

	@Column(name = "label_field_uid", nullable = false)
	lateinit var labelFieldUid: ByteArray

	@Column(name = "data_table_uid")
	var dataTableUid: ByteArray? = null

	@Column(name = "data_field_uid")
	var dataFieldUid: ByteArray? = null

	@Column(name = "pk_value")
	var pkValue: String? = null

	@Column(name = "data_value")
	var value: String? = null

	@Embedded
	lateinit var log: HistoryLog
}