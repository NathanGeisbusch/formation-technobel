package be.technobel.parsemaster.service.declaration;

public interface EmailService {
  /**
   * Sends an email confirming password modification of the user.
   * @param toEmail email address of the user
   */
  void passwordModificationConfirmation(String toEmail);

  /**
   *  Sends an email to the user with the url to change his password.
   * @param toEmail email address of the user
   * @param passwordChangeUrl front-end url to change user password.
   */
  void passwordModificationRequest(String toEmail, String passwordChangeUrl);

  /**
   * Sends an email confirming account creation of the user.
   * @param toEmail email address of the created user
   */
  void userCreation(String toEmail);
}
