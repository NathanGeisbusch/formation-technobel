package be.technobel.parsemaster.validation.validator;

import be.technobel.parsemaster.dto.ErrorFieldDTO;
import be.technobel.parsemaster.exception.InvalidParamException;
import be.technobel.parsemaster.validation.constraint.In;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class InValidator implements ConstraintValidator<In, Integer> {
  private String field;
  private String message;
  private long[] values;

  @Override
  public void initialize(In annotation) {
    field = annotation.field();
    message = annotation.message();
    values = annotation.values();
  }

  @Override
  public boolean isValid(Integer value, ConstraintValidatorContext context) {
    if(value == null) return true;
    for(final var v : values) if(value == v) return true;
    throw new InvalidParamException(new ErrorFieldDTO(field, message));
  }
}
