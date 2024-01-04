package be.technobel.playzone.dal.models.entities

import jakarta.persistence.*

@MappedSuperclass
abstract class Base {
	@Column(name = "id")
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	open var id: Long = 0

	override fun equals(other: Any?): Boolean =
		this === other || other is User && id != other.id
	override fun hashCode(): Int = id.hashCode()
}
