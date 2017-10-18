package io.pivotal.mday.c2c.frontend;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.netflix.discovery.EurekaClient;

@SpringBootApplication
@EnableEurekaClient
@RestController
public class FrontendApplication {

	@Value("${c2c.client}")
	private String c2cClient;

	private EurekaClient discoveryClient;
	private RestTemplate restTemplate;

	public FrontendApplication(EurekaClient discoveryClient) {
		this.discoveryClient = discoveryClient;
		this.restTemplate = new RestTemplate();
	}

	public static void main(String[] args) {
		SpringApplication.run(FrontendApplication.class, args);
	}

	/*
	 * Originally used Hysterix but for demo purposes we just catch the exception so
	 * refreshes are more rapid
	 */
	@GetMapping("/")
	public String connectMe() {
		try {
			final String url = this.discoveryClient.getNextServerFromEureka(c2cClient, false).getHomePageUrl();
			return restTemplate.getForObject(url, String.class);
		} catch (Exception e) {
			return "Could not reach client (networking problem?)";
		}
	}
}
