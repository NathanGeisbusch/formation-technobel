package be.technobel.playzone.pl.rest

import be.technobel.playzone.bll.services.SchemaTableService
import be.technobel.playzone.bll.services.impl.UidService
import be.technobel.playzone.pl.models.dto.SchemaTableDTO
import be.technobel.playzone.pl.models.forms.SchemaHeaderFormPatch
import be.technobel.playzone.pl.models.forms.SchemaTableForm
import be.technobel.playzone.pl.models.forms.SchemaTableFormPatch
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.Authentication
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/projects")
@Validated
class SchemaTableController(
	private val service: SchemaTableService,
	private val uidService: UidService,
) {
	@PreAuthorize("isAuthenticated()")
	@GetMapping("/{id}/schema")
	fun get(@PathVariable id: String, auth: Authentication): ResponseEntity<List<SchemaTableDTO>> {
		val uid = uidService.fromBase64Validated(id)
		val dto = service.get(uid, auth.name)
		return ResponseEntity.ok(dto)
	}

	@PreAuthorize("isAuthenticated()")
	@PutMapping("/{id}/schema")
	fun put(
		@PathVariable id: String,
		@RequestBody @Valid form: List<SchemaTableForm>,
		auth: Authentication,
	): ResponseEntity<Void> {
		val uid = uidService.fromBase64Validated(id)
		service.update(uid, form, auth.name)
		return ResponseEntity.noContent().build()
	}

	@PreAuthorize("isAuthenticated()")
	@GetMapping("/{id}/schema/tables")
	fun getTablesId(@PathVariable id: String, auth: Authentication): ResponseEntity<List<String>> {
		val uid = uidService.fromBase64Validated(id)
		val dto = service.getTablesId(uid, auth.name)
		return ResponseEntity.ok(dto)
	}

	@PreAuthorize("isAuthenticated()")
	@GetMapping("/{id}/schema/tables-deleted")
	fun getDeletedTablesId(@PathVariable id: String, auth: Authentication): ResponseEntity<List<String>> {
		val uid = uidService.fromBase64Validated(id)
		val dto = service.getDeletedTablesId(uid, auth.name)
		return ResponseEntity.ok(dto)
	}

	@PreAuthorize("isAuthenticated()")
	@PostMapping("/{projectId}/schema/tables/{tableId}")
	fun addTableToProject(
		@PathVariable projectId: String,
		@PathVariable tableId: String,
		auth: Authentication,
	): ResponseEntity<Void> {
		val projectUid = uidService.fromBase64Validated(projectId)
		val tableUid = uidService.fromBase64Validated(tableId)
		service.addTableToProject(projectUid, tableUid, auth.name)
		return ResponseEntity.noContent().build()
	}

	@PreAuthorize("isAuthenticated()")
	@DeleteMapping("/{projectId}/schema/tables/{tableId}")
	fun removeTableFromProject(
		@PathVariable projectId: String,
		@PathVariable tableId: String,
		auth: Authentication,
	): ResponseEntity<Void> {
		val projectUid = uidService.fromBase64Validated(projectId)
		val tableUid = uidService.fromBase64Validated(tableId)
		service.removeTableFromProject(projectUid, tableUid, auth.name)
		return ResponseEntity.noContent().build()
	}

	@PreAuthorize("isAuthenticated()")
	@PatchMapping("/{projectId}/schema/tables/{tableId}")
	fun patchTable(
		@PathVariable projectId: String,
		@PathVariable tableId: String,
		@RequestBody @Valid form: SchemaTableFormPatch,
		auth: Authentication,
	): ResponseEntity<Void> {
		val projectUid = uidService.fromBase64Validated(projectId)
		val tableUid = uidService.fromBase64Validated(tableId)
		service.patchTable(projectUid, tableUid, form, auth.name)
		return ResponseEntity.noContent().build()
	}

	@PreAuthorize("isAuthenticated()")
	@PatchMapping("/{projectId}/schema/headers/{headerId}")
	fun patchHeader(
		@PathVariable projectId: String,
		@PathVariable headerId: String,
		@RequestBody @Valid form: SchemaHeaderFormPatch,
		auth: Authentication,
	): ResponseEntity<Void> {
		val projectUid = uidService.fromBase64Validated(projectId)
		val headerUid = uidService.fromBase64Validated(headerId)
		service.patchHeader(projectUid, headerUid, form, auth.name)
		return ResponseEntity.noContent().build()
	}
}