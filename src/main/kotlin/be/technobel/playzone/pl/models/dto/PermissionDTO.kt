package be.technobel.playzone.pl.models.dto

import be.technobel.playzone.dal.models.entities.ProjectPermission
import be.technobel.playzone.dal.models.entities.TablePermission

class ProjectPermissionDTO
data class ProjectPermissionsDTO(val email: String)
fun ProjectPermission.toDTO() = ProjectPermissionDTO()
fun ProjectPermission.toEmailDTO() = ProjectPermissionsDTO(email = user.email)

class TablePermissionDTO
data class TablePermissionsDTO(val email: String)
fun TablePermission.toDTO() = TablePermissionDTO()
fun TablePermission.toEmailDTO() = TablePermissionsDTO(email = user.email)
