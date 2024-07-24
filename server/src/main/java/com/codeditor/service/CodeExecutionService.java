package com.codeditor.service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.requestreply.RequestReplyFuture;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

@Service
public class CodeExecutionService {

	private final KafkaTemplate<String, String> kafkaTemplate;

	public CodeExecutionService(KafkaTemplate<String, String> kafkaTemplate) {
		this.kafkaTemplate = kafkaTemplate;
	}

	public Map<String, String> execute(String code) {
		try {
			Map<String,String> output = new HashMap<>();
			ProducerRecord<String, String> producerRecord = new ProducerRecord<String, String>("java", code);
			producerRecord.headers()
						.add(KafkaHeaders.REPLY_TOPIC,"java_reply".getBytes())
						.add(KafkaHeaders.CORRELATION_ID,UUID.randomUUID().toString().getBytes());
			CompletableFuture<SendResult<String, String>> sendRequest = kafkaTemplate.send(producerRecord);
			RecordMetadata metadata =  sendRequest.get().getRecordMetadata();
			ConsumerRecord<String,String> consumerRecord = kafkaTemplate.receive("java_reply", metadata.partition(), metadata.offset());
			output.put("output", consumerRecord.value());
			return output;
		} catch (Exception  e) {
			e.printStackTrace();
		}
		return null;
	}
}
