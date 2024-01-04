package be.technobel.playzone.bll.services.impl

import be.technobel.playzone.bll.exceptions.NotFoundException
import be.technobel.playzone.bll.services.ProjectService
import be.technobel.playzone.dal.models.entities.HistoryLog
import be.technobel.playzone.dal.models.entities.Project
import be.technobel.playzone.dal.repositories.ProjectRepository
import be.technobel.playzone.dal.repositories.UserRepository
import be.technobel.playzone.pl.models.dto.ProjectDTO
import be.technobel.playzone.pl.models.dto.toDTO
import be.technobel.playzone.pl.models.forms.ProjectFormCreate
import be.technobel.playzone.pl.models.forms.ProjectFormUpdate
import be.technobel.playzone.pl.rest.ProjectVisibility
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class ProjectServiceImpl(
	private val projectRepository: ProjectRepository,
	private val userRepository: UserRepository,
	private val uidService: UidService,
) : ProjectService {
	override fun findAll(page: Int, size: Int, currentUserEmail: String): Page<ProjectDTO> {
		val pageable = PageRequest.of(page, size, Sort.by("name"))
		return projectRepository.find(pageable).map {
			it.toDTO(uidService::toBase64, userRepository::getLogIds)
		}
	}

	override fun findAll(page: Int, size: Int, currentUserEmail: String, visibility: ProjectVisibility): Page<ProjectDTO> {
		val pageable = PageRequest.of(page, size, Sort.by("name"))
		return when(visibility) {
			ProjectVisibility.OWNER  -> projectRepository.findOwn(pageable, currentUserEmail)
			ProjectVisibility.SHARED -> projectRepository.findShared(pageable, currentUserEmail)
			ProjectVisibility.PUBLIC -> projectRepository.findPublic(pageable)
		}.map {
			it.toDTO(uidService::toBase64, userRepository::getLogIds)
		}
	}

	@Throws(NotFoundException::class)
	override fun findOne(projectUid: ByteArray, currentUserEmail: String): ProjectDTO {
		return projectRepository.findByUidIfPermissionOrPublic(projectUid, currentUserEmail)
			?.toDTO(uidService::toBase64, userRepository::getLogIds)
			?: throw NotFoundException("Project not found")
	}

	override fun create(form: ProjectFormCreate, currentUserEmail: String): String {
		val currentUserId = userRepository.findIdByEmail(currentUserEmail)!!
		return projectRepository.saveAndFlush(
			Project().apply {
				uid = uidService.generate()
				name = form.name!!
				description = form.description!!
				log = HistoryLog().apply {
					createdAt = LocalDateTime.now()
					updatedAt = createdAt
					createdBy = currentUserId
					updatedBy = createdBy
				}
			}
		).run { uidService.toBase64(uid) }
	}

	@Throws(NotFoundException::class)
	override fun update(projectUid: ByteArray, form: ProjectFormUpdate, currentUserEmail: String) {
		val currentUserId = userRepository.findIdByEmail(currentUserEmail)!!
		projectRepository.saveAndFlush(
			projectRepository.findByUidIfPermission(projectUid, currentUserEmail)?.apply {
				name = form.name!!
				description = form.description!!
				isPublic = form.isPublic!!
				log.updatedAt = LocalDateTime.now()
				log.updatedBy = currentUserId
			} ?: throw NotFoundException("Project not found")
		)
	}

	@Throws(NotFoundException::class)
	override fun delete(projectUid: ByteArray, currentUserEmail: String) {
		val currentUserId = userRepository.findIdByEmail(currentUserEmail)!!
		if(projectRepository.existsByUidIfPermission(projectUid, currentUserEmail))
			projectRepository.deleteByUid(projectUid, currentUserId)
		else throw NotFoundException("Project not found")
	}
}