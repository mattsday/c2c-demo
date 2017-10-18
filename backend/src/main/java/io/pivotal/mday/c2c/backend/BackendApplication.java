package io.pivotal.mday.c2c.backend;

import java.net.UnknownHostException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@RestController
@EnableEurekaClient
public class BackendApplication {

	@Autowired
	Environment environment;

	public static void main(String[] args) {
		SpringApplication.run(BackendApplication.class, args);
	}

	@GetMapping("/ping")
	public String getMe() throws UnknownHostException {
		return "Remote hello from " + environment.getProperty("spring.application.name") + " (IP: "
				+ environment.getProperty("spring.cloud.client.ipAddress") + ":"
				+ environment.getProperty("local.server.port") + ")";
	}

	/*
	 * Connect to a service directly (external to foundation)
	 */
	@GetMapping("/remote")
	public String connectRemoteFrontend(String url) {
		RestTemplate restTemplate = new RestTemplate();
		try {
			return restTemplate.getForObject(url, String.class);
		} catch (Exception e) {
			return "Could not reach url " + url + " (networking problem?)";
		}
	}

}
