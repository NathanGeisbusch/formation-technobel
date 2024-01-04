package be.technobel.playzone.bll.services

import be.technobel.playzone.bll.exceptions.NotFoundException
import be.technobel.playzone.pl.models.dto.TablePermissionDTO
import be.technobel.playzone.pl.models.dto.TablePermissionsDTO
import be.technobel.playzone.pl.models.forms.TablePermissionForm
import org.springframework.data.domain.Page

interface TablePermissionService {
	@Throws(NotFoundException::class)
	fun findAll(tableUid: ByteArray, page: Int, size: Int, currentUserEmail: String): Page<TablePermissionsDTO>

	@Throws(NotFoundException::class)
	fun findOne(tableUid: ByteArray, currentUserEmail: String, otherUserEmail: String): TablePermissionDTO

	@Throws(NotFoundException::class)
	fun set(tableUid: ByteArray, form: TablePermissionForm, currentUserEmail: String, otherUserEmail: String)

	@Throws(NotFoundException::class)
	fun delete(tableUid: ByteArray, currentUserEmail: String, otherUserEmail: String)
}