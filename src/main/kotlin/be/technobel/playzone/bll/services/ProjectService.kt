package be.technobel.playzone.bll.services

import be.technobel.playzone.bll.exceptions.NotFoundException
import be.technobel.playzone.pl.models.dto.ProjectDTO
import be.technobel.playzone.pl.models.forms.ProjectFormCreate
import be.technobel.playzone.pl.models.forms.ProjectFormUpdate
import be.technobel.playzone.pl.rest.ProjectVisibility
import org.springframework.data.domain.Page

interface ProjectService {
	fun findAll(page: Int, size: Int, currentUserEmail: String): Page<ProjectDTO>

	fun findAll(page: Int, size: Int, currentUserEmail: String, visibility: ProjectVisibility): Page<ProjectDTO>

	@Throws(NotFoundException::class)
	fun findOne(projectUid: ByteArray, currentUserEmail: String): ProjectDTO

	/** @return l'id de la ressource créée */
	fun create(form: ProjectFormCreate, currentUserEmail: String): String

	@Throws(NotFoundException::class)
	fun update(projectUid: ByteArray, form: ProjectFormUpdate, currentUserEmail: String)

	@Throws(NotFoundException::class)
	fun delete(projectUid: ByteArray, currentUserEmail: String)
}