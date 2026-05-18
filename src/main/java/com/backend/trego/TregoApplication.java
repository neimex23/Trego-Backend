package com.backend.trego;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class TregoApplication {

	public static void main(String[] args) {
		SpringApplication.run(TregoApplication.class, args);
	}

}
