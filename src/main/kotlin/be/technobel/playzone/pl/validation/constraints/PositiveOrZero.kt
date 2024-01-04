package be.technobel.playzone.pl.validation.constraints

import be.technobel.playzone.pl.validation.validators.PositiveOrZeroValidator
import jakarta.validation.Constraint
import jakarta.validation.Payload
import kotlin.reflect.KClass

@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
@Constraint(validatedBy = [PositiveOrZeroValidator::class])
annotation class PositiveOrZero(
	val field: String = "field",
	val message: String = "positive_or_zero",
	val groups: Array<KClass<*>> = [],
	val payload: Array<KClass<out Payload>> = []
)