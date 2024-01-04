package be.technobel.playzone.bll.services.impl

import be.technobel.playzone.bll.exceptions.NotFoundException
import be.technobel.playzone.bll.services.TablePermissionService
import be.technobel.playzone.dal.models.entities.TablePermission
import be.technobel.playzone.dal.repositories.*
import be.technobel.playzone.pl.models.dto.TablePermissionDTO
import be.technobel.playzone.pl.models.dto.TablePermissionsDTO
import be.technobel.playzone.pl.models.dto.toDTO
import be.technobel.playzone.pl.models.dto.toEmailDTO
import be.technobel.playzone.pl.models.forms.TablePermissionForm
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service

@Service
class TablePermissionServiceImpl(
	private val tablePermissionRepository: TablePermissionRepository,
	private val dataTableRepository: DataTableRepository,
	private val userRepository: UserRepository,
) : TablePermissionService {
	override fun findAll(
		tableUid: ByteArray,
		page: Int,
		size: Int,
		currentUserEmail: String
	): Page<TablePermissionsDTO> {
		if(!dataTableRepository.existsByUidIfPermission(tableUid, currentUserEmail))
			throw NotFoundException("Table not found")
		val pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "user.email"))
		return tablePermissionRepository.findAll(pageable).map { it.toEmailDTO() }
	}

	override fun findOne(tableUid: ByteArray, currentUserEmail: String, otherUserEmail: String): TablePermissionDTO {
		if(!dataTableRepository.existsByUidIfPermission(tableUid, currentUserEmail))
			throw NotFoundException("Table not found")
		if(!userRepository.existsByEmail(otherUserEmail))
			throw NotFoundException("User not found")
		return tablePermissionRepository.findOne(tableUid, otherUserEmail)?.toDTO()
			?: throw NotFoundException("Table permission not found")
	}

	override fun set(tableUid: ByteArray, form: TablePermissionForm, currentUserEmail: String, otherUserEmail: String) {
		val user = userRepository.findByEmail(otherUserEmail)
			?: throw NotFoundException("User not found")
		val table = dataTableRepository.findByUidIfPermission(tableUid, currentUserEmail)
			?: throw NotFoundException("Table not found")
		val permission = tablePermissionRepository.findOne(tableUid, otherUserEmail)
		if(permission == null) {
			tablePermissionRepository.saveAndFlush(
				TablePermission().apply {
					this.user = user
					this.table = table
				}
			)
		}
	}

	override fun delete(tableUid: ByteArray, currentUserEmail: String, otherUserEmail: String) {
		if(!userRepository.existsByEmail(otherUserEmail))
			throw NotFoundException("User not found")
		if(!dataTableRepository.existsByUidIfPermission(tableUid, currentUserEmail))
			throw NotFoundException("Table not found")
		tablePermissionRepository.findOne(tableUid, otherUserEmail)?.let {
			tablePermissionRepository.delete(it)
		}
	}
}