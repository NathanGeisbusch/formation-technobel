package be.technobel.playzone.dal.utils

import be.technobel.playzone.bll.services.impl.UidService
import be.technobel.playzone.dal.models.entities.HistoryLog
import be.technobel.playzone.dal.models.entities.User
import be.technobel.playzone.dal.models.enums.UserRole
import be.technobel.playzone.dal.repositories.UserRepository
import org.springframework.beans.factory.InitializingBean
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.env.Environment
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
data class DllAutoProperty(
	@Value("\${spring.jpa.hibernate.ddl-auto}")
	val ddlAuto: String
)

@Component
class DataInit(
	private val environment: Environment,
	private val jdbcTemplate: JdbcTemplate,
	private val ddlAutoProperty: DllAutoProperty,
	private val userRepository: UserRepository,
	private val uidService: UidService,
	private val passwordEncoder: PasswordEncoder,
) : InitializingBean {
	override fun afterPropertiesSet() {
		if(ddlAutoProperty.ddlAuto != "create") return

		// Création d'index
		if(environment.activeProfiles.contains("production")) {
			jdbcTemplate.execute("CREATE INDEX IF NOT EXISTS idx_user_uid ON account USING btree (uid)")
			jdbcTemplate.execute("CREATE INDEX IF NOT EXISTS idx_user_email ON account USING btree (UPPER(email) varchar_pattern_ops)")
		}

		// Création d'un admin
		val admin = User().apply {
			uid = uidService.generate()
			email = "admin.playzone@hotmail.com"
			passwordHash = passwordEncoder.encode("admin")
			lastName = "PlayZone"
			firstName = "Admin"
			role = UserRole.ADMIN
			isActivated = true
			log = HistoryLog().apply {
				createdAt = LocalDateTime.now()
				updatedAt = createdAt
			}
		}
		userRepository.save(admin)

		// L'admin est créé par lui-même
		admin.log.apply {
			createdBy = admin.id
			updatedBy = admin.id
		}
		userRepository.save(admin)
	}
}