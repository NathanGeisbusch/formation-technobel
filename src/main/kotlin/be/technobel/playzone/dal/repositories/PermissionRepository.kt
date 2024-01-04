package be.technobel.playzone.dal.repositories

import be.technobel.playzone.dal.models.entities.ProjectPermission
import be.technobel.playzone.dal.models.entities.TablePermission
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface ProjectPermissionRepository : JpaRepository<ProjectPermission, Long> {
	@Query("""
		SELECT pp FROM ProjectPermission pp
		WHERE pp.project.uid = :uid
		AND pp.user.email = :otherUserEmail
	""")
	fun findOne(
		uid: ByteArray,
		otherUserEmail: String,
	): ProjectPermission?
}

interface TablePermissionRepository : JpaRepository<TablePermission, Long> {
	@Query("""
		SELECT tp FROM TablePermission tp
		WHERE tp.table.uid = :uid
		AND tp.user.email = :otherUserEmail
	""")
	fun findOne(
		uid: ByteArray,
		otherUserEmail: String,
	): TablePermission?
}
