package be.technobel.playzone.bll.services

import be.technobel.playzone.bll.exceptions.NotFoundException
import be.technobel.playzone.pl.models.dto.SchemaTableDTO
import be.technobel.playzone.pl.models.forms.SchemaHeaderFormPatch
import be.technobel.playzone.pl.models.forms.SchemaTableForm
import be.technobel.playzone.pl.models.forms.SchemaTableFormPatch
import be.technobel.playzone.pl.rest.InvalidParamException

interface SchemaTableService {
	/** Récupère le schéma du projet */
	@Throws(NotFoundException::class)
	fun get(projectUid: ByteArray, currentUserEmail: String): List<SchemaTableDTO>

	/** Modifie le schéma du projet */
	@Throws(NotFoundException::class, InvalidParamException::class)
	fun update(projectUid: ByteArray, form: List<SchemaTableForm>, currentUserEmail: String)

	/** Ajoute une table au schéma */
	@Throws(NotFoundException::class)
	fun addTableToProject(projectUid: ByteArray, tableUid: ByteArray, currentUserEmail: String)

	/** Retire une table du schéma */
	@Throws(NotFoundException::class)
	fun removeTableFromProject(projectUid: ByteArray, tableUid: ByteArray, currentUserEmail: String)

	/** Modifie les métadonnées d'une table dans le schéma */
	@Throws(NotFoundException::class)
	fun patchTable(projectUid: ByteArray, tableUid: ByteArray, form: SchemaTableFormPatch, currentUserEmail: String)

	/** Modifie les métadonnées d'une colonne dans le schéma */
	@Throws(NotFoundException::class)
	fun patchHeader(projectUid: ByteArray, headerId: ByteArray, form: SchemaHeaderFormPatch, currentUserEmail: String)

	/** Récupère la liste des tables présentes dans le schéma */
	@Throws(NotFoundException::class)
	fun getTablesId(projectUid: ByteArray, currentUserEmail: String): List<String>

	/** Récupère la liste des tables supprimées présentes dans le schéma */
	@Throws(NotFoundException::class)
	fun getDeletedTablesId(projectUid: ByteArray, currentUserEmail: String): List<String>
}