package be.technobel.parsemaster.service.declaration;

import be.technobel.parsemaster.dto.AccountDTO;
import be.technobel.parsemaster.entity.User;
import be.technobel.parsemaster.form.AccountForm;
import be.technobel.parsemaster.form.RegisterForm;

public interface UserConverter {
  AccountDTO toAccountDTO(User user);

  void fromAccountForm(User user, AccountForm form);

  User fromRegisterForm(RegisterForm form);
}
