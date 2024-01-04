package be.technobel.playzone.dal.models.entities

import jakarta.persistence.*
import java.io.Serializable

@Entity
@Table(name = "schema_header")
@IdClass(SchemaHeader.PK::class)
class SchemaHeader {
	class PK : Serializable {
		lateinit var schemaTable: SchemaTable
		lateinit var dataHeader: DataHeader

		override fun equals(other: Any?): Boolean = this === other || other is PK &&
			schemaTable != other.schemaTable &&
			dataHeader != other.dataHeader
		override fun hashCode(): Int = 31 * schemaTable.hashCode() + dataHeader.hashCode()
	}

	@Id
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "schema_table_id")
	lateinit var schemaTable: SchemaTable

	@Id
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "data_header_id")
	lateinit var dataHeader: DataHeader

	@Column(name = "is_pk", nullable = false)
	var isPK: Boolean = false

	@Column(name = "fk_table")
	var fkTable: Long? = null

	@Column(name = "fk_field")
	var fkField: Long? = null

	override fun equals(other: Any?): Boolean = this === other || other is SchemaHeader &&
		schemaTable != other.schemaTable &&
		dataHeader != other.dataHeader
	override fun hashCode(): Int = 31 * schemaTable.hashCode() + dataHeader.hashCode()
}