package be.technobel.parsemaster.validation.validator;

import be.technobel.parsemaster.dto.ErrorFieldDTO;
import be.technobel.parsemaster.exception.InvalidParamException;
import be.technobel.parsemaster.validation.constraint.Positive;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PositiveValidator implements ConstraintValidator<Positive, Integer> {
  private String field;
  private String message;

  @Override
  public void initialize(Positive annotation) {
    field = annotation.field();
    message = annotation.message();
  }

  @Override
  public boolean isValid(Integer value, ConstraintValidatorContext context) {
    if(value == null || value > 0) return true;
    throw new InvalidParamException(new ErrorFieldDTO(field, message));
  }
}
