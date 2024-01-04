package be.technobel.playzone.pl.rest

import be.technobel.playzone.bll.services.UserService
import be.technobel.playzone.bll.services.impl.UidService
import be.technobel.playzone.pl.models.dto.CreatedDTO
import be.technobel.playzone.pl.models.dto.PagesDTO
import be.technobel.playzone.pl.models.dto.UserDTO
import be.technobel.playzone.pl.models.forms.UserFormCreate
import be.technobel.playzone.pl.models.forms.UserFormPatch
import be.technobel.playzone.pl.validation.constraints.NotBlank
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
@RequestMapping("/api/users")
@Validated
class UserController(val service: UserService, val uidService: UidService) {
	@PreAuthorize("hasRole('ADMIN')")
	@GetMapping(params = ["page", "size"])
	fun find(
		@RequestParam @PositiveOrZero("page") page: Int,
		@RequestParam @Positive("size") size: Int,
	): ResponseEntity<PagesDTO<UserDTO>> {
		val dtoPage = service.findAll(page, size)
		val dto = PagesDTO(dtoPage.totalPages, dtoPage.number, dtoPage.size, dtoPage.content)
		return ResponseEntity.ok(dto)
	}

	@PreAuthorize("hasRole('ADMIN')")
	@GetMapping("/{id}")
	fun findOneById(@PathVariable id: String): ResponseEntity<UserDTO> {
		val uid = uidService.fromBase64Validated(id)
		val dto = service.findOne(uid)
		return ResponseEntity.ok(dto)
	}

	@PreAuthorize("hasRole('ADMIN')")
	@PostMapping
	fun create(
		@RequestBody @Valid form: UserFormCreate,
		auth: Authentication,
	): ResponseEntity<CreatedDTO> {
		val id = service.create(form, auth.name)
		return ResponseEntity.created(URI("/api/users/${id}")).body(CreatedDTO(id))
	}

	@PreAuthorize("hasRole('ADMIN')")
	@PatchMapping("/{id}")
	fun patch(
		@PathVariable id: String,
		@RequestBody @Valid form: UserFormPatch,
		auth: Authentication,
	): ResponseEntity<Void> {
		val uid = uidService.fromBase64Validated(id)
		service.patch(uid, form, auth.name)
		return ResponseEntity.noContent().build()
	}

	@PreAuthorize("hasRole('ADMIN')")
	@DeleteMapping("/{id}")
	fun delete(@PathVariable id: String, auth: Authentication): ResponseEntity<Void> {
		val uid = uidService.fromBase64Validated(id)
		service.delete(uid, auth.name)
		return ResponseEntity.noContent().build()
	}
}

@RestController
@RequestMapping("/api/users-emails")
@Validated
class UserEmailController(val service: UserService) {
	/** Récupère maximum 10 adresses email commençant par la valeur du paramètre 'email' */
	@PreAuthorize("isAuthenticated()")
	@GetMapping(params = ["email"])
	fun findEmails(@RequestParam @NotBlank("email") email: String): ResponseEntity<List<String>> {
		val emails = service.findEmails(email)
		return ResponseEntity.ok(emails)
	}
}
