package be.technobel.playzone.pl.rest

import be.technobel.playzone.bll.services.AuthService
import be.technobel.playzone.bll.services.UserService
import be.technobel.playzone.pl.models.dto.AuthDTO
import be.technobel.playzone.pl.models.dto.ProfileDTO
import be.technobel.playzone.pl.models.forms.ChangePasswordForm
import be.technobel.playzone.pl.models.forms.ChangePasswordRequestForm
import be.technobel.playzone.pl.models.forms.LoginForm
import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/auth")
class AuthController(
	private val service: AuthService,
	private val userService: UserService,
) {
	@PostMapping("/sign-in")
	fun signIn(@RequestBody @Valid form: LoginForm): ResponseEntity<AuthDTO> {
		val token = service.login(form)
		return ResponseEntity.ok(AuthDTO(token))
	}

	@PreAuthorize("isAuthenticated()")
	@PostMapping("/sign-out")
	fun signOut(request: HttpServletRequest): ResponseEntity<Void> {
		service.logout(request)
		return ResponseEntity.noContent().build()
	}

	@PostMapping("/change-password")
	fun passwordModificationRequest(
		@RequestBody @Valid form: ChangePasswordRequestForm,
	): ResponseEntity<Void> {
		service.passwordModificationRequest(form.email!!)
		return ResponseEntity.noContent().build()
	}

	@PostMapping("/change-password/{token}")
	fun passwordModification(
		@PathVariable token: String,
		@RequestBody @Valid form: ChangePasswordForm,
	): ResponseEntity<Void> {
		service.passwordModification(token, form.password!!)
		return ResponseEntity.noContent().build()
	}

	@PreAuthorize("isAuthenticated()")
	@GetMapping("/profile")
	fun profile(auth: Authentication): ResponseEntity<ProfileDTO> {
		val result = userService.profileByEmail(auth.name)
		return ResponseEntity.ok(result)
	}
}