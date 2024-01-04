package be.technobel.playzone.pl.rest

import org.springframework.boot.web.servlet.error.ErrorController
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class ErrorMvcController : ErrorController {
	@RequestMapping("/error")
	fun error(): ResponseEntity<Void> = ResponseEntity(HttpStatus.NOT_FOUND)
}
