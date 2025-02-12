package programmers.cafe.global.config;

//import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.swagger.v3.oas.models.security.SecurityRequirement;


@Configuration
public class SwaggerConfig {
//    @Bean
//    public OpenAPI openAPI() {
//        return new OpenAPI()
//                .components(new Components())
//                .info(apiInfo());
//    }

    private Info apiInfo() {
        return new Info()
                .title("[Programmers Devcourse BE4] Cafe") // API의 제목
                .description("최재우 1차 프로젝트 - Toy Project") // API에 대한 설명
                .version("1.0.0"); // API의 버전
    }

    @Bean
    public OpenAPI customOpenAPI() {
        final String securitySchemeName = "bearerAuth";
        return new OpenAPI()
                .info(new Info().title("Your API Title")
                        .description("API Description")
                        .version("v1.0"))
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName,
                                new SecurityScheme()
                                        .name(securitySchemeName)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")))
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName));
    }
}
