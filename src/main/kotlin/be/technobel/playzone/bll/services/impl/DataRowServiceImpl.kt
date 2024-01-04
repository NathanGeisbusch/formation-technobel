package be.technobel.playzone.bll.services.impl

import be.technobel.playzone.bll.exceptions.NotFoundException
import be.technobel.playzone.bll.services.DataRowService
import be.technobel.playzone.dal.models.entities.*
import be.technobel.playzone.dal.repositories.*
import be.technobel.playzone.dal.utils.escapeLikePattern
import be.technobel.playzone.pl.models.dto.DataRowDTO
import be.technobel.playzone.pl.models.dto.toDTO
import be.technobel.playzone.pl.models.forms.DataRowForm
import be.technobel.playzone.pl.models.forms.DataRowFormUpdates
import be.technobel.playzone.pl.rest.InvalidParamException
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class DataRowServiceImpl(
	private val dataRowRepository: DataRowRepository,
	private val dataHeaderRepository: DataHeaderRepository,
	private val dataValueRepository: DataValueRepository,
	private val dataTableRepository: DataTableRepository,
	private val userRepository: UserRepository,
	private val uidService: UidService,
) : DataRowService {
	private fun findFields(dataTable: DataRow): Map<String, String> {
		return dataValueRepository.findWithHeader(dataTable).associate { it.first to it.second }
	}

	@Throws(NotFoundException::class)
	override fun findAll(tableUid: ByteArray, page: Int, size: Int, currentUserEmail: String): Page<DataRowDTO> {
		if(!dataTableRepository.existsByUid(tableUid)) throw NotFoundException("Table not found")
		val pageable = PageRequest.of(page, size, Sort.by("log.createdAt"))
		return dataRowRepository.find(pageable, tableUid).map {
			it.toDTO(uidService::toBase64, userRepository::getLogIds, ::findFields)
		}
	}

	@Throws(NotFoundException::class)
	override fun findOne(tableUid: ByteArray, dataUid: ByteArray, currentUserEmail: String): DataRowDTO {
		if(!dataTableRepository.existsByUid(tableUid)) throw NotFoundException("Table not found")
		return dataRowRepository.findOne(tableUid, dataUid)
			?.toDTO(uidService::toBase64, userRepository::getLogIds, ::findFields)
			?: throw NotFoundException("Data row not found")
	}

	@Throws(NotFoundException::class)
	override fun create(tableUid: ByteArray, form: List<DataRowForm>, currentUserEmail: String): List<String> {
		val table = dataTableRepository.findByUidIfPermission(tableUid, currentUserEmail)
			?: throw NotFoundException("Table not found")
		val headers = dataHeaderRepository.find(table)
		val currentUserId = userRepository.findIdByEmail(currentUserEmail)!!
		val rows = form.map { rowForm ->
			val row = DataRow().apply {
				uid = uidService.generate()
				dataTable = table
				log = HistoryLog().apply {
					createdAt = LocalDateTime.now()
					updatedAt = createdAt
					createdBy = currentUserId
					updatedBy = createdBy
				}
			}
			val fields = rowForm.entries.map { fieldForm ->
				val header = headers.find { fieldForm.key == it.name }
					?: throw InvalidParamException("form.field.id", "not_found")
				DataValue().apply {
					dataRow = row
					dataHeader = header
					value = fieldForm.value
				}
			}
			if(fields.size != headers.size) throw InvalidParamException("form.field", "size")
			Pair(row, fields)
		}
		val createdRows = dataRowRepository.saveAll(rows.map { it.first })
		dataValueRepository.saveAll(rows.flatMap { it.second })
		return createdRows.map { uidService.toBase64(it.uid) }
	}

	@Throws(NotFoundException::class)
	override fun update(tableUid: ByteArray, form: List<DataRowFormUpdates>, currentUserEmail: String) {
		val table = dataTableRepository.findByUidIfPermission(tableUid, currentUserEmail)
			?: throw NotFoundException("Table not found")
		val headers = dataHeaderRepository.find(table)
		val currentUserId = userRepository.findIdByEmail(currentUserEmail)!!
		val rows = form.map { rowForm ->
			val dataRow = dataRowRepository.findOne(tableUid, uidService.fromBase64(rowForm.id!!))
				?: throw NotFoundException("Data row not found")
			dataRow.apply {
				log.updatedAt = LocalDateTime.now()
				log.updatedBy = currentUserId
			}
			// fields
			val fields = rowForm.values!!.entries.map { fieldForm ->
				val header = headers.find { fieldForm.key == it.name }
					?: throw InvalidParamException("form.field.id", "not_found")
				val field = dataValueRepository.findOne(dataRow, header)
					?: throw InvalidParamException("form.field", "not_found")
				field.value = fieldForm.value
				field
			}
			if(fields.size != headers.size) throw InvalidParamException("form.field", "size")
			Pair(dataRow, fields)
		}
		dataRowRepository.saveAll(rows.map { it.first })
		dataValueRepository.saveAll(rows.flatMap { it.second })
	}

	@Throws(NotFoundException::class)
	override fun update(tableUid: ByteArray, dataUid: ByteArray, form: DataRowForm, currentUserEmail: String) {
		val table = dataTableRepository.findByUidIfPermission(tableUid, currentUserEmail)
			?: throw NotFoundException("Table not found")
		val headers = dataHeaderRepository.find(table)
		val currentUserId = userRepository.findIdByEmail(currentUserEmail)!!
		// row
		val dataRow = dataRowRepository.findOne(tableUid, dataUid)
			?: throw NotFoundException("Data row not found")
		dataRow.apply {
			log.updatedAt = LocalDateTime.now()
			log.updatedBy = currentUserId
		}
		// fields
		val fields = form.entries.map { fieldForm ->
			val header = headers.find { fieldForm.key == it.name }
				?: throw InvalidParamException("form.field.id", "not_found")
			val field = dataValueRepository.findOne(dataRow, header)
				?: throw InvalidParamException("form.field", "not_found")
			field.value = fieldForm.value
			field
		}
		if(fields.size != headers.size) throw InvalidParamException("form.field", "size")
		// save
		dataRowRepository.save(dataRow)
		dataValueRepository.saveAll(fields)
	}

	@Throws(NotFoundException::class)
	override fun delete(tableUid: ByteArray, form: List<String>, currentUserEmail: String) {
		if(!dataTableRepository.existsByUidIfPermission(tableUid, currentUserEmail))
			throw NotFoundException("Table not found")
		val currentUserId = userRepository.findIdByEmail(currentUserEmail)!!
		form.map {
			val uid = uidService.fromBase64(it)
			if(!dataRowRepository.exists(tableUid, uid))
				throw NotFoundException("Data row not found")
			uid
		}.forEach {
			dataRowRepository.delete(tableUid, it, currentUserId)
		}
	}

	@Throws(NotFoundException::class)
	override fun delete(tableUid: ByteArray, dataUid: ByteArray, currentUserEmail: String) {
		if(!dataTableRepository.existsByUidIfPermission(tableUid, currentUserEmail))
			throw NotFoundException("Table not found")
		val currentUserId = userRepository.findIdByEmail(currentUserEmail)!!
		if(dataRowRepository.exists(tableUid, dataUid))
			dataRowRepository.delete(tableUid, dataUid, currentUserId)
		else throw NotFoundException("Data row not found")
	}

	@Throws(NotFoundException::class)
	override fun searchValues(tableUid: ByteArray, headerUid: ByteArray, value: String): List<String> {
		if(!dataTableRepository.existsByUid(tableUid)) throw NotFoundException("Table not found")
		if(!dataHeaderRepository.exists(headerUid)) throw NotFoundException("Header not found")
		val page = PageRequest.ofSize(20)
		return dataValueRepository.searchValues(page, tableUid, headerUid, escapeLikePattern(value)).content
	}
}