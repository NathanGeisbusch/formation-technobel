package be.technobel.playzone.dal.repositories

import be.technobel.playzone.dal.models.entities.DataValue
import be.technobel.playzone.dal.models.entities.DataHeader
import be.technobel.playzone.dal.models.entities.DataRow
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface DataValueRepository : JpaRepository<DataValue, Long> {
	@Query(
		"""
		SELECT dv FROM DataValue dv
		WHERE dv.dataRow = :dataRow
	"""
	)
	fun find(dataRow: DataRow): List<DataValue>

	@Query(
		"""
		SELECT new kotlin.Pair(dh.name, dv.value) FROM DataValue dv
		INNER JOIN DataHeader dh ON dv.dataHeader = dh
		WHERE dv.dataRow = :dataRow
	"""
	)
	fun findWithHeader(dataRow: DataRow): List<Pair<String, String>>

	@Query(
		"""
		SELECT dv FROM DataValue dv
		WHERE dv.dataRow = :dataRow
		AND dv.dataHeader = :dataHeader
	"""
	)
	fun findOne(dataRow: DataRow, dataHeader: DataHeader): DataValue?

	@Query(
		"""
		SELECT dv.value FROM DataValue dv
		JOIN DataHeader dh on dv.dataHeader = dh
		JOIN DataRow dr on dv.dataRow = dr
		JOIN DataTable dt on dh.dataTable = dt and dr.dataTable = dt
		WHERE dt.uid = :dataTable and dh.uid = :dataHeader
		AND UPPER(dv.value) LIKE UPPER(CONCAT(:value, '%')) ESCAPE '!'
	"""
	)
	fun searchValues(pageable: Pageable, dataTable: ByteArray, dataHeader: ByteArray, value: String): Page<String>
}