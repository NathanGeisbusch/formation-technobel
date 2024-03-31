package be.technobel.parsemaster.config;

import be.technobel.parsemaster.service.declaration.CompressionService;
import be.technobel.parsemaster.service.implementation.GzipCompressionService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Config {
  /** Maximum amount of bytes allowed for file upload. */
  public static final long FILE_UPLOAD_SIZE_LIMIT = 256_000;

  @Bean
  public CompressionService compression() {
    return new GzipCompressionService();
  }
}
