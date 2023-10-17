package com.example.kafkaproducerwikimedia;

import com.launchdarkly.eventsource.EventSource;
import com.launchdarkly.eventsource.background.BackgroundEventSource;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

public class WikimediaChangesProducer {
    private static final Logger log = LoggerFactory.getLogger(WikimediaChangesProducer.class);
    private static final String BOOTSTRAP_SERVERS = "bootstrap.servers";
    private static final String KEY_SERIALIZER = "key.serializer";
    private static final String VALUE_SERIALIZER = "value.serializer";
    private static final String KAFKA_HOST = "127.0.0.1";
    private static final String KAFKA_PORT = "9092";
    private static final String TOPIC_NAME = "wikimedia.recentchange";
    private static final String URL = "https://stream.wikimedia.org/v2/stream/recentchange";

    public static void main(String[] args) throws InterruptedException {
        log.info("Creating Producer properties");
        var properties = new Properties();
        properties.setProperty(BOOTSTRAP_SERVERS, "%s:%s".formatted(KAFKA_HOST, KAFKA_PORT));
        properties.setProperty(KEY_SERIALIZER, StringSerializer.class.getName());
        properties.setProperty(VALUE_SERIALIZER, StringSerializer.class.getName());

        var producer = new KafkaProducer<String, String>(properties);
        var eventHandler = new WikimediaChangeHandler(producer, TOPIC_NAME);
        var eventSource = new BackgroundEventSource.Builder(eventHandler, new EventSource.Builder(URI.create(URL))).build();

        eventSource.start();
        TimeUnit.MINUTES.sleep(1);
        eventSource.close();
    }
}
