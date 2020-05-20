package com.practice.arch.commonarch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.filter.CommonsRequestLoggingFilter;

@SpringBootApplication
public class CommonArchApplication {

	public static void main(String[] args) {
		SpringApplication.run(CommonArchApplication.class, args);
	}


}
