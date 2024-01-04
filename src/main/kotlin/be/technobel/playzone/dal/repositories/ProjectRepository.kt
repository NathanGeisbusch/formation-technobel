package be.technobel.playzone.dal.repositories

import be.technobel.playzone.dal.models.entities.Project
import be.technobel.playzone.dal.models.enums.UserRole
import jakarta.transaction.Transactional
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query

interface ProjectRepository : JpaRepository<Project, Long> {
	@Query("""
		SELECT p FROM Project p
		WHERE p.log.deletedAt IS NULL
	""")
	fun find(pageable: Pageable): Page<Project>

	@Query("""
		SELECT p FROM Project p
		INNER JOIN User u ON p.log.createdBy = u.id
		WHERE p.log.deletedAt IS NULL
		AND u.email = :currentUserEmail
	""")
	fun findOwn(pageable: Pageable, currentUserEmail: String): Page<Project>

	@Query("""
		SELECT p FROM Project p
		INNER JOIN ProjectPermission pp ON pp.project = p
		INNER JOIN User u ON pp.user = u
		WHERE p.log.deletedAt IS NULL
		AND u.email = :currentUserEmail
	""")
	fun findShared(pageable: Pageable, currentUserEmail: String): Page<Project>

	@Query("""
		SELECT p FROM Project p
		WHERE p.isPublic AND p.log.deletedAt IS NULL
	""")
	fun findPublic(pageable: Pageable): Page<Project>

	@Transactional
	@Modifying
	@Query("""
		UPDATE Project p SET p.log.deletedAt = CURRENT_DATE, p.log.deletedBy = :currentUserId
		WHERE p.uid = :uid AND p.log.deletedAt IS NULL
	""")
	fun deleteByUid(uid: ByteArray, currentUserId: Long)

	@Query("""
		SELECT DISTINCT p FROM Project p
		LEFT JOIN ProjectPermission pp ON pp.project = p
		INNER JOIN User u ON (
			u.role = :admin
			OR p.log.createdBy = u.id
			OR pp.user = u
			OR p.isPublic
		)
		WHERE p.uid = :uid AND p.log.deletedAt IS NULL
		AND u.email = :currentUserEmail
	""")
	fun findByUidIfPermissionOrPublic(
		uid: ByteArray,
		currentUserEmail: String,
		admin: UserRole = UserRole.ADMIN,
	): Project?

	@Query("""
		SELECT DISTINCT p FROM Project p
		LEFT JOIN ProjectPermission pp ON pp.project = p
		INNER JOIN User u ON (
			u.role = :admin
			OR p.log.createdBy = u.id
			OR pp.user = u
		)
		WHERE p.uid = :uid AND p.log.deletedAt IS NULL
		AND u.email = :currentUserEmail
	""")
	fun findByUidIfPermission(
		uid: ByteArray,
		currentUserEmail: String,
		admin: UserRole = UserRole.ADMIN,
	): Project?

	@Query("""
		SELECT DISTINCT p.id FROM Project p
		LEFT JOIN ProjectPermission pp ON pp.project = p
		INNER JOIN User u ON (
			u.role = :admin
			OR p.log.createdBy = u.id
			OR pp.user = u
		)
		WHERE p.uid = :uid AND p.log.deletedAt IS NULL
		AND u.email = :currentUserEmail
	""")
	fun findIdByUidIfPermission(
		uid: ByteArray,
		currentUserEmail: String,
		admin: UserRole = UserRole.ADMIN,
	): Long?

	@Query("""
		SELECT DISTINCT count(p) > 0 FROM Project p
		LEFT JOIN ProjectPermission pp ON pp.project = p
		INNER JOIN User u ON (
			u.role = :admin
			OR p.log.createdBy = u.id
			OR pp.user = u
		)
		WHERE p.uid = :uid AND p.log.deletedAt IS NULL
		AND u.email = :currentUserEmail
	""")
	fun existsByUidIfPermission(
		uid: ByteArray,
		currentUserEmail: String,
		admin: UserRole = UserRole.ADMIN,
	): Boolean
}