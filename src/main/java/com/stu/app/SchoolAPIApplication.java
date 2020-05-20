package com.stu.app;


import java.util.TimeZone;

import javax.annotation.PostConstruct;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class SchoolAPIApplication {

	public static void main(String[] args) {
		SpringApplication.run(SchoolAPIApplication.class, args);
	}
	
	 @PostConstruct
	    void init() {
	        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
	    }

	    /**
	     * This will be used to connect to any other external services
	     */
	    @Bean
	    public RestTemplate getRestTemplate() {
	        return new RestTemplate();
	    }
}
