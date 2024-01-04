package be.technobel.playzone.pl.rest

import be.technobel.playzone.bll.services.ProjectService
import be.technobel.playzone.bll.services.impl.UidService
import be.technobel.playzone.pl.models.dto.CreatedDTO
import be.technobel.playzone.pl.models.dto.PagesDTO
import be.technobel.playzone.pl.models.dto.ProjectDTO
import be.technobel.playzone.pl.models.forms.ProjectFormCreate
import be.technobel.playzone.pl.models.forms.ProjectFormUpdate
import be.technobel.playzone.pl.validation.constraints.Positive
import be.technobel.playzone.pl.validation.constraints.PositiveOrZero
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.Authentication
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import java.net.URI

enum class ProjectVisibility {OWNER, SHARED, PUBLIC}

@RestController
@RequestMapping("/api/projects")
@Validated
class ProjectController(val service: ProjectService, val uidService: UidService) {
	@PreAuthorize("hasRole('ADMIN')")
	@GetMapping(params = ["page", "size"])
	fun find(
		@RequestParam @PositiveOrZero("page") page: Int,
		@RequestParam @Positive("size") size: Int,
		auth: Authentication,
	): ResponseEntity<PagesDTO<ProjectDTO>> {
		val dtoPage = service.findAll(page, size, auth.name)
		val dto = PagesDTO(dtoPage.totalPages, dtoPage.number, dtoPage.size, dtoPage.content)
		return ResponseEntity.ok(dto)
	}

	@PreAuthorize("isAuthenticated()")
	@GetMapping(params = ["page", "size", "from"])
	fun find(
		@RequestParam @PositiveOrZero("page") page: Int,
		@RequestParam @Positive("size") size: Int,
		@RequestParam from: ProjectVisibility,
		auth: Authentication,
	): ResponseEntity<PagesDTO<ProjectDTO>> {
		val dtoPage = service.findAll(page, size, auth.name, from)
		val dto = PagesDTO(dtoPage.totalPages, dtoPage.number, dtoPage.size, dtoPage.content)
		return ResponseEntity.ok(dto)
	}

	@PreAuthorize("isAuthenticated()")
	@GetMapping("/{id}")
	fun findOneById(@PathVariable id: String, auth: Authentication): ResponseEntity<ProjectDTO> {
		val uid = uidService.fromBase64Validated(id)
		val dto = service.findOne(uid, auth.name)
		return ResponseEntity.ok(dto)
	}

	@PreAuthorize("isAuthenticated()")
	@PostMapping
	fun create(
		@RequestBody @Valid form: ProjectFormCreate,
		auth: Authentication,
	): ResponseEntity<CreatedDTO> {
		val id = service.create(form, auth.name)
		return ResponseEntity.created(URI("/api/projects/${id}")).body(CreatedDTO(id))
	}

	@PreAuthorize("isAuthenticated()")
	@PutMapping("/{id}")
	fun put(
		@PathVariable id: String,
		@RequestBody @Valid form: ProjectFormUpdate,
		auth: Authentication,
	): ResponseEntity<Void> {
		val uid = uidService.fromBase64Validated(id)
		service.update(uid, form, auth.name)
		return ResponseEntity.noContent().build()
	}

	@PreAuthorize("isAuthenticated()")
	@DeleteMapping("/{id}")
	fun delete(@PathVariable id: String, auth: Authentication): ResponseEntity<Void> {
		val uid = uidService.fromBase64Validated(id)
		service.delete(uid, auth.name)
		return ResponseEntity.noContent().build()
	}
}