package be.technobel.playzone.dal.repositories

import be.technobel.playzone.dal.models.entities.ProjectView
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.math.BigDecimal

data class ChartResult(
	val label: String,
	val data: String,
	val result: BigDecimal,
) {
	constructor(label: String, data: String, result: Long)
	: this(label, data, BigDecimal(result))
}

class TableIds(
	val tableId: Long,
	val headerId: Long,
	val headerPkId: Long?,
	val factTableFkId: Long?,
	val factHeaderFkId: Long?,
	val valueExists: Boolean?,
)

interface ChartRepository : JpaRepository<ProjectView, Long> {
	@Query(
		"""
		select new be.technobel.playzone.dal.repositories.ChartResult(
			dvl.value,
			dvd.value,
			sum(cast(dv3.value as BigDecimal))
		)
		from DataTable dt
		join DataRow dr on dr.dataTable = dt
		join DataHeader dh1 on dh1.dataTable = dt and dh1.id = :factHeaderLabelId
		join DataHeader dh2 on dh2.dataTable = dt and dh2.id = :factHeaderDataId
		join DataHeader dh3 on dh3.dataTable = dt and dh3.id = :factHeaderResultId
		join DataValue dv1 on dv1.dataHeader = dh1 and dv1.dataRow = dr
		join DataValue dv2 on dv2.dataHeader = dh2 and dv2.dataRow = dr
		join DataValue dv3 on dv3.dataHeader = dh3 and dv3.dataRow = dr

		join DataValue dvlfk on dvlfk.value = dv1.value
		join DataHeader dhlfk on dvlfk.dataHeader = dhlfk 
		join DataTable dtl on dhlfk.dataTable = dtl
		join DataRow drl on dvlfk.dataRow = drl
		join DataHeader dhl on dhl.dataTable = dtl
		join DataValue dvl on dvl.dataHeader = dhl and dvl.dataRow = drl

		join DataValue dvdfk on dvdfk.value = dv2.value
		join DataHeader dhdfk on dvdfk.dataHeader = dhdfk
		join DataTable dtd on dhdfk.dataTable = dtd
		join DataRow drd on dvdfk.dataRow = drd
		join DataHeader dhd on dhd.dataTable = dtd
		join DataValue dvd on dvd.dataHeader = dhd and dvd.dataRow = drd

		where dt.id = :factTableId
		and dtl.id = :labelTableId and dhlfk.id = :labelHeaderPkId and dhl.id = :labelHeaderId
		and dtd.id = :dataTableId and dhdfk.id = :dataHeaderPkId and dhd.id = :dataHeaderId
		group by dvl.value, dvd.value
		order by dvl.value, dvd.value
	"""
	)
	fun getResultByHeader(
		factTableId: Long,
		factHeaderLabelId: Long,
		factHeaderDataId: Long,
		factHeaderResultId: Long,
		labelTableId: Long,
		labelHeaderPkId: Long,
		labelHeaderId: Long,
		dataTableId: Long,
		dataHeaderPkId: Long,
		dataHeaderId: Long,
	): List<ChartResult>

	@Query(
		"""
		select new be.technobel.playzone.dal.repositories.ChartResult(
			dvl.value,
			'',
			sum(cast(dv3.value as BigDecimal))
		)
		from DataTable dt
		join DataRow dr on dr.dataTable = dt
		join DataHeader dh1 on dh1.dataTable = dt and dh1.id = :factHeaderLabelId
		join DataHeader dh3 on dh3.dataTable = dt and dh3.id = :factHeaderResultId
		join DataValue dv1 on dv1.dataHeader = dh1 and dv1.dataRow = dr
		join DataValue dv3 on dv3.dataHeader = dh3 and dv3.dataRow = dr

		join DataValue dvlfk on dvlfk.value = dv1.value
		join DataHeader dhlfk on dvlfk.dataHeader = dhlfk 
		join DataTable dtl on dhlfk.dataTable = dtl
		join DataRow drl on dvlfk.dataRow = drl
		join DataHeader dhl on dhl.dataTable = dtl
		join DataValue dvl on dvl.dataHeader = dhl and dvl.dataRow = drl

		where dt.id = :factTableId
		and dtl.id = :labelTableId and dhlfk.id = :labelHeaderPkId and dhl.id = :labelHeaderId
		group by dvl.value
		order by dvl.value
	"""
	)
	fun getResultByTable(
		factTableId: Long,
		factHeaderLabelId: Long,
		factHeaderResultId: Long,
		labelTableId: Long,
		labelHeaderPkId: Long,
		labelHeaderId: Long,
	): List<ChartResult>

	@Query(
		"""
		select new be.technobel.playzone.dal.repositories.ChartResult(
			dvl.value,
			dvd.value,
			sum(cast(dv3.value as BigDecimal))
		)
		from DataTable dt
		join DataRow dr on dr.dataTable = dt
		join DataHeader dh1 on dh1.dataTable = dt and dh1.id = :factHeaderLabelId
		join DataHeader dh2 on dh2.dataTable = dt and dh2.id = :factHeaderDataId
		join DataHeader dh3 on dh3.dataTable = dt and dh3.id = :factHeaderResultId
		join DataValue dv1 on dv1.dataHeader = dh1 and dv1.dataRow = dr
		join DataValue dv2 on dv2.dataHeader = dh2 and dv2.dataRow = dr
		join DataValue dv3 on dv3.dataHeader = dh3 and dv3.dataRow = dr

		join DataValue dvlfk on dvlfk.value = dv1.value
		join DataHeader dhlfk on dvlfk.dataHeader = dhlfk 
		join DataTable dtl on dhlfk.dataTable = dtl
		join DataRow drl on dvlfk.dataRow = drl
		join DataHeader dhl on dhl.dataTable = dtl
		join DataValue dvl on dvl.dataHeader = dhl and dvl.dataRow = drl

		join DataValue dvdfk on dvdfk.value = dv2.value
		join DataHeader dhdfk on dvdfk.dataHeader = dhdfk
		join DataTable dtd on dhdfk.dataTable = dtd
		join DataRow drd on dvdfk.dataRow = drd
		join DataHeader dhd on dhd.dataTable = dtd
		join DataValue dvd on dvd.dataHeader = dhd and dvd.dataRow = drd

		where dt.id = :factTableId and dvd.value = :value
		and dtl.id = :labelTableId and dhlfk.id = :labelHeaderPkId and dhl.id = :labelHeaderId
		and dtd.id = :dataTableId and dhdfk.id = :dataHeaderPkId and dhd.id = :dataHeaderId
		group by dvl.value, dvd.value
		order by dvl.value, dvd.value
	"""
	)
	fun getResultByValue(
		factTableId: Long,
		factHeaderLabelId: Long,
		factHeaderDataId: Long,
		factHeaderResultId: Long,
		labelTableId: Long,
		labelHeaderPkId: Long,
		labelHeaderId: Long,
		dataTableId: Long,
		dataHeaderPkId: Long,
		dataHeaderId: Long,
		value: String,
	): List<ChartResult>

	@Query(
		"""
		select new be.technobel.playzone.dal.repositories.ChartResult(
			dvl.value,
			dvd.value,
			sum(cast(dv3.value as BigDecimal))
		)
		from DataTable dt
		join DataRow dr on dr.dataTable = dt
		join DataHeader dh1 on dh1.dataTable = dt and dh1.id = :factHeaderLabelId
		join DataHeader dh2 on dh2.dataTable = dt and dh2.id = :factHeaderDataId
		join DataHeader dh3 on dh3.dataTable = dt and dh3.id = :factHeaderResultId
		join DataValue dv1 on dv1.dataHeader = dh1 and dv1.dataRow = dr
		join DataValue dv2 on dv2.dataHeader = dh2 and dv2.dataRow = dr
		join DataValue dv3 on dv3.dataHeader = dh3 and dv3.dataRow = dr

		join DataValue dvlfk on dvlfk.value = dv1.value
		join DataHeader dhlfk on dvlfk.dataHeader = dhlfk 
		join DataTable dtl on dhlfk.dataTable = dtl
		join DataRow drl on dvlfk.dataRow = drl
		join DataHeader dhl on dhl.dataTable = dtl
		join DataValue dvl on dvl.dataHeader = dhl and dvl.dataRow = drl

		join DataValue dvdfk on dvdfk.value = dv2.value
		join DataHeader dhdfk on dvdfk.dataHeader = dhdfk
		join DataTable dtd on dhdfk.dataTable = dtd
		join DataRow drd on dvdfk.dataRow = drd
		join DataHeader dhd on dhd.dataTable = dtd
		join DataValue dvd on dvd.dataHeader = dhd and dvd.dataRow = drd

		where dt.id = :factTableId and dv2.value = :pkValue
		and dtl.id = :labelTableId and dhlfk.id = :labelHeaderPkId and dhl.id = :labelHeaderId
		and dtd.id = :dataTableId and dhdfk.id = :dataHeaderPkId and dhd.id = :dataHeaderId
		group by dvl.value, dvd.value
		order by dvl.value, dvd.value
	"""
	)
	fun getResultByPkValue(
		factTableId: Long,
		factHeaderLabelId: Long,
		factHeaderDataId: Long,
		factHeaderResultId: Long,
		labelTableId: Long,
		labelHeaderPkId: Long,
		labelHeaderId: Long,
		dataTableId: Long,
		dataHeaderPkId: Long,
		dataHeaderId: Long,
		pkValue: String,
	): List<ChartResult>

	@Query(
		"""
		select distinct new be.technobel.playzone.dal.repositories.TableIds(
			dt.id, dh.id, dh_pk.id,
			fact_st.dataTable.id,
			fact_sh.dataHeader.id,
			(dv.value = :value)
		)
		from DataTable dt
		join DataHeader dh on dh.dataTable = dt and dh.uid = :headerUid
		left join SchemaTable st on st.dataTable = dt
			and st.fact = false
			and st.project.id = :projectId
		left join SchemaHeader sh_pk on sh_pk.schemaTable = st and sh_pk.isPK = true
		left join DataHeader dh_pk on sh_pk.dataHeader = dh_pk
		left join SchemaHeader fact_sh on fact_sh.fkTable = dt.id and fact_sh.fkField = dh_pk.id
		left join SchemaTable fact_st on fact_sh.schemaTable = fact_st
			and fact_st.fact = true
			and fact_st.project.id = :projectId
		left join DataValue dv on dv.dataHeader = dh and dv.value = :value
		where dt.uid = :tableUid
	"""
	)
	fun getTableIds(projectId: Long, tableUid: ByteArray, headerUid: ByteArray, value: String? = null): TableIds?

	@Query(
		"""
		select distinct new be.technobel.playzone.dal.repositories.TableIds(
			dt.id, dh.id, dh_pk.id,
			fact_st.dataTable.id,
			fact_sh.dataHeader.id,
			(dv.value = :value)
		)
		from DataTable dt
		join DataHeader dh on dh.dataTable = dt and dh.uid = :headerUid
		left join SchemaTable st on st.dataTable = dt
			and st.fact = false
			and st.project.id = :projectId
		left join SchemaHeader sh_pk on sh_pk.schemaTable = st and sh_pk.isPK = true
		left join DataHeader dh_pk on sh_pk.dataHeader = dh_pk
		left join SchemaHeader fact_sh on fact_sh.fkTable = dt.id and fact_sh.fkField = dh_pk.id
		left join SchemaTable fact_st on fact_sh.schemaTable = fact_st
			and fact_st.fact = true
			and fact_st.project.id = :projectId
		left join DataValue dv on dv.dataHeader = dh_pk and dv.value = :value
		where dt.uid = :tableUid
	"""
	)
	fun getTableIdsByPkValue(projectId: Long, tableUid: ByteArray, headerUid: ByteArray, value: String? = null): TableIds?

	/** Renvoie de l'id de la colonne représentant les résultats dans la table de faits */
	@Query("""
		select dh.id
		from DataTable dt
		join SchemaTable st on st.dataTable = dt
		join SchemaHeader sh on sh.schemaTable = st
		join DataHeader dh on sh.dataHeader = dh
		where st.project.id = :projectId and st.fact = true and sh.fkTable = null
	""")
	fun getFactResultHeader(pageable: Pageable, projectId: Long): Page<Long>
}
