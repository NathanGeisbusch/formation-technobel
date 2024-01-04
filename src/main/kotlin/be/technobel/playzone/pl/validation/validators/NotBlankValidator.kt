package be.technobel.playzone.pl.validation.validators

import be.technobel.playzone.pl.rest.FormError
import be.technobel.playzone.pl.rest.InvalidParamException
import be.technobel.playzone.pl.validation.constraints.NotBlank
import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext

class NotBlankValidator : ConstraintValidator<NotBlank, String> {
	private lateinit var field: String
	private lateinit var message: String

	override fun initialize(constraintAnnotation: NotBlank) {
		field = constraintAnnotation.field
		message = constraintAnnotation.message
	}

	override fun isValid(value: String, context: ConstraintValidatorContext): Boolean {
		return if (value.isNotBlank()) true else throw InvalidParamException(
			FormError(field, "$field $message")
		)
	}
}