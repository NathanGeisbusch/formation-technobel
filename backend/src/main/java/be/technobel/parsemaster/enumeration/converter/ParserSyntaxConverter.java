package be.technobel.parsemaster.enumeration.converter;

import be.technobel.parsemaster.enumeration.ParserSyntax;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class ParserSyntaxConverter implements Converter<String, ParserSyntax> {
  @Override
  public ParserSyntax convert(String source) {
    return ParserSyntax.fromString(source);
  }
}
