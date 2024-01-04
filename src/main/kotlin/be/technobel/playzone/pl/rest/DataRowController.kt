package be.technobel.playzone.pl.rest

import be.technobel.playzone.bll.services.DataRowService
import be.technobel.playzone.bll.services.impl.UidService
import be.technobel.playzone.pl.models.dto.DataRowDTO
import be.technobel.playzone.pl.models.dto.PagesDTO
import be.technobel.playzone.pl.models.forms.DataRowForm
import be.technobel.playzone.pl.models.forms.DataRowFormUpdates
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
@RequestMapping("/api/tables")
@Validated
class DataRowController(
	private val service: DataRowService,
	private val uidService: UidService,
) {
	@PreAuthorize("isAuthenticated()")
	@GetMapping("/{id}/data", params = ["page", "size"])
	fun find(
		@PathVariable id: String,
		@RequestParam @PositiveOrZero("page") page: Int,
		@RequestParam @Positive("size") size: Int,
		auth: Authentication,
	): ResponseEntity<PagesDTO<DataRowDTO>> {
		val uid = uidService.fromBase64Validated(id)
		val dtoPage = service.findAll(uid, page, size, auth.name)
		val dto = PagesDTO(dtoPage.totalPages, dtoPage.number, dtoPage.size, dtoPage.content)
		return ResponseEntity.ok(dto)
	}

	@PreAuthorize("isAuthenticated()")
	@GetMapping("/{tableId}/data/{dataId}")
	fun findOne(
		@PathVariable tableId: String,
		@PathVariable dataId: String,
		auth: Authentication,
	): ResponseEntity<DataRowDTO> {
		val tableUid = uidService.fromBase64Validated(tableId)
		val dataUid = uidService.fromBase64Validated(dataId)
		val dto = service.findOne(tableUid, dataUid, auth.name)
		return ResponseEntity.ok(dto)
	}

	@PreAuthorize("isAuthenticated()")
	@PostMapping("/{id}/data")
	fun create(
		@PathVariable id: String,
		@RequestBody @Valid form: List<DataRowForm>,
		auth: Authentication,
	): ResponseEntity<List<String>> {
		val uid = uidService.fromBase64Validated(id)
		val dto = service.create(uid, form, auth.name)
		return ResponseEntity.created(URI("/api/tables/${id}/data")).body(dto)
	}

	@PreAuthorize("isAuthenticated()")
	@PutMapping("/{id}/data")
	fun update(
		@PathVariable id: String,
		@RequestBody @Valid form: List<DataRowFormUpdates>,
		auth: Authentication,
	): ResponseEntity<Void> {
		val uid = uidService.fromBase64Validated(id)
		service.update(uid, form, auth.name)
		return ResponseEntity.noContent().build()
	}

	@PreAuthorize("isAuthenticated()")
	@PutMapping("/{tableId}/data/{dataId}")
	fun update(
		@PathVariable tableId: String,
		@PathVariable dataId: String,
		@RequestBody @Valid form: DataRowForm,
		auth: Authentication,
	): ResponseEntity<Void> {
		val tableUid = uidService.fromBase64Validated(tableId)
		val dataUid = uidService.fromBase64Validated(dataId)
		service.update(tableUid, dataUid, form, auth.name)
		return ResponseEntity.noContent().build()
	}

	@PreAuthorize("isAuthenticated()")
	@DeleteMapping("/{tableId}/data/{dataId}")
	fun delete(
		@PathVariable tableId: String,
		@PathVariable dataId: String,
		auth: Authentication,
	): ResponseEntity<Void> {
		val tableUid = uidService.fromBase64Validated(tableId)
		val dataUid = uidService.fromBase64Validated(dataId)
		service.delete(tableUid, dataUid, auth.name)
		return ResponseEntity.noContent().build()
	}

	@PreAuthorize("isAuthenticated()")
	@PostMapping("/{id}/data-bulk-delete")
	fun delete(
		@PathVariable id: String,
		@RequestBody @Valid form: List<String>,
		auth: Authentication,
	): ResponseEntity<Void> {
		val uid = uidService.fromBase64Validated(id)
		service.delete(uid, form, auth.name)
		return ResponseEntity.noContent().build()
	}

	@PreAuthorize("isAuthenticated()")
	@GetMapping("/{tableId}/headers/{headerId}/search", params = ["value"])
	fun searchValues(
		@PathVariable tableId: String,
		@PathVariable headerId: String,
		@RequestParam value: String,
	): ResponseEntity<List<String>> {
		val tableUid = uidService.fromBase64Validated(tableId)
		val headerUid = uidService.fromBase64Validated(headerId)
		val values = service.searchValues(tableUid, headerUid, value)
		return ResponseEntity.ok(values)
	}
}