package com.ridetogether.server;

import jakarta.annotation.PostConstruct;
import java.util.Date;
import java.util.TimeZone;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
@ConfigurationPropertiesScan
public class ServerApplication {

	@PostConstruct
	public void started() {
		TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"));
		System.out.println("현재 시각 = " + new Date());
	}

	public static void main(String[] args) {
		SpringApplication.run(ServerApplication.class, args);
	}

}
