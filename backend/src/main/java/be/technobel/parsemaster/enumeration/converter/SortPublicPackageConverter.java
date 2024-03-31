package be.technobel.parsemaster.enumeration.converter;

import be.technobel.parsemaster.enumeration.SortPublicPackage;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class SortPublicPackageConverter implements Converter<String, SortPublicPackage> {
  @Override
  public SortPublicPackage convert(String source) {
    return SortPublicPackage.fromString(source);
  }
}
