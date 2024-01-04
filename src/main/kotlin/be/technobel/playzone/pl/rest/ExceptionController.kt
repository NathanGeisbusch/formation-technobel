package be.technobel.playzone.pl.rest

import be.technobel.playzone.bll.exceptions.AlreadyExistsException
import be.technobel.playzone.bll.exceptions.NotFoundException
import com.fasterxml.jackson.databind.JsonMappingException
import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.ConstraintViolationException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.mail.MailException
import org.springframework.security.core.AuthenticationException
import org.springframework.security.access.AccessDeniedException
import org.springframework.validation.FieldError
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.UnsatisfiedServletRequestParameterException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.server.ResponseStatusException
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException

class InvalidParamException(val formError: FormError) : RuntimeException() {
	constructor(field: String, message: String) : this(FormError(field, message))
}

data class FormError(val field: String, val message: String)
data class FormErrorResponse (
	val status: Int,
	val path: String,
	val errors: List<FormError>,
)
data class ErrorResponse (
	val status: Int,
	val path: String,
	val error: String,
)

fun JsonMappingException.fieldName(): String? =
	if(path.isNotEmpty()) path.last().fieldName else null

@RestControllerAdvice
class ExceptionController {

	@ExceptionHandler(UnsatisfiedServletRequestParameterException::class)
	fun handleUnsatisfiedServletRequestParameterException(
		ex: UnsatisfiedServletRequestParameterException,
		request: HttpServletRequest
	): ResponseEntity<ErrorResponse>
	= ResponseEntity.notFound().build()

	@ExceptionHandler(MethodArgumentTypeMismatchException::class)
	fun handleMethodArgumentTypeMismatchException(ex: MethodArgumentTypeMismatchException, request: HttpServletRequest)
	: ResponseEntity<FormErrorResponse>
	= ResponseEntity.badRequest().body(FormErrorResponse(
		status = HttpStatus.BAD_REQUEST.value(),
		path = request.requestURI,
		errors = listOf(FormError(ex.parameter.parameter.name, "wrong_type")),
	))

	@ExceptionHandler(MethodArgumentNotValidException::class)
	fun handleValidationException(ex: MethodArgumentNotValidException, request: HttpServletRequest)
	: ResponseEntity<FormErrorResponse>
	= ResponseEntity.badRequest().body(FormErrorResponse(
		status = HttpStatus.BAD_REQUEST.value(),
		path = request.requestURI,
		errors = ex.bindingResult.allErrors.filterIsInstance<FieldError>().map {
			FormError(it.field, it.defaultMessage ?: "")
		},
	))

	@ExceptionHandler(ConstraintViolationException::class)
	fun handleConstraintValidationException(ex: ConstraintViolationException, request: HttpServletRequest)
	: ResponseEntity<FormErrorResponse>
	= ResponseEntity.badRequest().body(FormErrorResponse(
		status = HttpStatus.BAD_REQUEST.value(),
		path = request.requestURI,
		errors = ex.constraintViolations.map {
			FormError(it.propertyPath.last().name, it.message ?: "")
		},
	))

	@ExceptionHandler(InvalidParamException::class)
	fun handleArgumentInvalidException(ex: InvalidParamException, request: HttpServletRequest)
	: ResponseEntity<FormErrorResponse>
	= ResponseEntity.badRequest().body(FormErrorResponse(
		status = HttpStatus.BAD_REQUEST.value(),
		path = request.requestURI,
		errors = listOf(ex.formError),
	))

	@ExceptionHandler(HttpMessageNotReadableException::class)
	fun handleHttpMessageNotReadableException(ex: HttpMessageNotReadableException, request: HttpServletRequest)
	: ResponseEntity<FormErrorResponse> {
		val fieldName = (ex.cause as? JsonMappingException)?.fieldName()
		val errors = if (fieldName == null) emptyList() else listOf(FormError(fieldName, "bad_value"))
		val response = FormErrorResponse(HttpStatus.BAD_REQUEST.value(), request.requestURI, errors)
		return ResponseEntity.badRequest().body(response)
	}

	@ExceptionHandler(NotFoundException::class)
	fun handleNoSuchElementException(ex: NotFoundException, request: HttpServletRequest)
	: ResponseEntity<ErrorResponse>
	= ResponseEntity.notFound().build<ErrorResponse?>().also { println("${request.requestURI} ${ex.message}") }

	@ExceptionHandler(AlreadyExistsException::class)
	fun handleAlreadyExistsException(ex: AlreadyExistsException, request: HttpServletRequest)
	: ResponseEntity<ErrorResponse>
	= ResponseEntity.status(HttpStatus.CONFLICT).body(ErrorResponse(
		status = HttpStatus.CONFLICT.value(),
		path = request.requestURI,
		error = ex.message ?: "",
	))

	@ExceptionHandler(AccessDeniedException::class)
	fun handleUnauthorized (ex: RuntimeException, request: HttpServletRequest)
	: ResponseEntity<ErrorResponse>
	= ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ErrorResponse(
		status = HttpStatus.UNAUTHORIZED.value(),
		path = request.requestURI,
		error = "Unauthorized",
	))

	@ExceptionHandler(AuthenticationException::class)
	fun handleForbidden(ex: RuntimeException, request: HttpServletRequest): ResponseEntity<ErrorResponse>
	= ResponseEntity.status(HttpStatus.FORBIDDEN).body(ErrorResponse(
		status = HttpStatus.FORBIDDEN.value(),
		path = request.requestURI,
		error = "Forbidden",
	))

	@ExceptionHandler(MailException::class)
	fun handleMailException(ex: MailException, request: HttpServletRequest)
	: ResponseEntity<ErrorResponse>
	= ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ErrorResponse(
		status = HttpStatus.INTERNAL_SERVER_ERROR.value(),
		path = request.requestURI,
		error = "The confirmation email could not be sent",
	))

	@ExceptionHandler(ResponseStatusException::class)
	fun handleResponseStatusException(ex: ResponseStatusException, request: HttpServletRequest)
	: ResponseEntity<ErrorResponse>
	= ResponseEntity.status(ex.statusCode).body(ErrorResponse(
		status = ex.statusCode.value(),
		path = request.requestURI,
		error = ex.message ?: "",
	))
}
