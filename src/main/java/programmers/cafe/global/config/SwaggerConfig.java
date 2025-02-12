package programmers.cafe.global.config;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.swagger.v3.oas.models.security.SecurityRequirement;


@Configuration
@OpenAPIDefinition(info = @Info(title = "[Programmers Devcourse BE4] Cafe", version = "v1"))
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT"
)
public class SwaggerConfig {

    @Bean
    public GroupedOpenApi groupMemberApi() {
        return GroupedOpenApi.builder()
                .group("member")
                .pathsToMatch("/auth/**")
                .build();
    }

    @Bean
    public GroupedOpenApi groupOrderApi() {
        return GroupedOpenApi.builder()
                .group("order for user")
                .pathsToMatch("/order")
                .pathsToExclude("/order/cancel/**", "/order/deliver")
                .build();
    }

    @Bean
    public GroupedOpenApi groupItemManageApi() {
        return GroupedOpenApi.builder()
                .group("manage items for admin")
                .pathsToMatch("/admin/**")
                .build();
    }

    @Bean
    public GroupedOpenApi groupOrderManageApi() {
        return GroupedOpenApi.builder()
                .group("order manage for admin")
                .pathsToMatch("/order/deliver" , "/order/cancel/**")
                .build();
    }

    @Bean
    public GroupedOpenApi groupMenuApi() {
        return GroupedOpenApi.builder()
                .group("show menu for user")
                .pathsToMatch("/user/**")
                .build();
    }

    @Bean
    public GroupedOpenApi allApi() {
        return GroupedOpenApi.builder()
                .group("apiV1")
                .pathsToMatch("/**")
                .build();
    }

//    private Info apiInfo() {
//        return new Info()
//                .title("[Programmers Devcourse BE4] Cafe") // API의 제목
//                .description("최재우 1차 프로젝트 - Toy Project") // API에 대한 설명
//                .version("1.0.0"); // API의 버전
//    }
//
//    @Bean
//    public OpenAPI customOpenAPI() {
//        final String securitySchemeName = "bearerAuth";
//        return new OpenAPI()
//                .info(apiInfo())
//                .components(new Components()
//                        .addSecuritySchemes(securitySchemeName,
//                                new SecurityScheme()
//                                        .name(securitySchemeName)
//                                        .type(SecurityScheme.Type.HTTP)
//                                        .scheme("bearer")
//                                        .bearerFormat("JWT")))
//                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName));
//    }
}
