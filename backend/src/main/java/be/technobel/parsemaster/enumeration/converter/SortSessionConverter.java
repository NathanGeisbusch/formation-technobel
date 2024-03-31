package be.technobel.parsemaster.enumeration.converter;

import be.technobel.parsemaster.enumeration.SortSession;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class SortSessionConverter implements Converter<String, SortSession> {
  @Override
  public SortSession convert(String source) {
    return SortSession.fromString(source);
  }
}
