package be.technobel.playzone.dal.repositories

import be.technobel.playzone.dal.models.entities.DataTable
import be.technobel.playzone.dal.models.enums.UserRole
import jakarta.transaction.Transactional
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query

interface DataTableRepository: JpaRepository<DataTable, Long> {
	@Query("""
		SELECT dt FROM DataTable dt
		WHERE dt.log.deletedAt IS NULL
	""")
	fun find(pageable: Pageable): Page<DataTable>

	@Query("""
		SELECT dt FROM DataTable dt
		INNER JOIN User u ON dt.log.createdBy = u.id
		WHERE dt.log.deletedAt IS NULL
		AND u.email = :currentUserEmail
	""")
	fun findOwn(pageable: Pageable, currentUserEmail: String): Page<DataTable>

	@Query("""
		SELECT dt FROM DataTable dt
		INNER JOIN TablePermission tp ON tp.table = dt
		INNER JOIN User u ON tp.user = u
		WHERE dt.log.deletedAt IS NULL
		AND u.email = :currentUserEmail
	""")
	fun findShared(pageable: Pageable, currentUserEmail: String): Page<DataTable>

	@Transactional
	@Modifying
	@Query("""
		UPDATE DataTable dt SET dt.log.deletedAt = CURRENT_DATE, dt.log.deletedBy = :currentUserId
		WHERE dt.uid = :uid AND dt.log.deletedAt IS NULL
	""")
	fun deleteByUid(uid: ByteArray, currentUserId: Long)

	@Query("""
		SELECT dt FROM DataTable dt
		WHERE dt.uid = :uid AND dt.log.deletedAt IS NULL
	""")
	fun findByUid(uid: ByteArray): DataTable?

	@Query("""
		SELECT count(dt) > 0 FROM DataTable dt
		WHERE dt.uid = :uid AND dt.log.deletedAt IS NULL
	""")
	fun existsByUid(uid: ByteArray): Boolean

	@Query("""
		SELECT DISTINCT dt FROM DataTable dt
		LEFT JOIN TablePermission tp ON tp.table = dt
		INNER JOIN User u ON (
			u.role = :admin
			OR dt.log.createdBy = u.id
			OR tp.user = u
		)
		WHERE dt.uid = :uid AND dt.log.deletedAt IS NULL
		AND u.email = :currentUserEmail
	""")
	fun findByUidIfPermission(
		uid: ByteArray,
		currentUserEmail: String,
		admin: UserRole = UserRole.ADMIN,
	): DataTable?

	@Query("""
		SELECT DISTINCT count(dt) > 0 FROM DataTable dt
		LEFT JOIN TablePermission tp ON tp.table = dt
		INNER JOIN User u ON (
			u.role = :admin
			OR dt.log.createdBy = u.id
			OR tp.user = u
		)
		WHERE dt.uid = :uid AND dt.log.deletedAt IS NULL
		AND u.email = :currentUserEmail
	""")
	fun existsByUidIfPermission(
		uid: ByteArray,
		currentUserEmail: String,
		admin: UserRole = UserRole.ADMIN,
	): Boolean
}