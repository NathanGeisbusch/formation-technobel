package be.technobel.playzone.dal.repositories

import be.technobel.playzone.dal.models.entities.ProjectView
import jakarta.transaction.Transactional
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query

interface ProjectViewRepository : JpaRepository<ProjectView, Long> {
	@Query("""
		SELECT pv FROM ProjectView pv
		WHERE pv.project.id = :projectId
		AND pv.log.deletedAt IS NULL
	""")
	fun find(pageable: Pageable, projectId: Long): Page<ProjectView>

	@Query("""
		SELECT pv FROM ProjectView pv
		WHERE pv.project.id = :projectId
		AND pv.uid = :viewUid
		AND pv.log.deletedAt IS NULL
	""")
	fun findOne(projectId: Long, viewUid: ByteArray): ProjectView?

	@Query("""
		SELECT count(pv) > 0 FROM ProjectView pv
		WHERE pv.project.id = :projectId
		AND pv.uid = :viewUid
		AND pv.log.deletedAt IS NULL
	""")
	fun exists(projectId: Long, viewUid: ByteArray): Boolean

	@Transactional
	@Modifying
	@Query("""
		UPDATE ProjectView pv SET pv.log.deletedAt = CURRENT_DATE, pv.log.deletedBy = :currentUserId
		WHERE pv.project.id = :projectId
		AND pv.uid = :viewUid
		AND pv.log.deletedAt IS NULL
	""")
	fun delete(projectId: Long, viewUid: ByteArray, currentUserId: Long)
}