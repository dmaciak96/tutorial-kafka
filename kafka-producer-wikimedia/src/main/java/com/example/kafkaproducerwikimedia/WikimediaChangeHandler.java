package com.example.kafkaproducerwikimedia;

import com.launchdarkly.eventsource.MessageEvent;
import com.launchdarkly.eventsource.background.BackgroundEventHandler;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WikimediaChangeHandler implements BackgroundEventHandler {
    private static final Logger log = LoggerFactory.getLogger(WikimediaChangeHandler.class);
    private final KafkaProducer<String, String> producer;
    private final String topicName;

    public WikimediaChangeHandler(KafkaProducer<String, String> producer, String topicName) {
        this.producer = producer;
        this.topicName = topicName;
    }

    @Override
    public void onOpen() {
    }

    @Override
    public void onClosed() {
        log.info("Closing Kafka producer");
        producer.close();
    }

    @Override
    public void onMessage(String event, MessageEvent messageEvent) {
        log.info("Sending event to Kafka topic {}", topicName);
        producer.send(new ProducerRecord<>(topicName, messageEvent.getData()));
    }

    @Override
    public void onComment(String comment) {

    }

    @Override
    public void onError(Throwable t) {
        log.error("Error in stream reading", t);
    }
}
