package com.lalala.spring.trvapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.TimeZone;

//@SpringBootApplication(exclude={DataSourceAutoConfiguration.class})
@SpringBootApplication()
@EnableJpaAuditing
public class TrvappApplication {

	@PostConstruct
	void started(){
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
		LocalDateTime.now(ZoneId.of("Asia/Seoul"));
	}
	public static void main(String[] args) {
		SpringApplication.run(TrvappApplication.class, args);
	}

}
