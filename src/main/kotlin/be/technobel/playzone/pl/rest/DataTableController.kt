package be.technobel.playzone.pl.rest

import be.technobel.playzone.bll.services.DataTableService
import be.technobel.playzone.bll.services.impl.UidService
import be.technobel.playzone.pl.models.dto.CreatedDTO
import be.technobel.playzone.pl.models.dto.DataTableDTO
import be.technobel.playzone.pl.models.dto.PagesDTO
import be.technobel.playzone.pl.models.forms.DataTableFormCreate
import be.technobel.playzone.pl.models.forms.DataTableFormUpdate
import be.technobel.playzone.pl.validation.constraints.Positive
import be.technobel.playzone.pl.validation.constraints.PositiveOrZero
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.Authentication
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import java.net.URI

enum class TableVisibility {OWNER, SHARED}

@RestController
@RequestMapping("/api/tables")
@Validated
class DataTableController(
	private val service: DataTableService,
	private val uidService: UidService,
) {
	@PreAuthorize("isAuthenticated()")
	@GetMapping(params = ["page", "size"])
	fun find(
		@RequestParam @PositiveOrZero("page") page: Int,
		@RequestParam @Positive("size") size: Int,
		auth: Authentication,
	): ResponseEntity<PagesDTO<DataTableDTO>> {
		val dtoPage = service.findAll(page, size, auth.name)
		val dto = PagesDTO(dtoPage.totalPages, dtoPage.number, dtoPage.size, dtoPage.content)
		return ResponseEntity.ok(dto)
	}

	@PreAuthorize("isAuthenticated()")
	@GetMapping(params = ["page", "size", "from"])
	fun find(
		@RequestParam @PositiveOrZero("page") page: Int,
		@RequestParam @Positive("size") size: Int,
		@RequestParam from: TableVisibility,
		auth: Authentication,
	): ResponseEntity<PagesDTO<DataTableDTO>> {
		val dtoPage = service.findAll(page, size, auth.name, from)
		val dto = PagesDTO(dtoPage.totalPages, dtoPage.number, dtoPage.size, dtoPage.content)
		return ResponseEntity.ok(dto)
	}

	@PreAuthorize("isAuthenticated()")
	@GetMapping("/{id}")
	fun findOneById(@PathVariable id: String, auth: Authentication): ResponseEntity<DataTableDTO> {
		val uid = uidService.fromBase64Validated(id)
		val dto = service.findOne(uid, auth.name)
		return ResponseEntity.ok(dto)
	}

	@PreAuthorize("isAuthenticated()")
	@PostMapping
	fun create(
		@RequestBody @Valid form: DataTableFormCreate,
		auth: Authentication,
	): ResponseEntity<CreatedDTO> {
		val id = service.create(form, auth.name)
		return ResponseEntity.created(URI("/api/tables/${id}")).body(CreatedDTO(id))
	}

	@PreAuthorize("isAuthenticated()")
	@PutMapping("/{id}")
	fun put(
		@PathVariable id: String,
		@RequestBody @Valid form: DataTableFormUpdate,
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