package be.technobel.playzone.bll.services.impl

import be.technobel.playzone.bll.exceptions.AlreadyExistsException
import be.technobel.playzone.bll.exceptions.NotFoundException
import be.technobel.playzone.bll.services.UserService
import be.technobel.playzone.dal.models.entities.HistoryLog
import be.technobel.playzone.dal.models.entities.User
import be.technobel.playzone.dal.repositories.HistoryLogins
import be.technobel.playzone.dal.repositories.UserRepository
import be.technobel.playzone.dal.utils.escapeLikePattern
import be.technobel.playzone.pl.models.dto.ProfileDTO
import be.technobel.playzone.pl.models.dto.UserDTO
import be.technobel.playzone.pl.models.dto.toDTO
import be.technobel.playzone.pl.models.dto.toProfileDTO
import be.technobel.playzone.pl.models.forms.UserFormCreate
import be.technobel.playzone.pl.models.forms.UserFormPatch
import be.technobel.playzone.pl.rest.InvalidParamException
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class UserServiceImpl(
	private val userRepository: UserRepository,
	private val passwordEncoder: PasswordEncoder,
	private val uidService: UidService,
) : UserService {
	private fun getLogIds(createdBy: Long, updatedBy: Long): HistoryLogins {
		return userRepository.getLogIds(createdBy, updatedBy)
	}

	@Throws(NotFoundException::class)
	override fun findByEmail(email: String): UserDTO {
		return userRepository.findByEmail(email)?.toDTO(uidService::toBase64, ::getLogIds)
		?: throw NotFoundException("User not found")
	}

	@Throws(NotFoundException::class)
	override fun profileByEmail(email: String): ProfileDTO {
		return userRepository.findByEmail(email)?.toProfileDTO(uidService::toBase64)
		?: throw NotFoundException("User not found")
	}

	override fun findEmails(email: String): List<String> {
		val pageable = PageRequest.of(0, 10, Sort.by("email"))
		return userRepository.findEmails(pageable, escapeLikePattern(email)).content
	}

	override fun findAll(page: Int, size: Int): Page<UserDTO> {
		val pageable = PageRequest.of(page, size, Sort.by("email"))
		return userRepository.find(pageable).map {
			it.toDTO(uidService::toBase64, ::getLogIds)
		}
	}

	@Throws(NotFoundException::class)
	override fun findOne(userUid: ByteArray): UserDTO {
		return userRepository.findByUid(userUid)?.toDTO(uidService::toBase64, ::getLogIds)
		?: throw NotFoundException("User not found")
	}

	@Throws(AlreadyExistsException::class)
	override fun create(form: UserFormCreate, currentUserEmail: String): String {
		val currentUserId = userRepository.findIdByEmail(currentUserEmail)!!
		if(userRepository.existsByEmail(form.email!!)) throw AlreadyExistsException("User already exists")
		return userRepository.saveAndFlush(
			User().apply {
				uid = uidService.generate()
				email = form.email
				firstName = form.firstName!!
				lastName = form.lastName!!
				passwordHash = passwordEncoder.encode(form.password!!)
				role = form.role!!
				isActivated = form.isActivated ?: true
				log = HistoryLog().apply {
					createdAt = LocalDateTime.now()
					updatedAt = createdAt
					createdBy = currentUserId
					updatedBy = createdBy
				}
			}
		).run { uidService.toBase64(uid) }
	}

	@Throws(NotFoundException::class, AlreadyExistsException::class)
	override fun patch(userUid: ByteArray, form: UserFormPatch, currentUserEmail: String) {
		val currentUserId = userRepository.findIdByEmail(currentUserEmail)!!
		userRepository.saveAndFlush(
			userRepository.findByUid(userUid)?.apply {
				if(form.email != null) {
					if(email != form.email && userRepository.existsByEmail(form.email)) {
						throw AlreadyExistsException("User already exists")
					}
					email = form.email
				}
				if(form.firstName != null) firstName = form.firstName
				if(form.lastName != null) lastName = form.lastName
				if(form.role != null) role = form.role
				if(form.isActivated != null) isActivated = form.isActivated
				if(form.password != null) {
					if(form.password.trim().isBlank()) throw InvalidParamException("password", "not_blank")
					passwordHash = passwordEncoder.encode(form.password)
				}
				log.updatedAt = LocalDateTime.now()
				log.updatedBy = currentUserId
			} ?: throw NotFoundException("User not found")
		)
	}

	@Throws(NotFoundException::class)
	override fun delete(userUid: ByteArray, currentUserEmail: String) {
		val currentUserId = userRepository.findIdByEmail(currentUserEmail)!!
		if(userRepository.existsByUid(userUid)) userRepository.deleteByUid(userUid, currentUserId)
		else throw NotFoundException("User not found")
	}
}