package be.technobel.playzone.dal.models.entities

import be.technobel.playzone.dal.models.enums.UserRole
import jakarta.persistence.*
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

@Entity
@Table(name = "account")
open class User : Base(), UserDetails {
	@Column(name="uid", nullable = false, unique = true)
	open lateinit var uid: ByteArray

	@Column(name = "email", nullable = false)
	open lateinit var email: String

	@Column(name = "password_hash", nullable = false)
	open lateinit var passwordHash: String

	@Column(name = "last_name", nullable = false)
	open lateinit var lastName: String

	@Column(name = "first_name", nullable = false)
	open lateinit var firstName: String

	@Column(name = "role", nullable = false)
	@Enumerated(value = EnumType.STRING)
	open lateinit var role: UserRole

	@Embedded
	open lateinit var log: HistoryLog

	@Column(name = "is_activated", nullable = false)
	open var isActivated: Boolean = false

	fun fullName(): String {
		val blankLastName = lastName.isBlank()
		val blankFirstName = lastName.isBlank()
		return when {
			blankLastName && blankFirstName -> ""
			blankLastName -> firstName
			blankFirstName -> lastName
			else -> "$lastName $firstName"
		}
	}

	override fun getAuthorities(): Collection<GrantedAuthority> = role.authorities

	override fun getPassword(): String = passwordHash

	override fun getUsername(): String = email

	override fun isAccountNonExpired(): Boolean = true

	override fun isAccountNonLocked(): Boolean = true

	override fun isCredentialsNonExpired(): Boolean = true

	override fun isEnabled(): Boolean = isActivated && log.deletedAt == null
}
