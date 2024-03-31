package be.technobel.parsemaster.validation.constraint;

import be.technobel.parsemaster.validation.validator.InValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = InValidator.class)
public @interface In {
  String field() default "field";
  String message() default "bad_value";
  long[] values();
  Class<?>[] groups() default {};
  Class<? extends Payload>[] payload() default {};
}
