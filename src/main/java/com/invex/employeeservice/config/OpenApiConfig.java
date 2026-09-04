package com.invex.employeeservice.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Employee Service API",
                version = "1.0.0",
                description = "REST API for employee management"
        )
)
public class OpenApiConfig {
}