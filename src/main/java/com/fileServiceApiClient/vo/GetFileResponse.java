package com.fileServiceApiClient.vo;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class GetFileResponse  extends ResponseEntity<Resource> {

	public GetFileResponse(HttpStatus status) {
		super(status);
		// TODO Auto-generated constructor stub
	}

}
