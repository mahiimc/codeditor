package com.codeditor.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.codeditor.dto.CompilationRequest;
import com.codeditor.service.CodeExecutionService;

@RestController
@RequestMapping("/api/v1/code")
@CrossOrigin("*")
public class CodeExecutionController {

	@Autowired
	private CodeExecutionService executionService;
	
	@PostMapping("/")
	public ResponseEntity<Map<String, Object>> execute(@RequestBody CompilationRequest request) {
		String code = request.code();
		Map<String, Object> response = new HashMap<>();
		Map<String, String> result = executionService.execute(code);
		response.put("result", result);
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}
}
