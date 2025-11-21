package com.example.taskmanager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SimpleTaskApplication {

	public static void main(String[] args) {
		SpringApplication.run(SimpleTaskApplication.class, args);
	}

}
