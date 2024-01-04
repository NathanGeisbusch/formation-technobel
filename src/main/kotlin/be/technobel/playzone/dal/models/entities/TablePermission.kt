package be.technobel.playzone.dal.models.entities

import jakarta.persistence.*
import java.io.Serializable

@Entity
@Table(name = "table_permission")
@IdClass(TablePermission.PK::class)
class TablePermission {
	class PK : Serializable {
		lateinit var table: DataTable
		lateinit var user: User

		override fun equals(other: Any?): Boolean = this === other || other is PK &&
			table != other.table &&
			user != other.user
		override fun hashCode(): Int = 31 * table.hashCode() + user.hashCode()
	}

	@Id
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "data_table_id")
	lateinit var table: DataTable

	@Id
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "account_id")
	lateinit var user: User

	override fun equals(other: Any?): Boolean = this === other || other is TablePermission &&
		table != other.table &&
		user != other.user
	override fun hashCode(): Int = 31 * table.hashCode() + user.hashCode()
}