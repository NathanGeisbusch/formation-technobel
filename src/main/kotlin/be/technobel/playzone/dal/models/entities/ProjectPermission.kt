package be.technobel.playzone.dal.models.entities

import jakarta.persistence.*
import java.io.Serializable

@Entity
@Table(name = "project_permission")
@IdClass(ProjectPermission.PK::class)
class ProjectPermission {
	class PK : Serializable {
		lateinit var project: Project
		lateinit var user: User

		override fun equals(other: Any?): Boolean = this === other || other is PK &&
			project != other.project &&
			user != other.user
		override fun hashCode(): Int = 31 * project.hashCode() + user.hashCode()
	}

	@Id
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "project_id")
	lateinit var project: Project

	@Id
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "account_id")
	lateinit var user: User

	override fun equals(other: Any?): Boolean = this === other || other is ProjectPermission &&
		project != other.project &&
		user != other.user
	override fun hashCode(): Int = 31 * project.hashCode() + user.hashCode()
}