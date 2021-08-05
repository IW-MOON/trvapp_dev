package com.lalala.spring.trvapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

//@SpringBootApplication(exclude={DataSourceAutoConfiguration.class})
@SpringBootApplication()
@EnableJpaAuditing
public class TrvappApplication {

	//static {

		//System.out.println(System.getProperty("docker-compose.yml"));
		//System.setProperty("spring.config.location", "file:/application.yml");
		//System.setProperty("spring.config.location", "classpath:/application.yml");
	//}

	public static void main(String[] args) {
		SpringApplication.run(TrvappApplication.class, args);
	}

}
