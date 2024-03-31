package be.technobel.parsemaster.enumeration.converter;

import be.technobel.parsemaster.enumeration.GeneratorSyntax;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class GeneratorSyntaxConverter implements Converter<String, GeneratorSyntax> {
  @Override
  public GeneratorSyntax convert(String source) {
    return GeneratorSyntax.fromString(source);
  }
}
