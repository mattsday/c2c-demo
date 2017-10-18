package io.pivotal.mday.c2c.frontend;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.netflix.discovery.EurekaClient;

@SpringBootApplication
@EnableEurekaClient
@RestController
public class FrontendApplication {

	@Value("${c2c.backend}")
	private String c2cBackend;

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
	 * Connect to a service directly (external to foundation)
	 */
	@GetMapping("/remote/frontend")
	public String connectRemoteFrontend(String url) {
		try {
			return restTemplate.getForObject(url, String.class);
		} catch (Exception e) {
			return "Could not reach url " + url + " (networking problem?)";
		}
	}

	/*
	 * Connect to a service indirectly via backend (external to foundation)
	 */
	@GetMapping("/remote/backend")
	public String connectRemoteBackend(@RequestParam("url") String query) {
		try {
			final String url = this.discoveryClient.getNextServerFromEureka(c2cBackend, false).getHomePageUrl()
					+ "/remote?url=" + query;
			return restTemplate.getForObject(url, String.class);
		} catch (Exception e) {
			return "Could not reach url " + query + " (networking problem?)";
		}
	}

	/*
	 * Attempt to connect to backend
	 */
	@GetMapping("/ping")
	public String connectMe() {
		try {
			final String url = this.discoveryClient.getNextServerFromEureka(c2cBackend, false).getHomePageUrl()
					+ "/ping";
			return restTemplate.getForObject(url, String.class);
		} catch (Exception e) {
			return "Could not reach client (networking problem?)";
		}
	}
}
