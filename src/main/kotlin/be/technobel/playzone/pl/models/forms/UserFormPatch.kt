package be.technobel.playzone.pl.models.forms

import be.technobel.playzone.dal.models.enums.UserRole
import be.technobel.playzone.pl.validation.utils.EMAIL
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size

data class UserFormPatch(
	@field:Size(max = 250, message = "size_250")
	@field:Pattern(regexp = EMAIL, message = "email")
	val email: String?,

	@field:Size(max = 250, message = "size_250")
	val firstName: String?,

	@field:Size(max = 250, message = "size_250")
	val lastName: String?,

	@field:Size(max = 250, message = "size_250")
	val password: String?,

	val role: UserRole?,

	val isActivated: Boolean?,
)