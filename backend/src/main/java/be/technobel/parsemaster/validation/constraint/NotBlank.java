package be.technobel.parsemaster.validation.constraint;

import be.technobel.parsemaster.validation.validator.NotBlankValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = NotBlankValidator.class)
public @interface NotBlank {
  String field() default "field";
  String message() default "blank";
  Class<?>[] groups() default {};
  Class<? extends Payload>[] payload() default {};
}
