package be.technobel.parsemaster.validation.constraint;

import be.technobel.parsemaster.validation.validator.BetweenValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = BetweenValidator.class)
public @interface Between {
    String field() default "field";
    String message() default "bad_range";
    long min();
    long max();
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
