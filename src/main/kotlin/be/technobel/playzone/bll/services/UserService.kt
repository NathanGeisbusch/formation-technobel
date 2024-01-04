package be.technobel.playzone.bll.services

import be.technobel.playzone.bll.exceptions.AlreadyExistsException
import be.technobel.playzone.bll.exceptions.NotFoundException
import be.technobel.playzone.pl.models.dto.ProfileDTO
import be.technobel.playzone.pl.models.dto.UserDTO
import be.technobel.playzone.pl.models.forms.UserFormCreate
import be.technobel.playzone.pl.models.forms.UserFormPatch
import org.springframework.data.domain.Page

interface UserService {
	@Throws(NotFoundException::class)
	fun findByEmail(email: String): UserDTO

	/** Récupère la liste des adresses email contenant la valeur en paramètre (10 maximum) */
	fun findEmails(email: String): List<String>

	fun findAll(page: Int, size: Int): Page<UserDTO>

	@Throws(NotFoundException::class)
	fun findOne(userUid: ByteArray): UserDTO

	/** @return l'id de la ressource créée */
	@Throws(AlreadyExistsException::class)
	fun create(form: UserFormCreate, currentUserEmail: String): String

	@Throws(NotFoundException::class, AlreadyExistsException::class)
	fun patch(userUid: ByteArray, form: UserFormPatch, currentUserEmail: String)

	@Throws(NotFoundException::class)
	fun delete(userUid: ByteArray, currentUserEmail: String)

	@Throws(NotFoundException::class)
	fun profileByEmail(email: String): ProfileDTO
}