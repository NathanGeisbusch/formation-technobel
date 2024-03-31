package be.technobel.parsemaster.validation.validator;

import be.technobel.parsemaster.dto.ErrorFieldDTO;
import be.technobel.parsemaster.exception.InvalidParamException;
import be.technobel.parsemaster.validation.constraint.Between;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class BetweenValidator implements ConstraintValidator<Between, Integer> {
  private String field;
  private String message;
  private long min;
  private long max;

  @Override
  public void initialize(Between annotation) {
    field = annotation.field();
    message = annotation.message();
    min = annotation.min();
    max = annotation.max();
  }

  @Override
  public boolean isValid(Integer value, ConstraintValidatorContext context) {
    if(value == null || (value >= min && value <= max)) return true;
    throw new InvalidParamException(new ErrorFieldDTO(field, message));
  }
}
