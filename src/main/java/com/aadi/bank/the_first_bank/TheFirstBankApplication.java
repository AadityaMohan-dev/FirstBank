package com.aadi.bank.the_first_bank;

import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition(info = @Info(
		title = "First Bank App",
		description = "Backend Rest API's for First Bank App",
		version = "v1",
		contact = @Contact(
				name = "Aaditya Mohan",
				email = "aadimohan51@gmail.com",
				url = "https://github.com/AadityaMohan-dev"
		),
		license =@License(
				name = "First Bank",
				url = "https://github.com/AadityaMohan-dev/FirstBank"
		)
),
		externalDocs = @ExternalDocumentation(
				description = "First Bank Web Application Backend By Aaditya Mohan",
				url = "https://github.com/AadityaMohan-dev/FirstBank"
		)
)
public class TheFirstBankApplication {

	public static void main(String[] args) {
		SpringApplication.run(TheFirstBankApplication.class, args);
	}

}
