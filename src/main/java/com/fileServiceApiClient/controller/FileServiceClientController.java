package com.fileServiceApiClient.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.fileServiceApiClient.config.RestAPIConfig;
import com.fileServiceApiClient.vo.StoreFileResponse;

@RestController
public class FileServiceClientController {
	
	private static final Logger logger = LoggerFactory.getLogger(FileServiceClientController.class);
	
	@Value("${BASE_URL}")
	private String BASE_URL;
	
	@Autowired
	RestAPIConfig restAPIConfig;
	
	@GetMapping("getFile/{fileName:.+}")
	public ResponseEntity<Resource> getFile(@PathVariable String fileName) {
		RestTemplate restTemplate = restAPIConfig.getRestTemplate();
		
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
		} catch (IOException ex) {
			logger.error("File Input Out Exception", ex);
			
		}
		return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType))
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file + "\"").body(resource);
	}
	
	/**
	 * Upload a new File in the File Storage using @PostMapping
	 * @param file
	 * @return
	 */
	@PostMapping("storeFile/")
	public StoreFileResponse storeFile(@RequestParam("file") MultipartFile file,@RequestParam("email") String email) {
		RestTemplate restTemplate= restAPIConfig.getRestTemplate();
		MultiValueMap<String, Object> data = new LinkedMultiValueMap<String, Object>();
		ByteArrayResource resource=null;
	try {		
			resource = new ByteArrayResource(file.getBytes()) {
			    @Override
			    public String getFilename() {
			        return file.getOriginalFilename();
			    }
			};
		} catch (IOException ex) {
			logger.error("Could not convert to Resource", ex);
		}
		data.add("file", resource);
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.setContentType(MediaType.MULTIPART_FORM_DATA);
		HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<MultiValueMap<String, Object>>(data, requestHeaders);
		StoreFileResponse storeFileResponse=restTemplate.postForObject(buildUrl("/api/upload/"), requestEntity, StoreFileResponse.class);

		return storeFileResponse;
	}
	
	public String buildUrl(String url) {
		StringBuilder urlBuilder=new StringBuilder();
		urlBuilder.append(BASE_URL);
		urlBuilder.append(url);
		return urlBuilder.toString();
	}

}
