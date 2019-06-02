package com.fileServiceApiClient.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class FileServiceClientController {
	
	private static final Logger logger = LoggerFactory.getLogger(FileServiceClientController.class);
	
	@Value("${BASE_URL}")
	private String BASE_URL;
	
	@GetMapping("getFile/{fileName:.+}")
	public ResponseEntity<Resource> getFile(@PathVariable String fileName) {
		RestTemplate restTemplate = new RestTemplate();
		
		byte[] response=  restTemplate.getForObject(buildUrl("/api/downloadFile/"+fileName), byte[].class);
		logger.info(response.toString());
		
		logger.info(response.getClass().getSimpleName());
		//GetFileResponse getFileResponse = restTemplate.getForObject(buildUrl("/api/downloadFile/"+fileName), GetFileResponse.class);
		String contentType="image/jpeg";
		//String file="";
		File file=new File(fileName);
		Resource resource=null;
		Path path = Paths.get(file.getAbsolutePath());
		try {
			Files.write(path, response);
			resource= new UrlResource(path.toUri());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType))
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file + "\"").body(resource);
	}
	
	public String buildUrl(String url) {
		StringBuilder urlBuilder=new StringBuilder();
		urlBuilder.append(BASE_URL);
		urlBuilder.append(url);
		return urlBuilder.toString();
	}

}
