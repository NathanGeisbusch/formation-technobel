package be.technobel.parsemaster.exception;

public class AlreadyExistsException extends RuntimeException {
  public AlreadyExistsException(String message) {
    super(message);
  }
}
