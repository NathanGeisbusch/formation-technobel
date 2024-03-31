package be.technobel.parsemaster.validation.constraint;

import be.technobel.parsemaster.validation.validator.PositiveOrZeroValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PositiveOrZeroValidator.class)
public @interface PositiveOrZero {
  String field() default "field";
  String message() default "<0";
  Class<?>[] groups() default {};
  Class<? extends Payload>[] payload() default {};
}
