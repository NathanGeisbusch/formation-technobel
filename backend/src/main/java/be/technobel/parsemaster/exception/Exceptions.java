package be.technobel.parsemaster.exception;

import org.springframework.mail.MailPreparationException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public enum Exceptions {
  UNAUTHORIZED("unauthorized", UnauthorizedException.class),
  FORBIDDEN("forbidden", ForbiddenException.class),
  NOT_FOUND("not_found", NotFoundException.class),
  CONSTRAINT("constraint_failed", ConstraintException.class),
  ALREADY_EXISTS("already_exists", AlreadyExistsException.class),
  BAD_VALUE("bad_value", InvalidParamException.class),
  WRONG_TYPE("wrong_type", InvalidParamException.class),
  BAD_ENUM("bad_enum", InvalidParamException.class),
  SERVER_ERROR("server_error", RuntimeException.class),
  TOO_BIG_PAYLOAD("too_big_payload", InvalidParamException.class),
  INVALID_PAYLOAD("invalid_payload", InvalidParamException.class),
  MAIL_EXCEPTION("email_could_not_be_sent", MailPreparationException.class),
  TOKEN_NOT_FOUND("token_not_found", NotFoundException.class),
  BAD_TOKEN("invalid_token", BadCredentialsException.class),
  BAD_CREDENTIALS("bad_credentials", UnauthorizedException.class),
  USERNAME_NOT_FOUND("username_not_found", UsernameNotFoundException.class),
  USER_NOT_FOUND("user_not_found", NotFoundException.class),
  USER_ALREADY_EXISTS("user_already_exists", ConstraintException.class),
  PARSER_NOT_FOUND("parser_not_found", NotFoundException.class),
  PARSER_ALREADY_EXISTS("parser_already_exists", ConstraintException.class),
  GENERATOR_NOT_FOUND("generator_not_found", NotFoundException.class),
  GENERATOR_ALREADY_EXISTS("generator_already_exists", ConstraintException.class),
  SESSION_NOT_FOUND("session_not_found", NotFoundException.class),
  SESSION_ALREADY_EXISTS("session_already_exists", ConstraintException.class),
  NO_REMAINING_VERSION("no_remaining_version", ConstraintException.class);

  private final String msg;
  private final Class<? extends RuntimeException> clazz;

  Exceptions(String msg, Class<? extends RuntimeException> clazz) {
    this.msg = msg;
    this.clazz = clazz;
  }

  public RuntimeException create() {
    try {
      return clazz.getDeclaredConstructor(String.class).newInstance(msg);
    } catch(Exception e) {
      throw new RuntimeException(e);
    }
  }

  public String getMessage() {
    return this.msg;
  }
}
