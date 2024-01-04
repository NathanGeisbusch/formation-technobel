package be.technobel.playzone.pl.models.forms

import be.technobel.playzone.dal.models.enums.UserRole
import be.technobel.playzone.pl.validation.utils.EMAIL
import jakarta.validation.constraints.*

data class UserFormCreate(
	@field:NotNull(message = "required")
	@field:Size(max = 250, message = "size_250")
	@field:Pattern(regexp = EMAIL, message = "email")
	val email: String?,

	@field:NotNull(message = "required")
	@field:NotBlank(message = "not_blank")
	@field:Size(max = 250, message = "size_250")
	val firstName: String?,

	@field:NotNull(message = "required")
	@field:NotBlank(message = "not_blank")
	@field:Size(max = 250, message = "size_250")
	val lastName: String?,

	@field:NotNull(message = "required")
	@field:NotBlank(message = "not_blank")
	@field:Size(max = 250, message = "size_250")
	val password: String?,

	@field:NotNull(message = "required")
	val role: UserRole?,

	val isActivated: Boolean?,
)