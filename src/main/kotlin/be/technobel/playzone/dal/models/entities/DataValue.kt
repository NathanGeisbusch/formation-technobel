package be.technobel.playzone.dal.models.entities

import jakarta.persistence.*
import java.io.Serializable

@Entity
@Table(name = "data_value")
@IdClass(DataValue.PK::class)
class DataValue {
	class PK : Serializable {
		lateinit var dataRow: DataRow
		lateinit var dataHeader: DataHeader

		override fun equals(other: Any?): Boolean = this === other || other is PK &&
			dataRow != other.dataRow &&
			dataHeader != other.dataHeader
		override fun hashCode(): Int = 31 * dataRow.hashCode() + dataHeader.hashCode()
	}

	@Id
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "data_row_id")
	lateinit var dataRow: DataRow

	@Id
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "data_header_id")
	lateinit var dataHeader: DataHeader

	@Column(name = "field_value")
	lateinit var value: String

	override fun equals(other: Any?): Boolean = this === other || other is DataValue &&
		dataRow != other.dataRow &&
		dataHeader != other.dataHeader
	override fun hashCode(): Int = 31 * dataRow.hashCode() + dataHeader.hashCode()
}