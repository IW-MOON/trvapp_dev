package com.lalala.spring.trvapp;

import org.apache.tomcat.jni.Local;
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

	//static {

		//System.out.println(System.getProperty("docker-compose.yml"));
		//System.setProperty("spring.config.location", "file:/application.yml");
		//System.setProperty("spring.config.location", "classpath:/application.yml");
	//}

	@PostConstruct
	void started(){
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
		//LocalDateTime.now("UTC");
		LocalDateTime.now(ZoneId.of("Asia/Seoul"));
	}

	public static void main(String[] args) {
		SpringApplication.run(TrvappApplication.class, args);
	}

}
