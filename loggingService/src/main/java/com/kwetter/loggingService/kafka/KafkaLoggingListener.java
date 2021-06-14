package com.kwetter.loggingService.kafka;

import com.kwetter.loggingService.kafka.message.KafkaLoggingMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class KafkaLoggingListener {
    Logger logger = LoggerFactory.getLogger("KwetterLogs");

    @KafkaListener(topics = "logging", groupId = "loggingGroup", containerFactory = "loggingKafkaListenerContainerFactory")
    void listener(KafkaLoggingMessage message) {
        switch (message.getType()) {
            case INFO:
                logger.info("{} : [{}] : {}", message.getTimestamp().toString(), message.getServiceName(), message.getContent());
                break;
            case WARN:
                logger.warn("{} : [{}] : {}", message.getTimestamp().toString(), message.getServiceName(), message.getContent());
                break;
            case DEBUG:
                logger.debug("{} : [{}] : {}", message.getTimestamp().toString(), message.getServiceName(), message.getContent());
                break;
            case ERROR:
                logger.error("{} : [{}] : {}", message.getTimestamp().toString(), message.getServiceName(), message.getContent());
                break;
        }
    }
}
