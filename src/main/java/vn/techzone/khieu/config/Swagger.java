package vn.techzone.khieu.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.HandlerMethod;

import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.customizers.OperationCustomizer;
import vn.techzone.khieu.utils.annotation.ApiMessage;

@Configuration
public class Swagger {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("TechZone API")
                        .version("1.0")
                        .description("Tài liệu API cho hệ thống E-commerce TechZone"))
                // Bật tính năng Security (nút Authorize) cho toàn bộ API
                .addSecurityItem(new SecurityRequirement().addList("Bearer Authentication"))
                .components(new Components().addSecuritySchemes("Bearer Authentication", createAPIKeyScheme()));
    }

    // Cấu hình định dạng Token (JWT)
    private SecurityScheme createAPIKeyScheme() {
        return new SecurityScheme().type(SecurityScheme.Type.HTTP)
                .bearerFormat("JWT")
                .scheme("bearer");
    }

    @Bean
    public OperationCustomizer apiMessageOperationCustomizer() {
        return (Operation operation, HandlerMethod handlerMethod) -> {
            ApiMessage apiMessage = handlerMethod.getMethodAnnotation(ApiMessage.class);
            if (apiMessage != null) {
                operation.setSummary(apiMessage.value());
                operation.setDescription(apiMessage.value());
            }
            return operation;
        };
    }
}
