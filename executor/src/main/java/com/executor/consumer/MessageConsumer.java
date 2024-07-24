package com.executor.consumer;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Service;

import com.executor.service.CodeExecutorService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class MessageConsumer {
	
	private final CodeExecutorService executorService;
	
	@KafkaListener(topics="java")
	@SendTo
	public String  consume(String code) {
		String output =  executorService.execute(code).getConsoleOutput();
		return "output:"+output;
	}

}
