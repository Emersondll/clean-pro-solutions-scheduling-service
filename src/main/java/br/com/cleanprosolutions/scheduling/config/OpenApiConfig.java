package br.com.cleanprosolutions.scheduling.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI / Swagger UI configuration for the scheduling service.
 *
 * @author Clean Pro Solutions Team
 * @since 1.0.0
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI schedulingServiceOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Clean Pro Solutions — Scheduling Service API")
                        .description("Service scheduling management with optimistic locking.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Clean Pro Solutions Team")
                                .url("https://cleanprosolutions.com.br"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0")));
    }
}
