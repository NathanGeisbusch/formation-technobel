package be.technobel.parsemaster.enumeration.converter;

import be.technobel.parsemaster.enumeration.SortPrivatePackage;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class SortPrivatePackageConverter implements Converter<String, SortPrivatePackage> {
  @Override
  public SortPrivatePackage convert(String source) {
    return SortPrivatePackage.fromString(source);
  }
}
