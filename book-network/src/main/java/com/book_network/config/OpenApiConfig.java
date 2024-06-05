package com.book_network.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;

@OpenAPIDefinition(
		
		info = @Info(
				contact = @Contact(
						name = "T Pavan Kalyan",
						email = "tpavankalyan77@gmail.com",
						url = "https://www.linkedin.com/in/t-pavan-kalyan/"
						),
				description = "Open API documentation for Spring Security",
				title = "OpenApi specification - T Pavan Kalyan",
				version = "1.0",
				license = @License(
							name = "License Name",
							url = "https://some-url.com"
						),
				termsOfService = "Terms of services"
				
				),
			servers = {
					@Server(
							description = "Local ENV",
							url = "http://localhost:8088/api/v1"
					),
					@Server(
							description = "PROD ENV",
							url = "https://some-url.com"
					)
			},
			security = {
					@SecurityRequirement(
								name = "bearerAuth"
							)
			}
		)
@SecurityScheme(
		name = "bearerAuth",
		description = "JWT Auth description",
		scheme = "bearer",
		type = SecuritySchemeType.HTTP,
		bearerFormat = "JWT",
		in = SecuritySchemeIn.HEADER
	)
public class OpenApiConfig {

}
