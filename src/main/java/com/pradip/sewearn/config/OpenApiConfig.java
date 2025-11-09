package com.pradip.sewearn.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI sewEarnOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("SewEarn API")
                        .description("API documentation for the Sewing Earnings Management System")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Pradip")
                                .email("chavdapradip48@gmail.com")
                                .url("https://github.com/chavdapradip48"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0.html"))
                );
    }
}