package be.technobel.playzone.pl.security

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
class SecurityConfig {
	@Bean
	fun encoder(): PasswordEncoder = BCryptPasswordEncoder()

	@Bean
	fun authenticationManager(config: AuthenticationConfiguration)
	: AuthenticationManager = config.authenticationManager

	@Bean
	fun securityFilterChain(http: HttpSecurity): SecurityFilterChain = http
		.csrf { it.disable() }
		.cors { it.configurationSource(corsConfigurationSource()) }
		.sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
		.authorizeHttpRequests { it.anyRequest().permitAll() }
		.build()

	@Bean
	fun corsConfigurationSource(): CorsConfigurationSource? {
		val source = UrlBasedCorsConfigurationSource()
		val config = CorsConfiguration()
		config.allowedOrigins = listOf("http://localhost:4200", "*")
		config.allowedHeaders = listOf("*")
		config.exposedHeaders = listOf("Authorization", "Access-Control-Allow-Origin")
		config.setAllowedMethods(listOf("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"))
		source.registerCorsConfiguration("/**", config)
		return source
	}
}