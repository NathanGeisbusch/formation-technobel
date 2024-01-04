package be.technobel.playzone.pl.validation.constraints

import be.technobel.playzone.pl.validation.validators.NotBlankValidator
import jakarta.validation.Constraint
import jakarta.validation.Payload
import kotlin.reflect.KClass

@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
@Constraint(validatedBy = [NotBlankValidator::class])
annotation class NotBlank(
	val field: String = "field",
	val message: String = "not_blank",
	val groups: Array<KClass<*>> = [],
	val payload: Array<KClass<out Payload>> = []
)