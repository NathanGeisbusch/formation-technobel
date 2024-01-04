package be.technobel.playzone.pl.validation.constraints

import be.technobel.playzone.pl.validation.validators.PositiveValidator
import jakarta.validation.Constraint
import jakarta.validation.Payload
import kotlin.reflect.KClass

@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
@Constraint(validatedBy = [PositiveValidator::class])
annotation class Positive(
	val field: String = "field",
	val message: String = "positive",
	val groups: Array<KClass<*>> = [],
	val payload: Array<KClass<out Payload>> = []
)