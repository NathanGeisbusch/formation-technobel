package be.technobel.playzone.dal.repositories

import be.technobel.playzone.dal.models.entities.User
import jakarta.transaction.Transactional
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query

data class HistoryLogins(val createdBy: String, val updatedBy: String)

interface UserRepository : JpaRepository<User, Long> {
	@Query(
		"""
		SELECT new be.technobel.playzone.dal.repositories.HistoryLogins(u1.email, u2.email)
		FROM User u1, User u2 WHERE u1.id = :createdBy AND u2.id = :updatedBy
	"""
	)
	fun getLogIds(createdBy: Long, updatedBy: Long): HistoryLogins

	@Query("""
		SELECT u.email FROM User u
		WHERE UPPER(u.email) LIKE UPPER(CONCAT(:email, '%')) ESCAPE '!'
		AND u.log.deletedAt IS NULL AND u.isActivated = TRUE
	""")
	fun findEmails(pageable: Pageable, email: String): Page<String>

	@Query("""
		SELECT u.id FROM User u
		WHERE u.email = :email
		AND u.log.deletedAt IS NULL
		AND u.isActivated = TRUE
	""")
	fun findIdByEmail(email: String): Long?

	@Query("""
		SELECT u FROM User u
		WHERE u.email = :email
		AND u.log.deletedAt IS NULL
		AND u.isActivated = TRUE
	""")
	fun findByEmail(email: String): User?

	@Query("""
		SELECT u FROM User u
		WHERE u.log.deletedAt IS NULL
	""")
	fun find(pageable: Pageable): Page<User>

	@Query("""
		SELECT u FROM User u
		WHERE u.uid = :uid
		AND u.log.deletedAt IS NULL
	""")
	fun findByUid(uid: ByteArray): User?

	@Query("""
		SELECT count(u) > 0 FROM User u
		WHERE u.uid = :uid
		AND u.log.deletedAt IS NULL
	""")
	fun existsByUid(uid: ByteArray): Boolean

	@Query("""
		SELECT count(u) > 0 FROM User u
		WHERE u.email = :email
		AND u.log.deletedAt IS NULL
	""")
	fun existsByEmail(email: String): Boolean

	@Transactional @Modifying @Query("""
		UPDATE User u SET u.log.deletedAt = CURRENT_DATE, u.log.deletedBy = :currentUserId
		WHERE u.uid = :uid AND u.log.deletedAt IS NULL
	""")
	fun deleteByUid(uid: ByteArray, currentUserId: Long)

	@Transactional @Modifying @Query("""
		UPDATE User u SET u.passwordHash = :password
		WHERE u.uid = :uid
		AND u.log.deletedAt IS NULL
		AND u.isActivated = TRUE
	""")
	fun modifyPasswordByUid(uid: ByteArray, password: String)
}