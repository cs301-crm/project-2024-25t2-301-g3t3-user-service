package com.cs301.crm.producers;

import com.cs301.crm.protobuf.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Component
public class LogKafkaProducer {
    @Value("${kafka.topic.log}")
    private String logTopic;

    private final Logger logger = LoggerFactory.getLogger(LogKafkaProducer.class);
    private final KafkaTemplate<String, Log> kafkaTemplate;

    @Autowired
    public LogKafkaProducer(KafkaTemplate<String, Log> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void produceMessage(Log logMessage) {
        CompletableFuture<SendResult<String, Log>> future = kafkaTemplate.send(logTopic, logMessage);
        future.whenComplete( (result, ex) -> {
            if (ex == null) {
                logger.info("Sent message=[{}] with offset [{}]", logMessage.toString(), result.getRecordMetadata().offset());
            } else {
                logger.error("Unable to send message=[{}] due to [{}]", logMessage.toString(), ex.getMessage());
            }
        });
    }
}
