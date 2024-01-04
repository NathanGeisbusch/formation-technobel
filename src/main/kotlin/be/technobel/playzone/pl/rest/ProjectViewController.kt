package be.technobel.playzone.pl.rest

import be.technobel.playzone.bll.services.ProjectViewService
import be.technobel.playzone.bll.services.impl.UidService
import be.technobel.playzone.pl.models.dto.ChartDTO
import be.technobel.playzone.pl.models.dto.CreatedDTO
import be.technobel.playzone.pl.models.dto.PagesDTO
import be.technobel.playzone.pl.models.dto.ProjectViewDTO
import be.technobel.playzone.pl.models.forms.ProjectViewForm
import be.technobel.playzone.pl.models.forms.toChartRequest
import be.technobel.playzone.pl.validation.constraints.Positive
import be.technobel.playzone.pl.validation.constraints.PositiveOrZero
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.Authentication
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import java.net.URI

@RestController
@RequestMapping("/api/projects")
@Validated
class ProjectViewController(
	private val service: ProjectViewService,
	private val uidService: UidService,
) {
	@PreAuthorize("isAuthenticated()")
	@GetMapping("/{id}/views", params = ["page", "size"])
	fun find(
		@PathVariable id: String,
		@RequestParam @PositiveOrZero("page") page: Int,
		@RequestParam @Positive("size") size: Int,
		auth: Authentication,
	): ResponseEntity<PagesDTO<ProjectViewDTO>> {
		val uid = uidService.fromBase64Validated(id)
		val dtoPage = service.findAll(uid, page, size, auth.name)
		val dto = PagesDTO(dtoPage.totalPages, dtoPage.number, dtoPage.size, dtoPage.content)
		return ResponseEntity.ok(dto)
	}

	@PreAuthorize("isAuthenticated()")
	@GetMapping("/{projectId}/views/{viewId}")
	fun findOne(
		@PathVariable projectId: String,
		@PathVariable viewId: String,
		auth: Authentication,
	): ResponseEntity<ProjectViewDTO> {
		val projectUid = uidService.fromBase64Validated(projectId)
		val viewUid = uidService.fromBase64Validated(viewId)
		val dto = service.findOne(projectUid, viewUid, auth.name)
		return ResponseEntity.ok(dto)
	}

	@PreAuthorize("isAuthenticated()")
	@PostMapping("/{id}/views")
	fun create(
		@PathVariable id: String,
		@RequestBody @Valid form: ProjectViewForm,
		auth: Authentication,
	): ResponseEntity<CreatedDTO> {
		val uid = uidService.fromBase64Validated(id)
		val dto = service.create(uid, form, auth.name)
		return ResponseEntity.created(URI("/api/projects/${id}/views/${dto}")).body(CreatedDTO(dto))
	}

	@PreAuthorize("isAuthenticated()")
	@PutMapping("/{projectId}/views/{viewId}")
	fun update(
		@PathVariable projectId: String,
		@PathVariable viewId: String,
		@RequestBody @Valid form: ProjectViewForm,
		auth: Authentication,
	): ResponseEntity<Void> {
		val projectUid = uidService.fromBase64Validated(projectId)
		val viewUid = uidService.fromBase64Validated(viewId)
		service.update(projectUid, viewUid, form, auth.name)
		return ResponseEntity.noContent().build()
	}

	@PreAuthorize("isAuthenticated()")
	@DeleteMapping("/{projectId}/views/{viewId}")
	fun delete(
		@PathVariable projectId: String,
		@PathVariable viewId: String,
		auth: Authentication,
	): ResponseEntity<Void> {
		val projectUid = uidService.fromBase64Validated(projectId)
		val viewUid = uidService.fromBase64Validated(viewId)
		service.delete(projectUid, viewUid, auth.name)
		return ResponseEntity.noContent().build()
	}

	@PreAuthorize("isAuthenticated()")
	@GetMapping("/{projectId}/views/{viewId}/chart")
	fun chart(
		@PathVariable projectId: String,
		@PathVariable viewId: String,
		auth: Authentication,
	): ResponseEntity<ChartDTO> {
		val projectUid = uidService.fromBase64Validated(projectId)
		val viewUid = uidService.fromBase64Validated(viewId)
		val dto = service.getChart(projectUid, viewUid, auth.name)
		return ResponseEntity.ok(dto)
	}

	@PreAuthorize("isAuthenticated()")
	@PostMapping("/{projectId}/chart")
	fun chart(
		@PathVariable projectId: String,
		@RequestBody @Valid form: ProjectViewForm,
		auth: Authentication,
	): ResponseEntity<ChartDTO> {
		val projectUid = uidService.fromBase64Validated(projectId)
		val dto = service.getChart(projectUid, form.toChartRequest(uidService::fromBase64), auth.name)
		return ResponseEntity.ok(dto)
	}
}