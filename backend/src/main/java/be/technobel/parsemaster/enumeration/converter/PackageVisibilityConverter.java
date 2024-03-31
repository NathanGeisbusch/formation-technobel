package be.technobel.parsemaster.enumeration.converter;

import be.technobel.parsemaster.enumeration.PackageVisibility;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class PackageVisibilityConverter implements Converter<String, PackageVisibility> {
  @Override
  public PackageVisibility convert(String source) {
    return PackageVisibility.fromString(source);
  }
}
