package be.technobel.playzone.bll.services.impl

import be.technobel.playzone.bll.exceptions.NotFoundException
import be.technobel.playzone.bll.services.ProjectPermissionService
import be.technobel.playzone.dal.models.entities.ProjectPermission
import be.technobel.playzone.dal.repositories.ProjectPermissionRepository
import be.technobel.playzone.dal.repositories.ProjectRepository
import be.technobel.playzone.dal.repositories.UserRepository
import be.technobel.playzone.pl.models.dto.ProjectPermissionDTO
import be.technobel.playzone.pl.models.dto.ProjectPermissionsDTO
import be.technobel.playzone.pl.models.dto.toDTO
import be.technobel.playzone.pl.models.dto.toEmailDTO
import be.technobel.playzone.pl.models.forms.ProjectPermissionForm
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service

@Service
class ProjectPermissionServiceImpl(
	private val projectPermissionRepository: ProjectPermissionRepository,
	private val projectRepository: ProjectRepository,
	private val userRepository: UserRepository,
) : ProjectPermissionService {
	@Throws(NotFoundException::class)
	override fun findAll(projectUid: ByteArray, page: Int, size: Int, currentUserEmail: String): Page<ProjectPermissionsDTO> {
		if(!projectRepository.existsByUidIfPermission(projectUid, currentUserEmail))
			throw NotFoundException("Project not found")
		val pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "user.email"))
		return projectPermissionRepository.findAll(pageable).map { it.toEmailDTO() }
	}

	@Throws(NotFoundException::class)
	override fun findOne(projectUid: ByteArray, currentUserEmail: String, otherUserEmail: String): ProjectPermissionDTO {
		if(!projectRepository.existsByUidIfPermission(projectUid, currentUserEmail))
			throw NotFoundException("Project not found")
		if(!userRepository.existsByEmail(otherUserEmail))
			throw NotFoundException("User not found")
		return projectPermissionRepository.findOne(projectUid, otherUserEmail)?.toDTO()
			?: throw NotFoundException("Project permission not found")
	}

	@Throws(NotFoundException::class)
	override fun set(projectUid: ByteArray, form: ProjectPermissionForm, currentUserEmail: String, otherUserEmail: String) {
		val user = userRepository.findByEmail(otherUserEmail)
			?: throw NotFoundException("User not found")
		val project = projectRepository.findByUidIfPermission(projectUid, currentUserEmail)
			?: throw NotFoundException("Project not found")
		val permission = projectPermissionRepository.findOne(projectUid, otherUserEmail)
		if(permission == null) {
			projectPermissionRepository.saveAndFlush(
				ProjectPermission().apply {
					this.user = user
					this.project = project
				}
			)
		}
	}

	@Throws(NotFoundException::class)
	override fun delete(projectUid: ByteArray, currentUserEmail: String, otherUserEmail: String) {
		if(!userRepository.existsByEmail(otherUserEmail))
			throw NotFoundException("User not found")
		if(!projectRepository.existsByUidIfPermission(projectUid, currentUserEmail))
			throw NotFoundException("Project not found")
		projectPermissionRepository.findOne(projectUid, otherUserEmail)?.let {
			projectPermissionRepository.delete(it)
		}
	}
}