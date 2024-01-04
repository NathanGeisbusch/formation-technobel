package be.technobel.playzone.bll.services.impl

import be.technobel.playzone.bll.exceptions.NotFoundException
import be.technobel.playzone.bll.services.ProjectViewService
import be.technobel.playzone.dal.models.entities.HistoryLog
import be.technobel.playzone.dal.models.entities.ProjectView
import be.technobel.playzone.dal.repositories.*
import be.technobel.playzone.pl.models.dto.ChartDTO
import be.technobel.playzone.pl.models.dto.ProjectViewDTO
import be.technobel.playzone.pl.models.dto.toDTO
import be.technobel.playzone.pl.models.forms.ChartRequest
import be.technobel.playzone.pl.models.forms.ProjectViewForm
import be.technobel.playzone.pl.models.forms.TableHeaderUid
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class ProjectViewServiceImpl(
	private val chartService: ChartService,
	private val projectViewRepository: ProjectViewRepository,
	private val projectRepository: ProjectRepository,
	private val userRepository: UserRepository,
	private val uidService: UidService,
) : ProjectViewService {
	@Throws(NotFoundException::class)
	override fun findAll(projectUid: ByteArray, page: Int, size: Int, currentUserEmail: String): Page<ProjectViewDTO> {
		val projectId = projectRepository.findIdByUidIfPermission(projectUid, currentUserEmail)
			?: throw NotFoundException("Project not found")
		val pageable = PageRequest.of(page, size, Sort.by("log.updatedAt"))
		return projectViewRepository.find(pageable, projectId).map {
			it.toDTO(uidService::toBase64, userRepository::getLogIds)
		}
	}

	@Throws(NotFoundException::class)
	override fun findOne(projectUid: ByteArray, viewUid: ByteArray, currentUserEmail: String): ProjectViewDTO {
		val projectId = projectRepository.findIdByUidIfPermission(projectUid, currentUserEmail)
			?: throw NotFoundException("Project not found")
		return projectViewRepository.findOne(projectId, viewUid)
			?.toDTO(uidService::toBase64, userRepository::getLogIds)
			?: throw NotFoundException("Project view not found")
	}

	@Throws(NotFoundException::class)
	override fun create(projectUid: ByteArray, form: ProjectViewForm, currentUserEmail: String): String {
		val currentUserId = userRepository.findIdByEmail(currentUserEmail)!!
		return projectViewRepository.saveAndFlush(
			ProjectView().apply {
				uid = uidService.generate()
				project = projectRepository
					.findByUidIfPermission(projectUid, currentUserEmail)
					?: throw NotFoundException("Project not found")
				chartType = form.chart
				labelTableUid = uidService.fromBase64(form.label!!.table!!)
				labelFieldUid = uidService.fromBase64(form.label.field!!)
				if(form.data == null) {
					dataTableUid = null
					dataFieldUid = null
					pkValue = null
				} else {
					dataTableUid = uidService.fromBase64(form.data.table!!)
					dataFieldUid = uidService.fromBase64(form.data.field!!)
					value = form.data.value
					pkValue = form.data.pkValue
				}
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
	override fun update(projectUid: ByteArray, viewUid: ByteArray, form: ProjectViewForm, currentUserEmail: String) {
		val projectId = projectRepository.findIdByUidIfPermission(projectUid, currentUserEmail)
			?: throw NotFoundException("Project not found")
		val currentUserId = userRepository.findIdByEmail(currentUserEmail)!!
		projectViewRepository.saveAndFlush(
			projectViewRepository.findOne(projectId, viewUid)?.apply {
				chartType = form.chart
				labelTableUid = uidService.fromBase64(form.label!!.table!!)
				labelFieldUid = uidService.fromBase64(form.label.field!!)
				if(form.data == null) {
					dataTableUid = null
					dataFieldUid = null
					pkValue = null
				} else {
					dataTableUid = uidService.fromBase64(form.data.table!!)
					dataFieldUid = uidService.fromBase64(form.data.field!!)
					value = form.data.value
					pkValue = form.data.pkValue
				}
				log.updatedAt = LocalDateTime.now()
				log.updatedBy = currentUserId
			} ?: throw NotFoundException("Project view not found")
		)
	}

	@Throws(NotFoundException::class)
	override fun delete(projectUid: ByteArray, viewUid: ByteArray, currentUserEmail: String) {
		val projectId = projectRepository.findIdByUidIfPermission(projectUid, currentUserEmail)
			?: throw NotFoundException("Project not found")
		val currentUserId = userRepository.findIdByEmail(currentUserEmail)!!
		if(projectViewRepository.exists(projectId, viewUid))
			projectViewRepository.delete(projectId, viewUid, currentUserId)
		else throw NotFoundException("Project view not found")
	}

	@Throws(NotFoundException::class)
	override fun getChart(projectUid: ByteArray, viewUid: ByteArray, currentUserEmail: String): ChartDTO {
		val projectId = projectRepository.findIdByUidIfPermission(projectUid, currentUserEmail)
			?: throw NotFoundException("Project not found")
		val view = projectViewRepository.findOne(projectId, viewUid)
			?: throw NotFoundException("Project view not found")
		val chartRequest = ChartRequest(
			chart = view.chartType,
			label = TableHeaderUid(view.labelTableUid, view.labelFieldUid),
			data =
				if(view.dataTableUid == null || view.dataFieldUid == null) null
				else TableHeaderUid(view.dataTableUid!!, view.dataFieldUid!!),
			value = view.value,
			pkValue = view.pkValue
		)
		return chartService.getChart(projectId, chartRequest)
	}

	@Throws(NotFoundException::class)
	override fun getChart(projectUid: ByteArray, chartRequest: ChartRequest, currentUserEmail: String): ChartDTO {
		val projectId = projectRepository.findIdByUidIfPermission(projectUid, currentUserEmail)
			?: throw NotFoundException("Project not found")
		return chartService.getChart(projectId, chartRequest)
	}
}
