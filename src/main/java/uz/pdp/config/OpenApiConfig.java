package uz.pdp.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI shopFlowOpenAPI() {
        final String securitySchemaName = "bearerAuth";

        return new OpenAPI()
                .info(new Info()
                        .title("ShopFlow E-Commerce API")
                        .description("Online do'kon backend tizimi REST API hujjatlari")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("ShopFlow Backend Team")
                                .email("abduqodirv1ch16@gmail.com")))
                .addSecurityItem(new SecurityRequirement().addList(securitySchemaName))
                .components(new Components()
                        .addSecuritySchemes(securitySchemaName, new SecurityScheme()
                                .name(securitySchemaName)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")));
    }
}
