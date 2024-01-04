package be.technobel.playzone.bll.services.impl

import be.technobel.playzone.bll.exceptions.NotFoundException
import be.technobel.playzone.bll.services.DataTableService
import be.technobel.playzone.dal.models.entities.DataHeader
import be.technobel.playzone.dal.models.entities.DataTable
import be.technobel.playzone.dal.models.entities.HistoryLog
import be.technobel.playzone.dal.repositories.DataHeaderRepository
import be.technobel.playzone.dal.repositories.DataTableRepository
import be.technobel.playzone.dal.repositories.UserRepository
import be.technobel.playzone.pl.models.dto.DataTableDTO
import be.technobel.playzone.pl.models.dto.toDTO
import be.technobel.playzone.pl.models.forms.DataTableFormCreate
import be.technobel.playzone.pl.models.forms.DataTableFormUpdate
import be.technobel.playzone.pl.rest.InvalidParamException
import be.technobel.playzone.pl.rest.TableVisibility
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class DataTableServiceImpl(
	private val dataTableRepository: DataTableRepository,
	private val dataHeaderRepository: DataHeaderRepository,
	private val userRepository: UserRepository,
	private val uidService: UidService,
) : DataTableService {
	private fun findHeaders(dataTable: DataTable): List<DataHeader> {
		return dataHeaderRepository.find(dataTable)
	}

	override fun findAll(page: Int, size: Int, currentUserEmail: String): Page<DataTableDTO> {
		val pageable = PageRequest.of(page, size, Sort.by("name"))
		return dataTableRepository.find(pageable).map {
			it.toDTO(uidService::toBase64, userRepository::getLogIds, ::findHeaders)
		}
	}

	override fun findAll(
		page: Int, size: Int, currentUserEmail: String, visibility: TableVisibility
	): Page<DataTableDTO> {
		val pageable = PageRequest.of(page, size, Sort.by("name"))
		return when(visibility) {
			TableVisibility.OWNER  -> dataTableRepository.findOwn(pageable, currentUserEmail)
			TableVisibility.SHARED -> dataTableRepository.findShared(pageable, currentUserEmail)
		}.map {
			it.toDTO(uidService::toBase64, userRepository::getLogIds, ::findHeaders)
		}
	}

	@Throws(NotFoundException::class)
	override fun findOne(tableUid: ByteArray, currentUserEmail: String): DataTableDTO {
		return dataTableRepository.findByUid(tableUid)
			?.toDTO(uidService::toBase64, userRepository::getLogIds, ::findHeaders)
			?: throw NotFoundException("Table not found")
	}

	override fun create(form: DataTableFormCreate, currentUserEmail: String): String {
		val currentUserId = userRepository.findIdByEmail(currentUserEmail)!!
		val table = dataTableRepository.saveAndFlush(
			DataTable().apply {
				uid = uidService.generate()
				name = form.table!!
				log = HistoryLog().apply {
					createdAt = LocalDateTime.now()
					updatedAt = createdAt
					createdBy = currentUserId
					updatedBy = createdBy
				}
			}
		)
		val headers = form.headers!!.map {
			DataHeader().apply {
				uid = uidService.generate()
				dataTable = table
				name = it.name!!
			}
		}
		dataHeaderRepository.saveAllAndFlush(headers)
		return table.run { uidService.toBase64(uid) }
	}

	@Throws(NotFoundException::class)
	override fun update(tableUid: ByteArray, form: DataTableFormUpdate, currentUserEmail: String) {
		// get table
		val currentUserId = userRepository.findIdByEmail(currentUserEmail)!!
		val table = dataTableRepository.findByUidIfPermission(tableUid, currentUserEmail)?.apply {
			name = form.table!!
			log.updatedAt = LocalDateTime.now()
			log.updatedBy = currentUserId
		} ?: throw NotFoundException("Table not found")
		// check if same headers
		val formHeaders = form.headers!!.map {
			dataHeaderRepository.findOne(uidService.fromBase64(it.id!!))?.apply { name = it.name!! }
			?: throw InvalidParamException("form.headers", "id")
		}
		val headers = findHeaders(table)
		if(formHeaders.size != headers.size) throw InvalidParamException("form.headers", "size")
		// save
		dataTableRepository.saveAndFlush(table)
		dataHeaderRepository.saveAllAndFlush(formHeaders)
	}

	@Throws(NotFoundException::class)
	override fun delete(tableUid: ByteArray, currentUserEmail: String) {
		val currentUserId = userRepository.findIdByEmail(currentUserEmail)!!
		if(dataTableRepository.existsByUidIfPermission(tableUid, currentUserEmail))
			dataTableRepository.deleteByUid(tableUid, currentUserId)
		else throw NotFoundException("Table not found")
	}
}