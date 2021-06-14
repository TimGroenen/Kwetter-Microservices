package com.kwetter.loggingService.kafka.message;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class KafkaLoggingMessage {
    private String serviceName;
    private Date timestamp;
    private String content;
    private KafkaLoggingType type;

    public KafkaLoggingMessage() {}

    public KafkaLoggingMessage(String serviceName, String content, KafkaLoggingType type) {
        this.serviceName = serviceName;
        this.timestamp = new Date();
        this.content = content;
        this.type = type;
    }
}
