# File Service Client Application

This application will work as client application and it will access the REST API Server to upload, delete, download the files
this application can have multiple replicas, and may be this functionality can be implemented in the other application but it will be just use for as a service

for handling all the REST Request and Response in spring boot application we need help of RestTemplate class. by using this class we can hit the rest service and 
we can send the data to the rest service

As Our REST API is sending and receving the Files in text, xml, json any format so for that we need to implement HttpMessageConverter
I have created on ResponseConverter class for configuration of it

    package com.fileServiceApiClient.config;

    import java.util.List;
    
    import org.springframework.context.annotation.Bean;
    import org.springframework.context.annotation.Configuration;
    import org.springframework.http.converter.ByteArrayHttpMessageConverter;
    import org.springframework.http.converter.HttpMessageConverter;
    import org.springframework.web.client.RestTemplate;
    
    @Configuration
    public class ResponseConverter {
    
    	
    	@Bean
    	public RestTemplate restTemplate(List<HttpMessageConverter<?>> messageConverters) {
    	    return new RestTemplate(messageConverters);
    	}
    
    	@Bean
    	public ByteArrayHttpMessageConverter byteArrayHttpMessageConverter() {
    	    return new ByteArrayHttpMessageConverter();
    	}
    }


### Create a one controller class which will handle all the request from Front end application and gives the response back to the front end

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
    public class FileServiceController {
    	
    	private static final Logger logger = LoggerFactory.getLogger(FileServiceController.class);
    	
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



