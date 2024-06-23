package com.bardev.CarRegistry;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication
public class CarRegistryApplication {

	public static void main(String[] args) {
		SpringApplication.run(CarRegistryApplication.class, args);
		log.info("Registry car system works !!!");
	}

}
