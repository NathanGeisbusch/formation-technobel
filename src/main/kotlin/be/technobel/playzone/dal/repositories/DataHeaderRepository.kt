package be.technobel.playzone.dal.repositories

import be.technobel.playzone.dal.models.entities.DataHeader
import be.technobel.playzone.dal.models.entities.DataTable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface DataHeaderRepository: JpaRepository<DataHeader, Long> {
	@Query("""
		SELECT dh FROM DataHeader dh
		WHERE dh.dataTable = :dataTable
	""")
	fun find(dataTable: DataTable): List<DataHeader>

	@Query("""
		SELECT count(dh) > 0 FROM DataHeader dh
		WHERE dh.uid = :uid
	""")
	fun exists(uid: ByteArray): Boolean

	@Query("""
		SELECT dh FROM DataHeader dh
		WHERE dh.uid = :uid
	""")
	fun findOne(uid: ByteArray): DataHeader?

	@Query("""
		SELECT dh FROM DataHeader dh
		WHERE dh.dataTable = :dataTable
		AND dh.name = :name
	""")
	fun findOneByName(dataTable: DataTable, name: String): DataHeader?
}