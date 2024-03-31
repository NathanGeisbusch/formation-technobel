package be.technobel.parsemaster.openapi;

import be.technobel.parsemaster.enumeration.UserRole;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.customizers.GlobalOpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import java.util.*;

@Configuration
@Profile({"!prod"})
public class SwaggerConfig {
  @Bean
  public OpenAPI openAPI() {
    return security(new OpenAPI())
      .info(new Info()
        .title("ParseMaster")
        .description("Application de parsing et de génération de code")
        .version("0.0.1")
      );
  }

  private OpenAPI security(OpenAPI openAPI) {
    final var components = new Components();
    Arrays.stream(UserRole.values()).map(Enum::toString).forEach(scheme -> {
      openAPI.addSecurityItem(new SecurityRequirement().addList(scheme));
      components.addSecuritySchemes(scheme, new SecurityScheme()
        .name(scheme)
        .type(SecurityScheme.Type.HTTP)
        .scheme("bearer")
        .bearerFormat("JWT")
      );
    });
    return openAPI.components(components);
  }

  @Bean
  public GlobalOpenApiCustomizer customizeOpenAPI() {
    return openApi -> {
      openApi.getServers().get(0).setDescription("ParseMaster API");
      openApi.getComponents().setSchemas(new TreeMap<>(openApi.getComponents().getSchemas()));
    };
  }
}
