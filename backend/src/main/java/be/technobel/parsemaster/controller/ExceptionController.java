package be.technobel.parsemaster.controller;

import be.technobel.parsemaster.dto.*;
import be.technobel.parsemaster.exception.*;
import com.fasterxml.jackson.databind.JsonMappingException;
import jakarta.mail.MessagingException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.mail.MailException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.UnsatisfiedServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;

@RestControllerAdvice
public class ExceptionController {
  private static String fieldName(JsonMappingException exception) {
    final var path = exception.getPath();
    if(path != null && !path.isEmpty()) return path.get(path.size()-1).getFieldName();
    return "";
  }

  @ExceptionHandler(UnsatisfiedServletRequestParameterException.class)
  public ResponseEntity<ErrorNotFoundDTO> handleUnsatisfiedServletRequestParameterException(
    HttpServletRequest request
  ) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorNotFoundDTO(
      HttpStatus.NOT_FOUND.value(),
      request.getRequestURI(),
      Exceptions.NOT_FOUND.getMessage()
    ));
  }

  @ExceptionHandler(NotFoundException.class)
  public ResponseEntity<ErrorNotFoundDTO> handleNotFoundException(
    NotFoundException ex,
    HttpServletRequest request
  ) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorNotFoundDTO(
      HttpStatus.NOT_FOUND.value(),
      request.getRequestURI(),
      ex.getMessage() != null ? ex.getMessage() : Exceptions.NOT_FOUND.getMessage()
    ));
  }

  @ExceptionHandler(ServletException.class)
  public ResponseEntity<ErrorNotFoundDTO> handleServletException(
    HttpServletRequest request
  ) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorNotFoundDTO(
      HttpStatus.NOT_FOUND.value(),
      request.getRequestURI(),
      Exceptions.NOT_FOUND.getMessage()
    ));
  }

  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  public ResponseEntity<ErrorFormDTO> handleMethodArgumentTypeMismatchException(
    MethodArgumentTypeMismatchException ex,
    HttpServletRequest request
  ) {
    return ResponseEntity.badRequest().body(new ErrorFormDTO(
      HttpStatus.BAD_REQUEST.value(),
      request.getRequestURI(),
      List.of(new ErrorFieldDTO(
        ex.getParameter().getParameter().getName(),
        Exceptions.WRONG_TYPE.getMessage()
      ))
    ));
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorFormDTO> handleValidationException(
    MethodArgumentNotValidException ex,
    HttpServletRequest request
  ) {
    return ResponseEntity.badRequest().body(new ErrorFormDTO(
      HttpStatus.BAD_REQUEST.value(),
      request.getRequestURI(),
      ex.getBindingResult().getAllErrors().stream()
        .filter(error -> error instanceof FieldError)
        .map(error -> new ErrorFieldDTO(
          ((FieldError)error).getField(),
          error.getDefaultMessage() != null ? error.getDefaultMessage() : ""
        )).toList()
    ));
  }

  @ExceptionHandler(InvalidParamException.class)
  public ResponseEntity<ErrorFormDTO> handleInvalidParamException(
    InvalidParamException ex,
    HttpServletRequest request
  ) {
    return ResponseEntity.badRequest().body(new ErrorFormDTO(
      HttpStatus.BAD_REQUEST.value(),
      request.getRequestURI(),
      List.of(ex.formError)
    ));
  }

  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<ErrorFormDTO> handleConstraintValidationException(
    ConstraintViolationException ex,
    HttpServletRequest request
  ) {
    return ResponseEntity.badRequest().body(new ErrorFormDTO(
      HttpStatus.BAD_REQUEST.value(),
      request.getRequestURI(),
      ex.getConstraintViolations().stream().map(
        error -> new ErrorFieldDTO(
          error.getPropertyPath().iterator().next().getName(),
          error.getMessage() != null ? error.getMessage() : Exceptions.CONSTRAINT.getMessage()
        )
      ).toList()
    ));
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<ErrorFormDTO> handleHttpMessageNotReadableException(
    HttpMessageNotReadableException ex,
    HttpServletRequest request
  ) {
    final var fieldName = ex.getCause() != null && ex.getCause() instanceof JsonMappingException ?
      fieldName((JsonMappingException)ex.getCause()) : "";
    final var response = new ErrorFormDTO(
      HttpStatus.BAD_REQUEST.value(), request.getRequestURI(),
      List.of(new ErrorFieldDTO(fieldName, Exceptions.BAD_VALUE.getMessage()))
    );
    return ResponseEntity.badRequest().body(response);
  }

  @ExceptionHandler(AlreadyExistsException.class)
  public ResponseEntity<ErrorConflictDTO> handleAlreadyExistsException(
    AlreadyExistsException ex,
    HttpServletRequest request
  ) {
    return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorConflictDTO(
      HttpStatus.CONFLICT.value(),
      request.getRequestURI(),
      ex.getMessage() != null ? ex.getMessage() : Exceptions.ALREADY_EXISTS.getMessage()
    ));
  }

  @ExceptionHandler(ConstraintException.class)
  public ResponseEntity<ErrorConflictDTO> handleConstraintException(
    ConstraintException ex,
    HttpServletRequest request
  ) {
    return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorConflictDTO(
      HttpStatus.CONFLICT.value(),
      request.getRequestURI(),
      ex.getMessage() != null ? ex.getMessage() : Exceptions.CONSTRAINT.getMessage()
    ));
  }

  @ExceptionHandler(AccessDeniedException.class)
  public ResponseEntity<ErrorUnauthorizedDTO> handleAccessDeniedException(
    HttpServletRequest request
  ) {
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorUnauthorizedDTO(
      HttpStatus.UNAUTHORIZED.value(),
      request.getRequestURI(),
      Exceptions.UNAUTHORIZED.getMessage()
    ));
  }

  @ExceptionHandler(UnauthorizedException.class)
  public ResponseEntity<ErrorUnauthorizedDTO> handleUnauthorizedException(
    UnauthorizedException ex,
    HttpServletRequest request
  ) {
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorUnauthorizedDTO(
      HttpStatus.UNAUTHORIZED.value(),
      request.getRequestURI(),
      ex.getMessage()
    ));
  }

  @ExceptionHandler(AuthenticationException.class)
  public ResponseEntity<ErrorUnauthorizedDTO> handleAuthenticationException(
    HttpServletRequest request
  ) {
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorUnauthorizedDTO(
      HttpStatus.UNAUTHORIZED.value(),
      request.getRequestURI(),
      Exceptions.BAD_TOKEN.getMessage()
    ));
  }

  @ExceptionHandler(ForbiddenException.class)
  public ResponseEntity<ErrorForbiddenDTO> handleForbiddenException(
    HttpServletRequest request
  ) {
    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorForbiddenDTO(
      HttpStatus.FORBIDDEN.value(),
      request.getRequestURI(),
      Exceptions.FORBIDDEN.getMessage()
    ));
  }

  @ExceptionHandler(ResponseStatusException.class)
  public ResponseEntity<ErrorServerDTO> handleResponseStatusException(
    ResponseStatusException ex,
    HttpServletRequest request
  ) {
    return ResponseEntity.status(ex.getStatusCode()).body(new ErrorServerDTO(
      ex.getStatusCode().value(),
      request.getRequestURI(),
      Exceptions.SERVER_ERROR.getMessage()
    ));
  }

  @ExceptionHandler(MailException.class)
  public ResponseEntity<ErrorServerDTO> handleMailException(
    HttpServletRequest request
  ) {
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorServerDTO(
      HttpStatus.INTERNAL_SERVER_ERROR.value(),
      request.getRequestURI(),
      Exceptions.MAIL_EXCEPTION.getMessage()
    ));
  }

  @ExceptionHandler(MessagingException.class)
  public ResponseEntity<ErrorServerDTO> handleMessagingException(
    HttpServletRequest request
  ) {
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorServerDTO(
      HttpStatus.INTERNAL_SERVER_ERROR.value(),
      request.getRequestURI(),
      Exceptions.MAIL_EXCEPTION.getMessage()
    ));
  }
}
