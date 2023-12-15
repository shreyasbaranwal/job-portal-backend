package org.job.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;

@SpringBootApplication
@OpenAPIDefinition(
		info = @Info(
				title = "Spring Boot JobPortal Documentation",
				description = "Spring Boot REST API Documentation",
				version = "v1.0",
				contact = @Contact(
						name = "Chittaranjan Ghosh",
						email = "chittaranjanghosh7@gmail.com",
						url = "https://www.test.net"
				),
				license = @License(
						name = "Apache 2.0",
						url = "https://www.test.net"
				)
		)
)
public class JobPortalAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(JobPortalAppApplication.class, args);
	}

    
}
