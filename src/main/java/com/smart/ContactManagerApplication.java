package com.smart;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

@SpringBootApplication
@EnableWebSecurity
@ServletComponentScan
public class ContactManagerApplication {
	public static void main(String[] args) {
		SpringApplication.run(ContactManagerApplication.class, args);
	}
}
