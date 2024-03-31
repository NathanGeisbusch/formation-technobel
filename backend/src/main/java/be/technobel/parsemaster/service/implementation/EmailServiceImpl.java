package be.technobel.parsemaster.service.implementation;

import be.technobel.parsemaster.exception.Exceptions;
import be.technobel.parsemaster.repository.UserRepository;
import be.technobel.parsemaster.service.declaration.EmailService;
import freemarker.template.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import java.util.HashMap;

@Service
public class EmailServiceImpl implements EmailService {
  private final JavaMailSender mailSender;
  private final UserRepository userRepository;
  private final Configuration freemarkerConfig;
  private final String supportEmail;

  public EmailServiceImpl(
    JavaMailSender mailSender,
    UserRepository userRepository,
    Configuration freemarkerConfig
  ) {
    this.mailSender = mailSender;
    this.userRepository = userRepository;
    this.freemarkerConfig = freemarkerConfig;
    this.supportEmail = ((JavaMailSenderImpl)mailSender).getUsername();
  }

  private void sendMail(String to, String subject, String templatePath, Object model) {
    try {
      final var msg = mailSender.createMimeMessage();
      final var template = freemarkerConfig.getTemplate(templatePath);
      final var content = FreeMarkerTemplateUtils.processTemplateIntoString(template, model);
      final var helper = new MimeMessageHelper(msg, true);
      helper.setTo(to);
      helper.setFrom(supportEmail);
      helper.setSubject(subject);
      helper.setText(content, true);
      mailSender.send(msg);
    }
    catch(Exception ex) {
      throw Exceptions.MAIL_EXCEPTION.create();
    }
  }

  @Override
  public void passwordModificationConfirmation(String toEmail) {
    final var user = userRepository
      .findByEmail(toEmail)
      .orElseThrow(Exceptions.USER_NOT_FOUND::create);
    final var model = new HashMap<String, String>();
    model.put("pseudonym", user.getPseudonym());
    model.put("support", supportEmail);
    final var subject = "ParseMaster - Confirmation de changement de mot de passe";
    sendMail(toEmail, subject, "email/change-password.ftl", model);
  }

  @Override
  public void passwordModificationRequest(String toEmail, String passwordChangeUrl) {
    final var user = userRepository
      .findByEmail(toEmail)
      .orElseThrow(Exceptions.USER_NOT_FOUND::create);
    final var model = new HashMap<String, String>();
    model.put("pseudonym", user.getPseudonym());
    model.put("support", supportEmail);
    model.put("url", passwordChangeUrl);
    final var subject = "ParseMaster - Changement de mot de passe";
    sendMail(toEmail, subject, "email/request-password.ftl", model);
  }

  @Override
  public void userCreation(String toEmail) {
    final var user = userRepository
      .findByEmail(toEmail)
      .orElseThrow(Exceptions.USER_NOT_FOUND::create);
    final var model = new HashMap<String, String>();
    model.put("pseudonym", user.getPseudonym());
    final var subject = "ParseMaster - Confirmation de création de compte";
    sendMail(toEmail, subject, "email/user-creation.ftl", model);
  }
}
