package com.ashish.clubs.services.clubservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.kafka.annotation.EnableKafka;

/**
 * Club Service Application entry point.
 * Enables service discovery and Kafka messaging.
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableKafka
public class ClubServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ClubServiceApplication.class, args);
    }
}

