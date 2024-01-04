package be.technobel.playzone.bll.services

import be.technobel.playzone.bll.exceptions.NotFoundException
import be.technobel.playzone.pl.models.dto.DataTableDTO
import be.technobel.playzone.pl.models.forms.DataTableFormCreate
import be.technobel.playzone.pl.models.forms.DataTableFormUpdate
import be.technobel.playzone.pl.rest.TableVisibility
import org.springframework.data.domain.Page

interface DataTableService {
	fun findAll(page: Int, size: Int, currentUserEmail: String): Page<DataTableDTO>

	fun findAll(page: Int, size: Int, currentUserEmail: String, visibility: TableVisibility): Page<DataTableDTO>

	@Throws(NotFoundException::class)
	fun findOne(tableUid: ByteArray, currentUserEmail: String): DataTableDTO

	/** @return l'id de la ressource créée */
	fun create(form: DataTableFormCreate, currentUserEmail: String): String

	@Throws(NotFoundException::class)
	fun update(tableUid: ByteArray, form: DataTableFormUpdate, currentUserEmail: String)

	@Throws(NotFoundException::class)
	fun delete(tableUid: ByteArray, currentUserEmail: String)
}