package be.technobel.playzone.pl.models.dto

import be.technobel.playzone.dal.models.entities.User

data class ProfileDTO (
	val id: String,
	val email: String,
	val firstName: String,
	val lastName: String,
)

fun User.toProfileDTO(uidFromBin: (ByteArray) -> String) = ProfileDTO(
	id = uidFromBin(this.uid),
	email = this.email,
	firstName = this.firstName,
	lastName = this.lastName,
)
