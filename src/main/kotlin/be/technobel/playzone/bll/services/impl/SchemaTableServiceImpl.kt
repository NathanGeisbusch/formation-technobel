package be.technobel.playzone.bll.services.impl

import be.technobel.playzone.bll.exceptions.NotFoundException
import be.technobel.playzone.bll.services.SchemaTableService
import be.technobel.playzone.dal.models.entities.*
import be.technobel.playzone.dal.repositories.*
import be.technobel.playzone.pl.models.dto.SchemaTableDTO
import be.technobel.playzone.pl.models.dto.toDTO
import be.technobel.playzone.pl.models.forms.SchemaHeaderFormPatch
import be.technobel.playzone.pl.models.forms.SchemaTableForm
import be.technobel.playzone.pl.models.forms.SchemaTableFormPatch
import be.technobel.playzone.pl.rest.InvalidParamException
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class SchemaTableServiceImpl(
	private val schemaTableRepository: SchemaTableRepository,
	private val schemaHeaderRepository: SchemaHeaderRepository,
	private val dataTableRepository: DataTableRepository,
	private val dataHeaderRepository: DataHeaderRepository,
	private val projectRepository: ProjectRepository,
	private val userRepository: UserRepository,
	private val uidService: UidService,
) : SchemaTableService {
	@Throws(NotFoundException::class)
	private fun getTable(schemaTable: SchemaTable): DataTable {
		return dataTableRepository.findByUid(schemaTable.dataTable.uid)
			?: throw NotFoundException("Table not found")
	}

	private fun getHeaders(schemaTable: SchemaTable): List<Pair<SchemaHeader, DataHeader>> {
		return schemaHeaderRepository.findHeaders(schemaTable)
	}

	private fun getFkUid(fkTable: Long, fkField: Long): Pair<ByteArray, ByteArray>? {
		return schemaHeaderRepository.fkIdToUid(fkTable, fkField)
	}

	@Throws(NotFoundException::class)
	override fun getTablesId(projectUid: ByteArray, currentUserEmail: String): List<String> {
		if(!projectRepository.existsByUidIfPermission(projectUid, currentUserEmail))
			throw NotFoundException("Project not found")
		return schemaTableRepository.getTablesId(projectUid)
			.map { uidService.toBase64(it) }.sortedBy { it }
	}

	@Throws(NotFoundException::class)
	override fun getDeletedTablesId(projectUid: ByteArray, currentUserEmail: String): List<String> {
		if(!projectRepository.existsByUidIfPermission(projectUid, currentUserEmail))
			throw NotFoundException("Project not found")
		return schemaTableRepository.getDeletedTablesId(projectUid)
			.map { uidService.toBase64(it) }.sortedBy { it }
	}

	@Throws(NotFoundException::class)
	override fun get(projectUid: ByteArray, currentUserEmail: String): List<SchemaTableDTO> {
		if(!projectRepository.existsByUidIfPermission(projectUid, currentUserEmail))
			throw NotFoundException("Project not found")
		return schemaTableRepository.find(projectUid).map {
			it.toDTO(uidService::toBase64, userRepository::getLogIds, ::getTable, ::getHeaders, ::getFkUid)
		}
	}

	@Throws(NotFoundException::class, InvalidParamException::class)
	override fun update(projectUid: ByteArray, form: List<SchemaTableForm>, currentUserEmail: String) {
		val currentUserId = userRepository.findIdByEmail(currentUserEmail)!!
		// get tables
		val project = projectRepository.findByUidIfPermission(projectUid, currentUserEmail)
			?: throw NotFoundException("Project not found")
		val schemaTables = schemaTableRepository.find(projectUid)
		val schemaTablesId = schemaTables.map { uidService.toBase64(it.dataTable.uid) }.toSet()
		val formTablesId = form.map { it.id!! }.toSet()
		// check form
		val toDelete = schemaTablesId.filter { it !in formTablesId }.map { uidService.fromBase64(it) }
		val toSave = mutableListOf<Pair<SchemaTable, List<SchemaHeader>>>()
		for(formTable in form) {
			// check table id
			val tableUid = uidService.fromBase64(formTable.id!!)
			val table = dataTableRepository.findByUid(tableUid)
				?: throw InvalidParamException("form[].id", "not_found")
			val currentSchemaTable = schemaTables.find { uidService.toBase64(it.dataTable.uid) == formTable.id }
			// not in db but in form (create)
			if(currentSchemaTable == null) {
				// to entity
				val schemaTable = SchemaTable().apply {
					this.project = project
					this.dataTable = table
					this.fact = formTable.fact!!
					this.coordX = formTable.coord!!.x!!
					this.coordY = formTable.coord.y!!
					this.log = HistoryLog().apply {
						createdAt = LocalDateTime.now()
						updatedAt = createdAt
						createdBy = currentUserId
						updatedBy = createdBy
					}
				}
				// headers
				val schemaHeaders = formTable.headers!!.map {
					// check header id
					val headerUid = uidService.fromBase64(it.id!!)
					val header = dataHeaderRepository.findOne(headerUid)
						?: throw InvalidParamException("form[].headers[].id", "not_found")
					// to entity
					SchemaHeader().apply {
						this.schemaTable = schemaTable
						this.dataHeader = header
						this.isPK = it.pk!!
						if(it.fk == null) {
							this.fkTable = null
							this.fkField = null
						} else {
							val fk = schemaHeaderRepository.fkUidToId(
								uidService.fromBase64(it.fk.table!!),
								uidService.fromBase64(it.fk.field!!),
							) ?: throw InvalidParamException("form[].headers[].fk", "not_found")
							this.fkTable = fk.first
							this.fkField = fk.second
						}
					}
				}
				if(schemaHeaders.size != dataHeaderRepository.find(table).size)
					throw InvalidParamException("form[].headers", "size")
				toSave.add(Pair(schemaTable, schemaHeaders))
			}
			// in db and in form (update)
			else {
				// update entity
				currentSchemaTable.apply {
					fact = formTable.fact!!
					coordX = formTable.coord!!.x!!
					coordY = formTable.coord.y!!
					log.apply {
						updatedAt = LocalDateTime.now()
						updatedBy = currentUserId
					}
				}
				// headers
				val schemaHeaders = formTable.headers!!.map {
					// check header id
					val headerUid = uidService.fromBase64(it.id!!)
					val header = dataHeaderRepository.findOne(headerUid)
						?: throw InvalidParamException("form[].headers[].id", "not_found")
					val schemaHeader = schemaHeaderRepository.findOne(currentSchemaTable, header)
						?: throw NotFoundException("Schema header not found")
					// to entity
					schemaHeader.apply {
						this.isPK = it.pk!!
						if(it.fk == null) {
							this.fkTable = null
							this.fkField = null
						} else {
							val fk = schemaHeaderRepository.fkUidToId(
								uidService.fromBase64(it.fk.table!!),
								uidService.fromBase64(it.fk.field!!),
							) ?: throw InvalidParamException("form[].headers[].fk", "not_found")
							this.fkTable = fk.first
							this.fkField = fk.second
						}
					}
				}
				if(schemaHeaders.size != dataHeaderRepository.find(table).size)
					throw InvalidParamException("form[].headers", "size")
				toSave.add(Pair(currentSchemaTable, schemaHeaders))
			}
		}
		// save
		schemaTableRepository.delete(projectUid, toDelete, currentUserId)
		schemaTableRepository.saveAllAndFlush(toSave.map { it.first })
		schemaHeaderRepository.saveAllAndFlush(toSave.map { it.second }.flatten())
	}

	@Throws(NotFoundException::class)
	override fun addTableToProject(projectUid: ByteArray, tableUid: ByteArray, currentUserEmail: String) {
		// check if exists
		val project = projectRepository.findByUidIfPermission(projectUid, currentUserEmail)
			?: throw NotFoundException("Project not found")
		val table = dataTableRepository.findByUid(tableUid)
			?: throw NotFoundException("Table not found")
		if(schemaTableRepository.exists(projectUid, tableUid)) return
		val currentUserId = userRepository.findIdByEmail(currentUserEmail)!!
		// save table
		val schemaTable = schemaTableRepository.saveAndFlush(
			SchemaTable().apply {
				this.project = project
				this.dataTable = table
				this.log = HistoryLog().apply {
					createdAt = LocalDateTime.now()
					updatedAt = createdAt
					createdBy = currentUserId
					updatedBy = createdBy
				}
			}
		)
		// save headers
		val headers = dataHeaderRepository.find(table).map {
			SchemaHeader().apply {
				this.schemaTable = schemaTable
				this.dataHeader = it
			}
		}
		schemaHeaderRepository.saveAllAndFlush(headers)
	}

	@Throws(NotFoundException::class)
	override fun removeTableFromProject(projectUid: ByteArray, tableUid: ByteArray, currentUserEmail: String) {
		if(!projectRepository.existsByUidIfPermission(projectUid, currentUserEmail))
			throw NotFoundException("Project not found")
		if(!dataTableRepository.existsByUid(tableUid))
			throw NotFoundException("Table not found")
		if(!schemaTableRepository.exists(projectUid, tableUid))
			throw NotFoundException("Schema table not found")
		val currentUserId = userRepository.findIdByEmail(currentUserEmail)!!
		schemaTableRepository.delete(projectUid, tableUid, currentUserId)
	}

	@Throws(NotFoundException::class)
	override fun patchTable(projectUid: ByteArray, tableUid: ByteArray, form: SchemaTableFormPatch, currentUserEmail: String) {
		if(!projectRepository.existsByUidIfPermission(projectUid, currentUserEmail))
			throw NotFoundException("Project not found")
		if(!dataTableRepository.existsByUid(tableUid))
			throw NotFoundException("Table not found")
		val schemaTable = schemaTableRepository.findOne(projectUid, tableUid)
			?: throw NotFoundException("Schema table not found")
		schemaTable.apply {
			if(form.fact != null) fact = form.fact
			if(form.coord != null) {
				coordX = form.coord.x!!
				coordY = form.coord.y!!
			}
		}
		schemaTableRepository.saveAndFlush(schemaTable)
	}

	@Throws(NotFoundException::class)
	override fun patchHeader(projectUid: ByteArray, headerId: ByteArray, form: SchemaHeaderFormPatch, currentUserEmail: String) {
		if(!projectRepository.existsByUidIfPermission(projectUid, currentUserEmail))
			throw NotFoundException("Project not found")
		if(!dataHeaderRepository.exists(headerId))
			throw NotFoundException("Header not found")
		val schemaHeader = schemaHeaderRepository.findOneByProject(projectUid, headerId)
			?: throw NotFoundException("Schema header not found")
		schemaHeader.apply {
			if(form.pk != null) isPK = form.pk
			if(form.fk != null) {
				val fk = schemaHeaderRepository.fkUidToId(
					uidService.fromBase64(form.fk.table!!),
					uidService.fromBase64(form.fk.field!!),
				) ?: throw InvalidParamException("form[].headers[].fk", "not_found")
				fkTable = fk.first
				fkField = fk.second
			}
		}
		schemaHeaderRepository.saveAndFlush(schemaHeader)
	}
}