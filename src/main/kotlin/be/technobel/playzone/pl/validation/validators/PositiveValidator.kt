package be.technobel.playzone.pl.validation.validators

import be.technobel.playzone.pl.rest.FormError
import be.technobel.playzone.pl.rest.InvalidParamException
import be.technobel.playzone.pl.validation.constraints.Positive
import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext

class PositiveValidator : ConstraintValidator<Positive, Int> {
	private lateinit var field: String
	private lateinit var message: String

	override fun initialize(constraintAnnotation: Positive) {
		field = constraintAnnotation.field
		message = constraintAnnotation.message
	}

	override fun isValid(value: Int, context: ConstraintValidatorContext): Boolean {
		return if (value > 0) true else throw InvalidParamException(
			FormError(field, "$field $message")
		)
	}
}
