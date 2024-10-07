package com.quest.etna;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

//@SpringBootApplication
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;

//@SpringBootApplication(scanBasePackages = {"com.quest.etna.config", "com.quest.etna.repositories"})
@SpringBootApplication
public class QuestWebJavaApplication {
	public static void main(String[] args) {
		SpringApplication.run(QuestWebJavaApplication.class, args);
	}

}

