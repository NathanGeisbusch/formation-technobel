package be.technobel.playzone.dal.repositories

import be.technobel.playzone.dal.models.entities.SchemaTable
import jakarta.transaction.Transactional
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query

interface SchemaTableRepository : JpaRepository<SchemaTable, Long> {
	@Query("""
		SELECT st.dataTable.uid FROM SchemaTable st
		WHERE st.project.uid = :projectUid
		AND st.log.deletedAt IS NULL
	""")
	fun getTablesId(projectUid: ByteArray): List<ByteArray>

	@Query("""
		SELECT st.dataTable.uid FROM SchemaTable st
		WHERE st.project.uid = :projectUid
		AND st.log.deletedAt IS NULL
		AND st.dataTable.log.deletedAt IS NOT NULL
	""")
	fun getDeletedTablesId(projectUid: ByteArray): List<ByteArray>

	@Query("""
		SELECT st FROM SchemaTable st
		WHERE st.project.uid = :projectUid
		AND st.log.deletedAt IS NULL
	""")
	fun find(projectUid: ByteArray): List<SchemaTable>

	@Query("""
		SELECT count(st) > 0 FROM SchemaTable st
		WHERE st.project.uid = :projectUid
		AND st.dataTable.uid = :tableUid
		AND st.log.deletedAt IS NULL
	""")
	fun exists(projectUid: ByteArray, tableUid: ByteArray): Boolean

	@Query("""
		SELECT st FROM SchemaTable st
		WHERE st.project.uid = :projectUid
		AND st.dataTable.uid = :tableUid
		AND st.log.deletedAt IS NULL
	""")
	fun findOne(projectUid: ByteArray, tableUid: ByteArray): SchemaTable?

	@Transactional
	@Modifying
	@Query("""
		UPDATE SchemaTable st SET st.log.deletedAt = CURRENT_DATE, st.log.deletedBy = :currentUserId
		WHERE st.project.uid = :projectUid
		AND st.dataTable.uid = :tableUid
		AND st.log.deletedAt IS NULL
	""")
	fun delete(projectUid: ByteArray, tableUid: ByteArray, currentUserId: Long)

	@Transactional
	@Modifying
	@Query("""
		UPDATE SchemaTable st SET st.log.deletedAt = CURRENT_DATE, st.log.deletedBy = :currentUserId
		WHERE st.project.uid = :projectUid
		AND st.dataTable.uid IN :tablesUid
		AND st.log.deletedAt IS NULL
	""")
	fun delete(projectUid: ByteArray, tablesUid: List<ByteArray>, currentUserId: Long)
}