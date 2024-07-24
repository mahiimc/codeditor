package com.codeditor.config;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

@Configuration
public class KafkaProducerConfig {

	@Value("${spring.kafka.bootstrap-servers}")
	private String bootstrapServers;

	@Value("${spring.kafka.consumer.group-id}")
	private String groupId;
	
	@Bean
	public ReplyingKafkaTemplate<String, String, String> replyKafkaTemplate(ProducerFactory<String, String> producerFactory , 
			KafkaMessageListenerContainer<String, String> listenerContainer ) {
		return new ReplyingKafkaTemplate<String, String, String>(producerFactory, listenerContainer);
	}
	
	@Bean
	public KafkaMessageListenerContainer<String, String> replyContainer(ConsumerFactory<String, String> cf) {
	  ContainerProperties containerProperties = new ContainerProperties("java_reply");
	  return new KafkaMessageListenerContainer<>(cf, containerProperties);
	}
	
	@Bean
	public ConsumerFactory<String, String> consumerFactory() {
		Map<String, Object> props = new HashMap<>();
		props.put("bootstrap.servers", bootstrapServers);
		props.put("group.id", groupId);
		props.put("key.deserializer", StringDeserializer.class);
		props.put("value.deserializer", StringDeserializer.class);
		props.put("value.deserializer.delegate.class", StringDeserializer.class);
		return new DefaultKafkaConsumerFactory<>(props);
	}

	@Bean
	public ProducerFactory<String, String> producerFactory() {
		Map<String, Object> props = new HashMap<>();
		props.put("bootstrap.servers", bootstrapServers);
//		props.put("group.id", groupId);
		props.put("key.deserializer", ErrorHandlingDeserializer.class);
		props.put("key.serializer", StringSerializer.class);
		props.put("value.serializer", JsonSerializer.class);
		props.put("value.deserializer", ErrorHandlingDeserializer.class);
//		props.put("value.deserializer.delegate.class", JsonDeserializer.class);
		return new DefaultKafkaProducerFactory<>(props);
	}

	
	@Bean
	public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, String>> kafkaListenerContainerFactory() {
	  ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
	  factory.setConsumerFactory(consumerFactory());
	  factory.setReplyTemplate(kafkaTemplate());
	  return factory;
	}
	
	
	@Bean
	public KafkaTemplate<String, String> kafkaTemplate() {
		KafkaTemplate<String, String> kafkaTemplate= new KafkaTemplate<String, String>(producerFactory());
		kafkaTemplate.setConsumerFactory(consumerFactory());
		return kafkaTemplate;
	}
}
