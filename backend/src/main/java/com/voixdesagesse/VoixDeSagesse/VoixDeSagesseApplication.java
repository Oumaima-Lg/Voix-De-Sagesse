package com.voixdesagesse.VoixDeSagesse;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class VoixDeSagesseApplication {

	public static void main(String[] args) {
		SpringApplication.run(VoixDeSagesseApplication.class, args);
	}

}

