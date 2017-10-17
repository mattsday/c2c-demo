package io.pivotal.mday.c2c.client;

import java.net.UnknownHostException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
@EnableEurekaClient
public class ClientApplication {

	@Autowired
	Environment environment;

	public static void main(String[] args) {
		SpringApplication.run(ClientApplication.class, args);
	}

	@GetMapping("/")
	public String getMe() throws UnknownHostException {
		return "Remote hello from " + environment.getProperty("spring.application.name") + " (IP: "
				+ environment.getProperty("spring.cloud.client.ipAddress") + ":"
				+ environment.getProperty("local.server.port") + ")";
	}

}
