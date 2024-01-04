package be.technobel.playzone.bll.services

import be.technobel.playzone.bll.exceptions.NotFoundException
import be.technobel.playzone.pl.models.dto.ChartDTO
import be.technobel.playzone.pl.models.dto.ProjectViewDTO
import be.technobel.playzone.pl.models.forms.ChartRequest
import be.technobel.playzone.pl.models.forms.ProjectViewForm
import org.springframework.data.domain.Page

interface ProjectViewService {
	@Throws(NotFoundException::class)
	fun findAll(projectUid: ByteArray, page: Int, size: Int, currentUserEmail: String): Page<ProjectViewDTO>

	@Throws(NotFoundException::class)
	fun findOne(projectUid: ByteArray, viewUid: ByteArray, currentUserEmail: String): ProjectViewDTO

	/** @return l'id de la ressource créée */
	@Throws(NotFoundException::class)
	fun create(projectUid: ByteArray, form: ProjectViewForm, currentUserEmail: String): String

	@Throws(NotFoundException::class)
	fun update(projectUid: ByteArray, viewUid: ByteArray, form: ProjectViewForm, currentUserEmail: String)

	@Throws(NotFoundException::class)
	fun delete(projectUid: ByteArray, viewUid: ByteArray, currentUserEmail: String)

	@Throws(NotFoundException::class)
	fun getChart(projectUid: ByteArray, viewUid: ByteArray, currentUserEmail: String): ChartDTO

	@Throws(NotFoundException::class)
	fun getChart(projectUid: ByteArray, chartRequest: ChartRequest, currentUserEmail: String): ChartDTO
}