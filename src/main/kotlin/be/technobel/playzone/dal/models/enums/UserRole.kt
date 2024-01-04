package be.technobel.playzone.dal.models.enums

import org.springframework.security.core.authority.SimpleGrantedAuthority

enum class UserRole {
	ADMIN, USER;
	val authorities = setOf(SimpleGrantedAuthority("ROLE_$this"))
}
