package com.stratio.casemanagement.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger.web.OperationsSorter;
import springfox.documentation.swagger.web.UiConfiguration;
import springfox.documentation.swagger.web.UiConfigurationBuilder;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerConfiguration {

    public static final String API_PREFIX = "/api";

    /*
     * Creates API documentation
     */
    @Bean
    public Docket productApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.stratio.casemanagement"))
                .paths(PathSelectors.any())
                .build()
                .apiInfo(apiInfo());
    }

    @Bean
    public UiConfiguration uiConfiguration() {
        return UiConfigurationBuilder.builder()
                .deepLinking(false)
                .defaultModelsExpandDepth(1)
                .defaultModelExpandDepth(1)
                .displayRequestDuration(true)
                .operationsSorter(OperationsSorter.METHOD)
                .build();
    }

    /*
     * Creates info for API documentation
     */
    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("Case Management | API Documentation")
                .description("API Documentation with Swagger")
                .contact(new Contact("Stratio", "", ""))
                .build();
    }
}
