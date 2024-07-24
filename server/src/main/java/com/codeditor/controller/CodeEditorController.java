package com.codeditor.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class CodeEditorController {
	
	@MessageMapping("/codeChange")
	@SendTo("/topic/codeUpdate")
	public String  handleCodeChange(String code)  {
		return code;
	}

}
