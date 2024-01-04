package be.technobel.playzone.bll.services

import be.technobel.playzone.bll.exceptions.NotFoundException
import be.technobel.playzone.pl.models.dto.DataRowDTO
import be.technobel.playzone.pl.models.forms.DataRowForm
import be.technobel.playzone.pl.models.forms.DataRowFormUpdates
import org.springframework.data.domain.Page

interface DataRowService {
	@Throws(NotFoundException::class)
	fun findAll(tableUid: ByteArray, page: Int, size: Int, currentUserEmail: String): Page<DataRowDTO>

	@Throws(NotFoundException::class)
	fun findOne(tableUid: ByteArray, dataUid: ByteArray, currentUserEmail: String): DataRowDTO

	/** @return l'id de la ressource créée */
	@Throws(NotFoundException::class)
	fun create(tableUid: ByteArray, form: List<DataRowForm>, currentUserEmail: String): List<String>

	@Throws(NotFoundException::class)
	fun update(tableUid: ByteArray, form: List<DataRowFormUpdates>, currentUserEmail: String)

	@Throws(NotFoundException::class)
	fun update(tableUid: ByteArray, dataUid: ByteArray, form: DataRowForm, currentUserEmail: String)

	@Throws(NotFoundException::class)
	fun delete(tableUid: ByteArray, form: List<String>, currentUserEmail: String)

	@Throws(NotFoundException::class)
	fun delete(tableUid: ByteArray, dataUid: ByteArray, currentUserEmail: String)

	/** Récupère maximum 20 valeurs pour la colonne en paramètre (contenant valeur en paramètre) */
	@Throws(NotFoundException::class)
	fun searchValues(tableUid: ByteArray, headerUid: ByteArray, value: String): List<String>
}