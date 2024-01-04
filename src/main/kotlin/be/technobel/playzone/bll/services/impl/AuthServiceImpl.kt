package be.technobel.playzone.bll.services.impl

import be.technobel.playzone.bll.exceptions.NotFoundException
import be.technobel.playzone.bll.services.AuthService
import be.technobel.playzone.bll.services.EmailService
import be.technobel.playzone.dal.repositories.UserRepository
import be.technobel.playzone.pl.models.forms.LoginForm
import be.technobel.playzone.pl.security.JWTProvider
import jakarta.servlet.http.HttpServletRequest
import org.springframework.core.env.Environment
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.AuthenticationException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.time.Instant
import java.util.*

/** Durée de vie en millisecondes d'un token */
private const val PASSWORD_TOKEN_EXPIRATION = 1000L*60*5

@Service
class AuthServiceImpl(
	private val userRepository: UserRepository,
	private val authenticationManager: AuthenticationManager,
	private val jwtProvider: JWTProvider,
	private val passwordEncoder: PasswordEncoder,
	private val uidService: UidService,
	private val emailService: EmailService,
	private val environment: Environment,
) : AuthService {
	private data class PasswordToken(val token: String, val email: String, val expiration: Instant)
	private val passwordTokens = TreeSet<PasswordToken> { a, b -> a.expiration.compareTo(b.expiration) }

	@Throws(AuthenticationException::class)
	override fun login(form: LoginForm): String {
		try {
			authenticationManager.authenticate(UsernamePasswordAuthenticationToken(form.login, form.password))
			val user = userRepository.findByEmail(form.login!!)!!
			return jwtProvider.generateToken(user.username, user.role)
		}
		catch (ex: AuthenticationException) {
			throw ex
		}
	}

	@Throws(BadCredentialsException::class)
	override fun logout(request: HttpServletRequest) {
		val token = jwtProvider.extractToken(request)
		?: throw BadCredentialsException("Invalid token")
		if(jwtProvider.validateToken(token)) jwtProvider.revoke(token)
		else throw BadCredentialsException("Invalid token")
	}

	override fun passwordModificationRequest(email: String) {
		cleanPasswordTokens()
		if(!passwordTokens.any { it.email == email }) {
			val token = uidService.generateBase64()
			passwordTokens.add(PasswordToken(
				token, email, Instant.now().plusMillis(PASSWORD_TOKEN_EXPIRATION)
			))
			if(environment.activeProfiles.contains("production")) {
				emailService.sendPasswordModificationEmail(
					email, "http://172.20.50.19:4200/auth/changePassword/${token}"
				)
			} else {
				emailService.sendPasswordModificationEmail(
					email, "http://localhost:4200/auth/changePassword/${token}"
				)
			}

		}
	}

	@Throws(NotFoundException::class)
	override fun passwordModification(token: String, password: String) {
		cleanPasswordTokens()
		val passwdToken = passwordTokens.find { it.token == token }
		?: throw NotFoundException("Token not found")
		val user = userRepository.findByEmail(passwdToken.email)
		?: throw NotFoundException("User not found")
		userRepository.modifyPasswordByUid(user.uid, passwordEncoder.encode(password))
		emailService.sendPasswordModificationConfirmationEmail(passwdToken.email)
		passwordTokens.remove(passwdToken)
	}

	/** Supprime les tokens expirés */
	private fun cleanPasswordTokens() {
		val now = Instant.now()
		passwordTokens.removeIf { it.expiration < now }
	}
}