package be.technobel.playzone.pl.security

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtAuthenticationFilter(private val jwtProvider: JWTProvider) : OncePerRequestFilter() {
	override fun doFilterInternal(
		request: HttpServletRequest,
		response: HttpServletResponse,
		filterChain: FilterChain
	) {
		val token = jwtProvider.extractToken(request)
		if(token != null && jwtProvider.validateToken(token) && !jwtProvider.isRevoked(token)) {
			val auth = jwtProvider.createAuthentication(token)
			SecurityContextHolder.getContext().authentication = auth
		}
		filterChain.doFilter(request, response)
	}
}