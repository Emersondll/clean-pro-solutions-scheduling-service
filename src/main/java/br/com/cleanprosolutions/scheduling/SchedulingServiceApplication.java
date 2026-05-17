package br.com.cleanprosolutions.scheduling;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * Main entry point for the Scheduling Service.
 *
 * <p>Manages the service appointments for Clean Pro Solutions,
 * exposing a REST API and communicating via RabbitMQ.</p>
 *
 * @author Emerson Lima
 * @version 1.0.0
 * @since 1.0.0
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class SchedulingServiceApplication {

    public static void main(final String[] args) {
        SpringApplication.run(SchedulingServiceApplication.class, args);
    }
}
