package com.dbserver.crud_curso.infra.springdoc;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;

@Configuration
public class SpringDocConfiguration {
    @Bean
    OpenAPI customOpenAPI() {
        return new OpenAPI()
                .components(new Components()
                        .addSecuritySchemes("bearer-key",
                                new SecurityScheme().type(SecurityScheme.Type.HTTP).scheme("bearer")
                                        .bearerFormat("JWT")))
                        .info(new Info()
                                .title("Crud Curso Ar")
                                .description("Atividade proposta pela Unidade Ar da DB, onde foi desenvolvido um CRUD.")
                                .contact(new Contact()
                                    .name("Pedro Cecilio")
                                    .email("pedro.cecilio@dbserver.com.br"))
                        );   
                                
    }
}