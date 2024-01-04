package be.technobel.playzone.bll.services

import be.technobel.playzone.bll.exceptions.NotFoundException
import be.technobel.playzone.pl.models.forms.LoginForm
import jakarta.servlet.http.HttpServletRequest
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.core.AuthenticationException

interface AuthService {
	@Throws(AuthenticationException::class)
	fun login(form: LoginForm): String

	@Throws(BadCredentialsException::class)
	fun logout(request: HttpServletRequest)

	/** Envoie un mail de récupération de mot de passe */
	fun passwordModificationRequest(email: String)

	/** Modifie le mot de passe de l'utilisateur pour le token en paramètre */
	@Throws(NotFoundException::class)
	fun passwordModification(token: String, password: String)
}