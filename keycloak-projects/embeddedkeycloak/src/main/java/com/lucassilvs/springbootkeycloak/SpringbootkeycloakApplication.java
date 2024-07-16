package com.lucassilvs.springbootkeycloak;

import com.lucassilvs.springbootkeycloak.config.KeycloakServerProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication(exclude = LiquibaseAutoConfiguration.class)
@EnableConfigurationProperties({KeycloakServerProperties.class})
public class SpringbootkeycloakApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringbootkeycloakApplication.class, args);
	}

}
