package com.app.Iot_weather;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class IotWeatherApplication {


	public static void main(String[] args) {
		SpringApplication.run(IotWeatherApplication.class, args);

	}

}


