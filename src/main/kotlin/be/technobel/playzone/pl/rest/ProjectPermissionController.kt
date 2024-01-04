package be.technobel.playzone.pl.rest

import be.technobel.playzone.bll.services.ProjectPermissionService
import be.technobel.playzone.bll.services.impl.UidService
import be.technobel.playzone.pl.models.dto.PagesDTO
import be.technobel.playzone.pl.models.dto.ProjectPermissionDTO
import be.technobel.playzone.pl.models.dto.ProjectPermissionsDTO
import be.technobel.playzone.pl.models.forms.ProjectPermissionForm
import be.technobel.playzone.pl.validation.constraints.Positive
import be.technobel.playzone.pl.validation.constraints.PositiveOrZero
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.Authentication
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/projects")
@Validated
class ProjectPermissionController(
	val service: ProjectPermissionService,
	val uidService: UidService,
) {
	@PreAuthorize("isAuthenticated()")
	@GetMapping("/{id}/permissions", params = ["page", "size"])
	fun findPermissions(
		@PathVariable id: String,
		@RequestParam @PositiveOrZero("page") page: Int,
		@RequestParam @Positive("size") size: Int,
		auth: Authentication,
	): ResponseEntity<PagesDTO<ProjectPermissionsDTO>> {
		val uid = uidService.fromBase64Validated(id)
		val dtoPage = service.findAll(uid, page, size, auth.name)
		val dto = PagesDTO(dtoPage.totalPages, dtoPage.number, dtoPage.size, dtoPage.content)
		return ResponseEntity.ok(dto)
	}

	@PreAuthorize("isAuthenticated()")
	@GetMapping("/{id}/permissions/{emailBase64}")
	fun findOnePermissionById(
		@PathVariable id: String,
		@PathVariable emailBase64: String,
		auth: Authentication,
	): ResponseEntity<ProjectPermissionDTO> {
		val uid = uidService.fromBase64Validated(id)
		val userEmail = uidService.decodeEmailBase64Validated(emailBase64)
		val dto = service.findOne(uid, auth.name, userEmail)
		return ResponseEntity.ok(dto)
	}

	@PreAuthorize("isAuthenticated()")
	@PostMapping("/{id}/permissions/{emailBase64}")
	fun setPermission(
		@PathVariable id: String,
		@PathVariable emailBase64: String,
		@RequestBody @Valid form: ProjectPermissionForm,
		auth: Authentication,
	): ResponseEntity<Void> {
		val uid = uidService.fromBase64Validated(id)
		val userEmail = uidService.decodeEmailBase64Validated(emailBase64)
		service.set(uid, form, auth.name, userEmail)
		return ResponseEntity.noContent().build()
	}

	@PreAuthorize("isAuthenticated()")
	@DeleteMapping("/{id}/permissions/{emailBase64}")
	fun deletePermission(
		@PathVariable id: String,
		@PathVariable emailBase64: String,
		auth: Authentication,
	): ResponseEntity<Void> {
		val uid = uidService.fromBase64Validated(id)
		val userEmail = uidService.decodeEmailBase64Validated(emailBase64)
		service.delete(uid, auth.name, userEmail)
		return ResponseEntity.noContent().build()
	}
}