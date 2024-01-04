package be.technobel.playzone.dal.repositories

import be.technobel.playzone.dal.models.entities.DataRow
import jakarta.transaction.Transactional
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query

interface DataRowRepository : JpaRepository<DataRow, Long> {
	@Query("""
		SELECT dr FROM DataRow dr
		WHERE dr.dataTable.uid = :tableUid
		AND dr.log.deletedAt IS NULL
	""")
	fun find(pageable: Pageable, tableUid: ByteArray): Page<DataRow>

	@Query("""
		SELECT dr FROM DataRow dr
		WHERE dr.dataTable.uid = :tableUid
		AND dr.uid = :dataUid
		AND dr.log.deletedAt IS NULL
	""")
	fun findOne(tableUid: ByteArray, dataUid: ByteArray): DataRow?

	@Query("""
		SELECT count(dr) > 0 FROM DataRow dr
		WHERE dr.dataTable.uid = :tableUid
		AND dr.uid = :dataUid
		AND dr.log.deletedAt IS NULL
	""")
	fun exists(tableUid: ByteArray, dataUid: ByteArray): Boolean

	@Transactional
	@Modifying
	@Query("""
		UPDATE DataRow dr SET dr.log.deletedAt = CURRENT_DATE, dr.log.deletedBy = :currentUserId
		WHERE dr.dataTable.uid = :tableUid
		AND dr.uid = :dataUid
		AND dr.log.deletedAt IS NULL
	""")
	fun delete(tableUid: ByteArray, dataUid: ByteArray, currentUserId: Long)
}