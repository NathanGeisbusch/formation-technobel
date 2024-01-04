package be.technobel.playzone

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication


@SpringBootApplication
class PlayZoneBackendApplication

fun main(args: Array<String>) {
	val application = SpringApplication(PlayZoneBackendApplication::class.java)
	application.setAdditionalProfiles("dev")
	application.run(*args)
}
