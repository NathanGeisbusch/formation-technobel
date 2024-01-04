package be.technobel.playzone.dal.repositories

import be.technobel.playzone.dal.models.entities.DataHeader
import be.technobel.playzone.dal.models.entities.SchemaHeader
import be.technobel.playzone.dal.models.entities.SchemaTable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface SchemaHeaderRepository : JpaRepository<SchemaHeader, Long> {
	@Query("""
		SELECT sh FROM SchemaHeader sh
		WHERE sh.schemaTable = :schemaTable
	""")
	fun find(schemaTable: SchemaTable): List<SchemaHeader>

	@Query("""
		SELECT new kotlin.Pair(sh, dh) FROM SchemaHeader sh
		INNER JOIN DataHeader dh ON sh.dataHeader = dh
		WHERE sh.schemaTable = :schemaTable
	""")
	fun findHeaders(schemaTable: SchemaTable): List<Pair<SchemaHeader, DataHeader>>

	@Query("""
		SELECT sh FROM SchemaHeader sh
		WHERE sh.schemaTable = :schemaTable
		AND sh.dataHeader = :dataHeader
	""")
	fun findOne(schemaTable: SchemaTable, dataHeader: DataHeader): SchemaHeader?

	@Query("""
		SELECT sh FROM SchemaHeader sh
		INNER JOIN SchemaTable st ON sh.schemaTable = st
		WHERE st.project.uid = :projectUid
		AND sh.dataHeader.uid = :dataHeaderUid
	""")
	fun findOneByProject(projectUid: ByteArray, dataHeaderUid: ByteArray): SchemaHeader?

	@Query("""
		select new kotlin.Pair(dt.id, dh.id)
		from DataTable dt
		join DataHeader dh on dh.dataTable = dt and dh.uid = :headerUid
		where dt.uid = :tableUid
	""")
	fun fkUidToId(tableUid: ByteArray, headerUid: ByteArray): Pair<Long,Long>?

	@Query("""
		select new kotlin.Pair(dt.uid, dh.uid)
		from DataTable dt
		join DataHeader dh on dh.dataTable = dt and dh.id = :headerId
		where dt.id = :tableId
	""")
	fun fkIdToUid(tableId: Long, headerId: Long): Pair<ByteArray,ByteArray>?
}