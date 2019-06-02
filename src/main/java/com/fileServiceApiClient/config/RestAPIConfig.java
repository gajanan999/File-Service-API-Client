package com.fileServiceApiClient.config;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class RestAPIConfig {

	public RestTemplate restTemplate=null;
	
	/**
	 * Created only one Rest Template for Spring container
	 * @return
	 */
	public RestTemplate getRestTemplate() {
		if(null == restTemplate) {
			restTemplate = new RestTemplate();
		}
		return restTemplate;
	}
}
