package com.lee.robot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class QwRobotApplication {

	public static void main(String[] args) {
		SpringApplication.run(QwRobotApplication.class, args);
	}

}
