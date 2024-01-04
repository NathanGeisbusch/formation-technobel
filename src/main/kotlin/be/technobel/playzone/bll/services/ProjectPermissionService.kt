package be.technobel.playzone.bll.services

import be.technobel.playzone.bll.exceptions.NotFoundException
import be.technobel.playzone.pl.models.dto.ProjectPermissionDTO
import be.technobel.playzone.pl.models.dto.ProjectPermissionsDTO
import be.technobel.playzone.pl.models.forms.ProjectPermissionForm
import org.springframework.data.domain.Page

interface ProjectPermissionService {
	@Throws(NotFoundException::class)
	fun findAll(projectUid: ByteArray, page: Int, size: Int, currentUserEmail: String): Page<ProjectPermissionsDTO>

	@Throws(NotFoundException::class)
	fun findOne(projectUid: ByteArray, currentUserEmail: String, otherUserEmail: String): ProjectPermissionDTO

	@Throws(NotFoundException::class)
	fun set(projectUid: ByteArray, form: ProjectPermissionForm, currentUserEmail: String, otherUserEmail: String)

	@Throws(NotFoundException::class)
	fun delete(projectUid: ByteArray, currentUserEmail: String, otherUserEmail: String)
}