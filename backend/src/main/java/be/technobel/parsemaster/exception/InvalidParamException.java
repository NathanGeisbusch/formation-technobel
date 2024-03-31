package be.technobel.parsemaster.exception;

import be.technobel.parsemaster.dto.ErrorFieldDTO;

public class InvalidParamException extends RuntimeException {
  public final ErrorFieldDTO formError;

  public InvalidParamException(ErrorFieldDTO formError) {
    this.formError = formError;
  }

  public InvalidParamException(String field, String reason) {
    this.formError = new ErrorFieldDTO(field, reason);
  }

  public InvalidParamException(String reason) {
    this.formError = new ErrorFieldDTO("", reason);
  }
}
