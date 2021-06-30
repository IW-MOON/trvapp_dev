package com.trv.trvapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude={DataSourceAutoConfiguration.class})
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
