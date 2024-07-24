package com.executor.dto;

import lombok.Data;

@Data
public class Result {
	private Object output;
	private String consoleOutput;
	private String error;
}
