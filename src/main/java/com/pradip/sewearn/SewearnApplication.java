package com.pradip.sewearn;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SewearnApplication {

	public static void main(String[] args) {
		SpringApplication.run(SewearnApplication.class, args);
	}

}
