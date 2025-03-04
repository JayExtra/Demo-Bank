package com.dev.james.Demo.Bank

import com.dev.james.Demo.Bank.dto.BankResponse
import io.swagger.v3.oas.annotations.ExternalDocumentation
import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.info.Contact
import io.swagger.v3.oas.annotations.info.Info
import io.swagger.v3.oas.annotations.info.License
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Bean

@SpringBootApplication
@OpenAPIDefinition(
		info =@Info(
				title = "Demo Bank App" ,
				description =  "Backend Rest APIs for Bank XYZ.",
				version = "v1.0",
				contact = @Contact(
						name = "James Gitonga",
						email = "murokijames21@gmail.com" ,
						url = ""
				),
				license = @License(
						name = "Jay's creations",
						url = ""
				)
		),
		externalDocs = @ExternalDocumentation(
				description = "Demo Bank App documentation.",
				url=""
		)
)
class DemoBankApplication {


	static void main(String[] args) {
		SpringApplication.run(DemoBankApplication, args)
	}

}
