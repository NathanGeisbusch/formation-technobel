package be.technobel.playzone.pl.security

import be.technobel.playzone.dal.models.entities.User
import be.technobel.playzone.dal.models.enums.UserRole
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.JWTVerificationException
import jakarta.servlet.http.HttpServletRequest
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Component
import java.security.SecureRandom
import java.time.Instant
import java.util.*

@Component
class JWTProvider(private val userDetailsService: UserDetailsService) {
	private companion object {
		val JWT_SECRET = generateSecret(64)

		/** Durée de vie en secondes d'un token */
		const val EXPIRES_AT: Long = 60*60*24 // 60*15: 15 minutes

		private fun generateSecret(length: Int): ByteArray {
			val result = ByteArray(length)
			SecureRandom.getInstanceStrong().nextBytes(result)
			return result
		}
	}

	private data class RevokedToken(val token: String, val expiration: Instant)
	private val revokedTokens = TreeSet<RevokedToken> { a, b -> a.expiration.compareTo(b.expiration) }

	fun generateToken(username: String, role: UserRole): String {
		return "Bearer " + JWT.create()
			.withSubject(username)
			.withClaim("role", role.toString())
			.withExpiresAt(Instant.now().plusMillis(1000*EXPIRES_AT))
			.sign(Algorithm.HMAC512(JWT_SECRET))
	}

	fun extractToken(req: HttpServletRequest): String? {
		val header = req.getHeader("Authorization")
		if(header == null || !header.startsWith("Bearer ")) return null
		return header.replaceFirst("Bearer ", "")
	}

	fun validateToken(token: String): Boolean {
		try {
			val jwt = JWT.require(Algorithm.HMAC512(JWT_SECRET))
				.acceptExpiresAt(EXPIRES_AT)
				.withClaimPresence("sub")
				.withClaimPresence("role")
				.build()
				.verify(token)
			val username = jwt.subject
			val user = userDetailsService.loadUserByUsername(username) as User
			if(!user.isEnabled) return false
			val role = jwt.getClaim("role").`as`(UserRole::class.java)
			return user.role == role
		}
		catch(ex: JWTVerificationException) {
			println(ex.message)
			return false
		}
		catch(ex: UsernameNotFoundException) {
			println(ex.message)
			return false
		}
	}

	fun createAuthentication(token: String): Authentication {
		val jwt = JWT.decode(token)
		val username = jwt.subject
		val userDetails = userDetailsService.loadUserByUsername(username)
		return UsernamePasswordAuthenticationToken(userDetails.username, null, userDetails.authorities)
	}

	fun revoke(token: String) {
		cleanRevoked()
		if(!revokedTokens.any { it.token == token }) {
			revokedTokens.add(RevokedToken(token, JWT.decode(token).expiresAtAsInstant))
		}
	}

	fun isRevoked(token: String): Boolean {
		cleanRevoked()
		return revokedTokens.any { it.token == token }
	}

	private fun cleanRevoked() {
		val now = Instant.now()
		revokedTokens.removeIf { it.expiration < now }
	}
}