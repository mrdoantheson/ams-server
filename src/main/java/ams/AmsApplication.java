package ams;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;


@SpringBootApplication
@EnableJpaAuditing(auditorAwareRef = "appAuditorAware")
public class AmsApplication {

	public static void main(String[] args) {

		SpringApplication.run(AmsApplication.class, args);

	}



}
