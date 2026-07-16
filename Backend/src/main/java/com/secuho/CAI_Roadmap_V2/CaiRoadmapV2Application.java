package com.secuho.CAI_Roadmap_V2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class CaiRoadmapV2Application {

	public static void main(String[] args) {
		SpringApplication.run(CaiRoadmapV2Application.class, args);
	}

}
