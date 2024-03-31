package be.technobel.parsemaster.validation.validator;

import be.technobel.parsemaster.dto.ErrorFieldDTO;
import be.technobel.parsemaster.exception.InvalidParamException;
import be.technobel.parsemaster.validation.constraint.NotBlank;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class NotBlankValidator implements ConstraintValidator<NotBlank, String> {
  private String field;
  private String message;

  @Override
  public void initialize(NotBlank annotation) {
    field = annotation.field();
    message = annotation.message();
  }

  @Override
  public boolean isValid(String value, ConstraintValidatorContext context) {
    if(value == null || !value.isBlank()) return true;
    throw new InvalidParamException(new ErrorFieldDTO(field, message));
  }
}
