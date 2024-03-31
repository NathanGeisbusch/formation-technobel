package be.technobel.parsemaster.enumeration.converter;

import be.technobel.parsemaster.enumeration.UserRole;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class UserRoleConverter implements Converter<String, UserRole> {
  @Override
  public UserRole convert(String source) {
    return UserRole.fromString(source);
  }
}
