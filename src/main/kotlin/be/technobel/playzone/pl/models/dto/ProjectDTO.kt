package be.technobel.playzone.pl.models.dto

import be.technobel.playzone.dal.models.entities.Project
import be.technobel.playzone.dal.repositories.HistoryLogins

data class ProjectDTO (
	val id: String,
	val name: String,
	val description: String,
	val isPublic: Boolean,
	val log: HistoryLogDTO,
)

fun Project.toDTO(
	uidFromBin: (ByteArray) -> String,
	getLogIds: (Long,Long) -> HistoryLogins,
) = ProjectDTO(
	id = uidFromBin(this.uid),
	name = this.name,
	description = this.description,
	isPublic = this.isPublic,
	log = this.log.toDTO(getLogIds),
)