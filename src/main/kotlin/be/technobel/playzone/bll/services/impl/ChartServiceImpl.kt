package be.technobel.playzone.bll.services.impl

import be.technobel.playzone.dal.repositories.ChartRepository
import be.technobel.playzone.dal.repositories.ChartResult
import be.technobel.playzone.pl.models.dto.ChartDTO
import be.technobel.playzone.pl.models.dto.ChartDataset
import be.technobel.playzone.pl.models.forms.ChartRequest
import be.technobel.playzone.pl.rest.InvalidParamException
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import java.math.BigDecimal

@Service
class ChartService(private val chartRepository: ChartRepository) {
	fun getChart(projectId: Long, chartRequest: ChartRequest): ChartDTO {
		val factResultHeaderContent =
			chartRepository.getFactResultHeader(PageRequest.ofSize(1), projectId).content
		if(factResultHeaderContent.size == 0) throw InvalidParamException("form", "fact_result_not_found")
		val factResultHeaderId = factResultHeaderContent[0]
		return when {
			chartRequest.data == null -> getChartByTable(projectId, chartRequest, factResultHeaderId)
			chartRequest.value != null -> getChartByValue(projectId, chartRequest, factResultHeaderId)
			chartRequest.pkValue != null -> getChartByPkValue(projectId, chartRequest, factResultHeaderId)
			else -> getChartByHeader(projectId, chartRequest, factResultHeaderId)
		}
	}

	private fun getChartByTable(
		projectId: Long,
		chartRequest: ChartRequest,
		factResultHeaderId: Long,
	): ChartDTO {
		val label = chartRepository.getTableIds(projectId, chartRequest.label.tableUid, chartRequest.label.headerUid)
			?: throw InvalidParamException("form.label", "not_found")
		if(label.headerPkId == null) throw InvalidParamException("form.label", "pk_not_found")
		if(label.factTableFkId == null || label.factHeaderFkId == null) throw InvalidParamException("form.label", "fk_not_found")
		try {
			val chartResultList = chartRepository.getResultByTable(
				label.factTableFkId, label.factHeaderFkId, factResultHeaderId,
				label.tableId, label.headerPkId, label.headerId,
			)
			val labels = chartResultList.map { it.label }
			val datasets = listOf(
				ChartDataset(label = null, data = chartResultList.map { it.result })
			)
			return ChartDTO(labels, datasets)
		}
		catch(ex: NumberFormatException) {
			throw InvalidParamException("form", "not_a_number")
		}
	}

	private fun getChartByHeader(
		projectId: Long,
		chartRequest: ChartRequest,
		factResultHeaderId: Long,
	): ChartDTO {
		// label
		val label = chartRepository.getTableIds(
			projectId, chartRequest.label.tableUid, chartRequest.label.headerUid
		) ?: throw InvalidParamException("form.label", "not_found")
		if(label.headerPkId == null) throw InvalidParamException("form.label", "pk_not_found")
		if(label.factTableFkId == null || label.factHeaderFkId == null) throw InvalidParamException("form.label", "fk_not_found")
		// data
		val data = chartRepository.getTableIds(
			projectId, chartRequest.data!!.tableUid, chartRequest.data.headerUid
		) ?: throw InvalidParamException("form.data", "not_found")
		if(data.headerPkId == null) throw InvalidParamException("form.data", "pk_not_found")
		if(data.factTableFkId == null || data.factHeaderFkId == null) throw InvalidParamException("form.data", "fk_not_found")
		// result
		try {
			val chartResultList = chartRepository.getResultByHeader(
				label.factTableFkId, label.factHeaderFkId, data.factHeaderFkId, factResultHeaderId,
				label.tableId, label.headerPkId, label.headerId,
				data.tableId, data.headerPkId, data.headerId,
			)
			return getGroupedResult(chartResultList)
		}
		catch(ex: NumberFormatException) {
			throw InvalidParamException("form", "not_a_number")
		}
	}

	private fun getChartByValue(
		projectId: Long,
		chartRequest: ChartRequest,
		factResultHeaderId: Long,
	): ChartDTO {
		// label
		val label = chartRepository.getTableIds(
			projectId, chartRequest.label.tableUid, chartRequest.label.headerUid
		) ?: throw InvalidParamException("form.label", "not_found")
		if(label.headerPkId == null) throw InvalidParamException("form.label", "pk_not_found")
		if(label.factTableFkId == null || label.factHeaderFkId == null) throw InvalidParamException("form.label", "fk_not_found")
		// data
		val data = chartRepository.getTableIds(
			projectId, chartRequest.data!!.tableUid, chartRequest.data.headerUid, chartRequest.value
		) ?: throw InvalidParamException("form.data", "not_found")
		if(data.headerPkId == null) throw InvalidParamException("form.data", "pk_not_found")
		if(data.factTableFkId == null || data.factHeaderFkId == null) throw InvalidParamException("form.data", "fk_not_found")
		if(data.valueExists == null || data.valueExists == false) throw InvalidParamException("form.data", "value_not_found")
		// result
		try {
			val chartResultList = chartRepository.getResultByValue(
				label.factTableFkId, label.factHeaderFkId, data.factHeaderFkId, factResultHeaderId,
				label.tableId, label.headerPkId, label.headerId,
				data.tableId, data.headerPkId, data.headerId,
				chartRequest.value!!
			)
			return getGroupedResult(chartResultList)
		}
		catch(ex: NumberFormatException) {
			throw InvalidParamException("form", "not_a_number")
		}
	}

	private fun getChartByPkValue(
		projectId: Long,
		chartRequest: ChartRequest,
		factResultHeaderId: Long,
	): ChartDTO {
		// label
		val label = chartRepository.getTableIds(
			projectId, chartRequest.label.tableUid, chartRequest.label.headerUid
		) ?: throw InvalidParamException("form.label", "not_found")
		if(label.headerPkId == null) throw InvalidParamException("form.label", "pk_not_found")
		if(label.factTableFkId == null || label.factHeaderFkId == null) throw InvalidParamException("form.label", "fk_not_found")
		// data
		val data = chartRepository.getTableIdsByPkValue(
			projectId, chartRequest.data!!.tableUid, chartRequest.data.headerUid, chartRequest.pkValue
		) ?: throw InvalidParamException("form.data", "not_found")
		if(data.headerPkId == null) throw InvalidParamException("form.data", "pk_not_found")
		if(data.factTableFkId == null || data.factHeaderFkId == null) throw InvalidParamException("form.data", "fk_not_found")
		if(data.valueExists == null || data.valueExists == false) throw InvalidParamException("form.data", "value_not_found")
		// result
		try {
			val chartResultList = chartRepository.getResultByPkValue(
				label.factTableFkId, label.factHeaderFkId, data.factHeaderFkId, factResultHeaderId,
				label.tableId, label.headerPkId, label.headerId,
				data.tableId, data.headerPkId, data.headerId,
				chartRequest.pkValue!!
			)
			return getGroupedResult(chartResultList)
		}
		catch(ex: NumberFormatException) {
			throw InvalidParamException("form", "not_a_number")
		}
	}

	private fun getGroupedResult(chartResultList: List<ChartResult>): ChartDTO {
		val labels = chartResultList.map { it.label }.toSet().toList()
		val datas = chartResultList.map { it.data }.toSet().toList()
		val groupedData = chartResultList.groupBy { it.label }
		val datasets = datas.map { data ->
			ChartDataset(
				label = data,
				data = labels.map { label ->
					groupedData[label]?.find { it.data == data }?.result ?: BigDecimal.ZERO
				}
			)
		}
		return ChartDTO(labels, datasets)
	}
}