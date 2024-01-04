package be.technobel.playzone.pl.models.dto

import be.technobel.playzone.dal.models.entities.User
import be.technobel.playzone.dal.models.enums.UserRole
import be.technobel.playzone.dal.repositories.HistoryLogins

data class UserDTO (
	val id: String,
	val email: String,
	val firstName: String,
	val lastName: String,
	val role: UserRole,
	val isActivated: Boolean,
	val log: HistoryLogDTO,
)

fun User.toDTO(
	uidFromBin: (ByteArray) -> String,
	getLogIds: (Long,Long) -> HistoryLogins,
) = UserDTO(
	id = uidFromBin(this.uid),
	email = this.email,
	firstName = this.firstName,
	lastName = this.lastName,
	role = this.role,
	isActivated = this.isActivated,
	log = this.log.toDTO(getLogIds),
)
